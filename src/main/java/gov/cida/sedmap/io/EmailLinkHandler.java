package gov.cida.sedmap.io;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import gov.cida.sedmap.io.util.StrUtils;
import gov.cida.sedmap.io.util.exceptions.SedmapException;
import gov.cida.sedmap.mail.SedmapDataMail;

public class EmailLinkHandler extends ZipHandler {
	private static final Logger logger = Logger.getLogger(EmailLinkHandler.class);

	protected String contentType = "text/plain";
	protected String emailAddr;
	protected final File file;
	protected String errorId;
	protected Exception exceptionThrown;


	public EmailLinkHandler(HttpServletResponse res, File file, String email) throws FileNotFoundException {
		super(res, new FileOutputStream(file), email);
		this.file = file;
		emailAddr = email;
	}

	@Override
	public FileDownloadHandler beginWritingFiles() throws SedmapException {
		/**
		 * We close the clients response connection here to simulate spawning
		 * another process.  In actuality, the browser gets a "close" after
		 * "acquisition commenced" is written and continues on its merry way
		 * while THIS thread (whoever called this method) continues execution.
		 */
		try {
			resp.setContentType( getContentType() );
			resp.getOutputStream().write("acquisition commenced".getBytes());
			resp.getOutputStream().flush();
			resp.getOutputStream().close();
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
			String fileId = getFileName();
			fileId = fileId.substring(5,fileId.length()-4);

			mailer.sendFileMessage(emailAddr, fileId);
		} else {
			mailer.sendErrorMessage(emailAddr, getErrorId(), getExceptionThrown());
		}
		return this; //chain
	}

	protected String getFileName() {

		return file.getName();
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

	@Override
	public boolean isAlive() {
		return true;
	}
}
