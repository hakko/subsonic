package net.sourceforge.subsonic.ajax;

import net.sourceforge.subsonic.Logger;

import org.directwebremoting.Browser;
import org.directwebremoting.ServerContext;
import org.directwebremoting.ServerContextFactory;
import org.directwebremoting.ui.dwr.Util;

/**
 * Provides AJAX-enabled services for the chatting.
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 */
public class LibraryStatusService {

	private static final Logger LOG = Logger.getLogger(LibraryStatusService.class);

	private long lastModified = System.currentTimeMillis();
	
	public enum Message {
		
		SCAN_STARTED ("Your library is being scanned."), 
		SCAN_FINISHED ("Your library has been scanned.<br><a href=\"javascript:window.location.reload()\">Refresh.</a>");
		
		private final String message;

		private Message(String message) {
			this.message = message;
		}
	}
	
	public long getLastModified() {
		return lastModified;
	}
	
	public void notifyLibraryUpdate(final Message message) {

		lastModified = System.currentTimeMillis();
		
		ServerContext serverContext = ServerContextFactory.get();
		if (serverContext != null) {
			String page = ServerContextFactory.get().getContextPath() + "/left.view";
			Browser.withPage(page, new Runnable() {
				@Override
				public void run() {
					Util.setValue("leftMessage", message.message);
				}
			});
		}
	}

}