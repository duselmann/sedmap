package gov.cida.sedmap.io;


import gov.cida.sedmap.io.util.StrUtils;
import gov.cida.sedmap.mail.SedmapDataMail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

public class EmailLinkHandler extends ZipHandler {

	protected String contentType = "text/plain";
	protected String emailAddr;
	protected final File file;
	protected String errorId;


	public EmailLinkHandler(HttpServletResponse res, File file, String email) throws FileNotFoundException {
		super(res, new FileOutputStream(file));
		this.file = file;
		emailAddr = email;
	}

	@Override
	public FileDownloadHandler beginWritingFiles() throws IOException {
		resp.setContentType( getContentType() );
		resp.getOutputStream().write("acquisition commenced".getBytes());
		resp.getOutputStream().flush();
		resp.getOutputStream().close();
		return this; //chain
	}
	@Override
	public FileDownloadHandler finishWritingFiles() throws IOException {
		super.finishWritingFiles();

		SedmapDataMail mailer = new SedmapDataMail();
		// TODO handle false on send message
		if ( StrUtils.isEmpty(getErrorId()) ) {
			String fileId = getFileName();
			fileId = fileId.substring(5,fileId.length()-4);

			mailer.sendFileMessage(emailAddr, fileId);
		} else {
			mailer.sendErrorMessage(emailAddr, getErrorId());
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


}
