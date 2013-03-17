package net.sourceforge.subsonic.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.ajax.ChatService;
import net.sourceforge.subsonic.util.XMLBuilder;
import net.sourceforge.subsonic.util.XMLBuilder.Attribute;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

public class RESTChatController extends RESTAbstractController {

    private ChatService chatService;

    public void getChatMessages(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        XMLBuilder builder = createXMLBuilder(request, response, true);

        long since = ServletRequestUtils.getLongParameter(request, "since", 0L);

        builder.add("chatMessages", false);

        for (ChatService.Message message : chatService.getMessages(0L).getMessages()) {
            long time = message.getDate().getTime();
            if (time > since) {
                builder.add("chatMessage", true, new Attribute("username", message.getUsername()),
                        new Attribute("time", time), new Attribute("message", message.getContent()));
            }
        }
        builder.endAll();
        response.getWriter().print(builder);
    }

    public void addChatMessage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        try {
            chatService.doAddMessage(ServletRequestUtils.getRequiredStringParameter(request, "message"), request);
            XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
            response.getWriter().print(builder);
        } catch (ServletRequestBindingException x) {
            error(request, response, ErrorCode.MISSING_PARAMETER, getErrorMessage(x));
        }
    }

    public ChatService getChatService() {
        return chatService;
    }

    public void setChatService(ChatService chatService) {
        this.chatService = chatService;
    }

}
