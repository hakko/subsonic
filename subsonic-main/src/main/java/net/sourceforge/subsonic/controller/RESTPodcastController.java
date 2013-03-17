package net.sourceforge.subsonic.controller;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.PodcastChannel;
import net.sourceforge.subsonic.domain.PodcastEpisode;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.PodcastService;
import net.sourceforge.subsonic.util.StringUtil;
import net.sourceforge.subsonic.util.XMLBuilder;
import net.sourceforge.subsonic.util.XMLBuilder.AttributeSet;

public class RESTPodcastController extends RESTAbstractController {

    private RESTBrowseController restBrowseController;
    private PodcastService podcastService;
    private MediaFileService mediaFileService;

    public void getPodcasts(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        Player player = playerService.getPlayer(request, response);
        XMLBuilder builder = createXMLBuilder(request, response, true);
        builder.add("podcasts", false);

        for (PodcastChannel channel : podcastService.getAllChannels()) {
            AttributeSet channelAttrs = new AttributeSet();
            channelAttrs.add("id", channel.getId());
            channelAttrs.add("url", channel.getUrl());
            channelAttrs.add("status", channel.getStatus().toString().toLowerCase());
            if (channel.getTitle() != null) {
                channelAttrs.add("title", channel.getTitle());
            }
            if (channel.getDescription() != null) {
                channelAttrs.add("description", channel.getDescription());
            }
            if (channel.getErrorMessage() != null) {
                channelAttrs.add("errorMessage", channel.getErrorMessage());
            }
            builder.add("channel", channelAttrs, false);

            List<PodcastEpisode> episodes = podcastService.getEpisodes(channel.getId(), false);
            for (PodcastEpisode episode : episodes) {
                AttributeSet episodeAttrs = new AttributeSet();

                String path = episode.getPath();
                if (path != null) {
                    MediaFile mediaFile = mediaFileService.getNonIndexedMediaFile(path);
                    File coverArt = mediaFileService.getCoverArt(mediaFile);
                    episodeAttrs.addAll(restBrowseController.createAttributesForMediaFile(player, coverArt, mediaFile));
                    episodeAttrs.add("streamId", StringUtil.utf8HexEncode(mediaFile.getPath()));
                }

                episodeAttrs.add("id", episode.getId());  // Overwrites the previous "id" attribute.
                episodeAttrs.add("status", episode.getStatus().toString().toLowerCase());

                if (episode.getTitle() != null) {
                    episodeAttrs.add("title", episode.getTitle());
                }
                if (episode.getDescription() != null) {
                    episodeAttrs.add("description", episode.getDescription());
                }
                if (episode.getPublishDate() != null) {
                    episodeAttrs.add("publishDate", episode.getPublishDate());
                }

                builder.add("episode", episodeAttrs, true);
            }

            builder.end(); // <channel>
        }
        builder.endAll();
        response.getWriter().print(builder);
    }

    public void setRestBrowseController(RESTBrowseController restBrowseController) {
        this.restBrowseController = restBrowseController;
    }

    public void setPodcastService(PodcastService podcastService) {
        this.podcastService = podcastService;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

}
