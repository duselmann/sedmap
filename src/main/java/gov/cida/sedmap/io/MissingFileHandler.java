package gov.cida.sedmap.io;


import gov.cida.sedmap.data.DataFileMgr;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

public class MissingFileHandler extends BaseHandler {

	protected static String contentType = "text/plain";

	public MissingFileHandler(HttpServletResponse res, OutputStream stream) throws FileNotFoundException {
		super(res, stream, contentType);
	}

	@Override
	public FileDownloadHandler beginWritingFiles() throws IOException {
		resp.setContentType( getContentType() );
		resp.getOutputStream().write( ("We are sorry. That file was not found.\n\n"
				+" Please use the URL you received.\n"
				+" If you are using a ULR your received, note that files are only retained for "
				+DataFileMgr.RETAIN_DAYS +" days.").getBytes() );
		resp.getOutputStream().flush();
		resp.getOutputStream().close();
		return this; //chain
	}
}
