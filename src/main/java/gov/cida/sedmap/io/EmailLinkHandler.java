package gov.cida.sedmap.io;


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


	public EmailLinkHandler(HttpServletResponse res, File file, String email) throws FileNotFoundException {
		super(res, new FileOutputStream(file));
		this.file = file;
		emailAddr = email;
	}

	@Override
	public FileDownloadHandler beginWritingFiles() throws IOException {
		resp.setContentType( getContentType() );
		resp.getOutputStream().write("download commenced".getBytes());
		resp.getOutputStream().flush();
		resp.getOutputStream().close();
		return this; //chain
	}
	@Override
	public FileDownloadHandler finishWritingFiles() throws IOException {
		super.finishWritingFiles();

		// TODO handle false on send message
		new SedmapDataMail().sendFileMessage(emailAddr, getFileName());
		return this; //chain
	}

	protected String getFileName() {

		return file.getName();
	}
}
