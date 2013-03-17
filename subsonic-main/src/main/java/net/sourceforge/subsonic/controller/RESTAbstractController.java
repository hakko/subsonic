package net.sourceforge.subsonic.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.PlayerTechnology;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.util.StringUtil;
import net.sourceforge.subsonic.util.XMLBuilder;
import net.sourceforge.subsonic.util.XMLBuilder.Attribute;

import org.springframework.web.bind.ServletRequestUtils;

public abstract class RESTAbstractController {

    protected static final Logger LOG = Logger.getLogger(RESTController.class);

    protected PlayerService playerService;

    protected HttpServletRequest wrapRequest(HttpServletRequest request) {
        return wrapRequest(request, false);
    }

    protected HttpServletRequest wrapRequest(final HttpServletRequest request, boolean jukebox) {
        final String playerId = createPlayerIfNecessary(request, jukebox);
        return new HttpServletRequestWrapper(request) {
            @Override
            public String getParameter(String name) {

                // Renames "id" request parameter to "path".
                if ("path".equals(name)) {
                    try {
                        return StringUtil.utf8HexDecode(request.getParameter("id"));
                    } catch (Exception e) {
                        return null;
                    }
                }

                // Returns the correct player to be used in PlayerService.getPlayer()
                else if ("player".equals(name)) {
                    return playerId;
                }

                return super.getParameter(name);
            }
        };
    }

    protected String getErrorMessage(Throwable t) {
        if (t.getMessage() != null) {
            return t.getMessage();
        }
        return t.getClass().getSimpleName();
    }

    public static void error(HttpServletRequest request, HttpServletResponse response, ErrorCode code, String message) throws IOException {
        XMLBuilder builder = createXMLBuilder(request, response, false);
        builder.add("error", true,
                new XMLBuilder.Attribute("code", code.getCode()),
                new XMLBuilder.Attribute("message", message));
        builder.end();
        response.getWriter().print(builder);
    }

    protected static XMLBuilder createXMLBuilder(HttpServletRequest request, HttpServletResponse response, boolean ok) throws IOException {
        String format = ServletRequestUtils.getStringParameter(request, "f", "xml");
        boolean json = "json".equals(format);
        boolean jsonp = "jsonp".equals(format);
        XMLBuilder builder;

        response.setCharacterEncoding(StringUtil.ENCODING_UTF8);

        if (json) {
            builder = XMLBuilder.createJSONBuilder();
            response.setContentType("application/json");
        } else if (jsonp) {
            builder = XMLBuilder.createJSONPBuilder(request.getParameter("callback"));
            response.setContentType("text/javascript");
        } else {
            builder = XMLBuilder.createXMLBuilder();
            response.setContentType("text/xml");
        }

        builder.preamble(StringUtil.ENCODING_UTF8);
        builder.add("subsonic-response", false,
                new Attribute("xmlns", "http://subsonic.org/restapi"),
                new Attribute("status", ok ? "ok" : "failed"),
                new Attribute("version", StringUtil.getRESTProtocolVersion()));
        return builder;
    }

    protected String createPlayerIfNecessary(HttpServletRequest request, boolean jukebox) {
        String username = request.getRemoteUser();
        String clientId = request.getParameter("c");
        if (jukebox) {
            clientId += "-jukebox";
        }

        List<Player> players = playerService.getPlayersForUserAndClientId(username, clientId);

        // If not found, create it.
        if (players.isEmpty()) {
            Player player = new Player();
            player.setIpAddress(request.getRemoteAddr());
            player.setUsername(username);
            player.setClientId(clientId);
            player.setName(clientId);
            player.setTechnology(jukebox ? PlayerTechnology.JUKEBOX : PlayerTechnology.EXTERNAL_WITH_PLAYLIST);
            playerService.createPlayer(player);
            players = playerService.getPlayersForUserAndClientId(username, clientId);
        }

        // Return the player ID.
        return !players.isEmpty() ? players.get(0).getId() : null;
    }

    public static enum ErrorCode {

        GENERIC(0, "A generic error."),
        MISSING_PARAMETER(10, "Required parameter is missing."),
        PROTOCOL_MISMATCH_CLIENT_TOO_OLD(20, "Incompatible Subsonic REST protocol version. Client must upgrade."),
        PROTOCOL_MISMATCH_SERVER_TOO_OLD(30, "Incompatible Subsonic REST protocol version. Server must upgrade."),
        NOT_AUTHENTICATED(40, "Wrong username or password."),
        NOT_AUTHORIZED(50, "User is not authorized for the given operation."),
        NOT_LICENSED(60, "The trial period for the Subsonic server is over. Please donate to get a license key. Visit subsonic.org for details."),
        NOT_FOUND(70, "Requested data was not found.");

        private final int code;
        private final String message;

        ErrorCode(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

}
