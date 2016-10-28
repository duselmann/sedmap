package gov.cida.sedmap.io;


import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import gov.cida.sedmap.io.util.StrUtils;
import gov.cida.sedmap.io.util.exceptions.SedmapException;
import gov.cida.sedmap.mail.SedmapDataMail;

public class EmailLinkHandler extends ZipHandler {
	private static final Logger logger = Logger.getLogger(EmailLinkHandler.class);

	protected String contentType = "text/plain";
	protected String emailAddr;
	
	// TODO these need refactor
	protected String errorId;
	protected Exception exceptionThrown;


	public EmailLinkHandler(HttpServletResponse res, String email) throws IOException {
		super(res);
		emailAddr = email;
	}

	@Override
	public FileDownloadHandler beginWritingFiles() throws SedmapException {
		resp.setContentType( getContentType() );
		/**
		 * We close the clients response connection here to simulate spawning
		 * another process.  In actuality, the browser gets a "close" after
		 * "acquisition commenced" is written and continues on its merry way
		 * while THIS thread (whoever called this method) continues execution.
		 */
		try (OutputStream outs = resp.getOutputStream()) {
			outs.write("acquisition commenced".getBytes());
		} catch (IOException e) {
			String msg = "Error writing response to user that the data acquisition commenced";
			logger.error(msg,e);
			throw new SedmapException(msg, e);
		}
		
		return this; //chain
	}
	@Override
	public FileDownloadHandler finishWritingFiles() throws SedmapException {
		super.finishWritingFiles();

		SedmapDataMail mailer = new SedmapDataMail();
		// TODO handle false on send message
		if ( StrUtils.isEmpty(getErrorId()) ) {
			// this to prevent exposing how files are stored
			String fileId = getFileName();
			fileId = fileId.substring(5,fileId.length()-4);

			mailer.sendFileMessage(emailAddr, fileId);
		} else {
			mailer.sendErrorMessage(emailAddr, getErrorId(), getExceptionThrown());
		}
		return this; //chain
	}


	public String getErrorId() {
		return errorId;
	}

	public void setErrorId(String errorId) {
		this.errorId = errorId;
	}

	public Exception getExceptionThrown() {
		return exceptionThrown;
	}

	public void setExceptionThrown(Exception exceptionThrown) {
		this.exceptionThrown = exceptionThrown;
	}
}
