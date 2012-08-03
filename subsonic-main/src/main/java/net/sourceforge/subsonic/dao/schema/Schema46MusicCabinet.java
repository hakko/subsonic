package net.sourceforge.subsonic.dao.schema;

import org.springframework.jdbc.core.JdbcTemplate;

public class Schema46MusicCabinet extends Schema {

    @Override
    public void execute(JdbcTemplate template) {
    	
    	// if we come from an installation of Subsonic 4.7+,
    	// delete what's new since 4.6, before adding MusicCabinet stuff.
    	
    	if (tableExists(template, "media_file")) { // added in 4.7
    		template.execute("drop table starred_media_file");
    		template.execute("drop table starred_album");
    		template.execute("drop table starred_artist");
    		template.execute("drop table artist");
    		template.execute("drop table album");
    		template.execute("drop table playlist_file");
    		template.execute("drop table playlist_user");
    		template.execute("drop table playlist");
    		template.execute("drop table media_file");
    		
    		template.execute("delete from version where version > 20");
    	}
    }

}
