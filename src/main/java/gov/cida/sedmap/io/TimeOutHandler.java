package gov.cida.sedmap.io;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import gov.cida.sedmap.io.util.SessionUtil;
import gov.cida.sedmap.io.util.exceptions.SedmapException;

//
public class TimeOutHandler extends EmailLinkHandler {
	private static final Logger logger = Logger.getLogger(TimeOutHandler.class);
	
	protected static final String LINK_PATH_ENV_KEY        = "sedmap/timeouthandler/linkpath";
	protected static final String LINK_PATH_LINK_DEFAULT   = "/sediment/download?file=";
	public    static final String LINK_PATH;
	
	static {
		LINK_PATH = SessionUtil.lookup(LINK_PATH_ENV_KEY, LINK_PATH_LINK_DEFAULT);
	}
	
	private volatile Boolean finishedWriting = false;
	private volatile boolean pastEmailLogic = false;

	
	public TimeOutHandler(HttpServletResponse res, String email) throws IOException {
		super(res, email);
	}
	
	@Override
	public FileDownloadHandler beginWritingFiles() throws SedmapException {
		/**
		 * Since the fetcher is dealing w/ response to the client based on a
		 * timeout value, we dont do anything here.
		 */
		
		return this; //chain
	}
	
	@Override
	public FileDownloadHandler finishWritingFiles() throws SedmapException {
		/**
		 * This is a relatively fast method but its speed has implications such
		 * that if we actually have started this method but the main thread's
		 * timeout hit during it, we could have a situation where we return
		 * the filename to the client AND send an email.  So what we'll do is
		 * synchronize this whole block against the "finishedWriting" flag so
		 * it has the least chance of happening.
		 */
		synchronized (finishedWriting) {
			if (logger.isDebugEnabled()) {
				logger.debug("finishing writing files for timeout send email is " + isSendEmail);
			}
			
			finishedWriting = true;
			pastEmailLogic = isSendEmail;
			
			super.finishWritingFiles();
		}
		
		return this; //chain
	}
	
	/**
	 * Returns the URL for the file created in this handler
	 * @return
	 */
	public String getFileUrl() {
		String fileName = getFileName();
		String fileId   = fileName.substring(5,fileName.length()-4);
		
		return LINK_PATH + fileId;
	}
	
	/**
	 * Returns the HttpServletResponse so the controlling fetcher can send back
	 * correct things to the client since only the Handler has access to the
	 * response.
	 * 
	 * Not ideal but no other way to do this when dealing w/ threading and timeouts
	 * as the fetcher is the service kicking off this handler but the handler
	 * has the response and needs to keep going if we time out
	 * @return
	 */
	public HttpServletResponse getResponse() {
		return this.resp;
	}

	public boolean sendingEmail() {
		return isSendEmail;
	}

	public void setSendEmail(boolean send) {
		isSendEmail = send;
	}
	
	public boolean finishedWriting() {
		return finishedWriting;
	}

	public boolean isPastEmailLogic() {
		return pastEmailLogic;
	}
}
