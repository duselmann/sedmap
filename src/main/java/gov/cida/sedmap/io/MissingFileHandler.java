package gov.cida.sedmap.io;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import gov.cida.sedmap.data.DataFileMgr;
import gov.cida.sedmap.io.util.exceptions.SedmapException;

public class MissingFileHandler extends BaseHandler {

	protected static String contentType = "text/plain";

	public MissingFileHandler(HttpServletResponse res, OutputStream stream, String filename) throws FileNotFoundException {
		super(res, stream, contentType, filename);
	}

	@Override
	public FileDownloadHandler beginWritingFiles() throws SedmapException {
		resp.setContentType( getContentType() );
		try (OutputStream out = resp.getOutputStream()) {
			out.write( ("We are sorry. That file was not found.\n\n"
					+" If you are using a URL you received, note that files are only retained for "
					+DataFileMgr.RETAIN_DAYS +" days.").getBytes() );
		} catch (IOException e) {
			throw new SedmapException("Failed to write file not found to user", e);
		}
		return this; //chain
	}
}
