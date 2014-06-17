package gov.cida.sedmap.io;


import gov.cida.sedmap.io.util.exceptions.SedmapException;
import gov.cida.sedmap.io.util.exceptions.SedmapException.OGCExceptionCode;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class ZipHandler extends BaseHandler {
	private static final Logger logger = Logger.getLogger(ZipHandler.class);

	protected static final String CONTENT_TYPE= "application/zip";
	protected final ZipOutputStream     out;


	public ZipHandler(HttpServletResponse res, OutputStream stream) {
		super(res, new ZipOutputStream(stream), CONTENT_TYPE);
		out = (ZipOutputStream) super.out;
	}



	@Override
	public FileDownloadHandler beginWritingFiles() throws Exception {
		try {
			super.beginWritingFiles();
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.error("Due to internal exception caught, throwing generic OGC error for error handling on the client side.");
			throw new SedmapException(OGCExceptionCode.NoApplicableCode, new Exception(SedmapException.GENERIC_ERROR));
		}
		
		resp.addHeader("Content-Disposition", "attachment; filename=data.zip");
		return this; //chain
	}
	@Override
	public FileDownloadHandler startNewFile(String contentType, String filename) throws Exception {
		super.startNewFile(contentType, filename);
		ZipEntry entry = new ZipEntry(filename);
		
		try {
			out.putNextEntry(entry);
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.error("Due to internal exception caught, throwing generic OGC error for error handling on the client side.");
			throw new SedmapException(OGCExceptionCode.NoApplicableCode, new Exception(SedmapException.GENERIC_ERROR));
		}
		
		return this; //chain
	}
	@Override
	public FileDownloadHandler endNewFile() throws Exception {
		out.closeEntry();
		
		try {
		super.endNewFile();
		}  catch (IOException e) {
			logger.error(e.getMessage());
			logger.error("Due to internal exception caught, throwing generic OGC error for error handling on the client side.");
			throw new SedmapException(OGCExceptionCode.NoApplicableCode, new Exception(SedmapException.GENERIC_ERROR));
		}
		
		return this; //chain
	}
	@Override
	public FileDownloadHandler finishWritingFiles() throws Exception {
		// if the container handles this then it does not have to be in a finally block
		out.finish();
		
		try {
			super.finishWritingFiles();
		}  catch (IOException e) {
			logger.error(e.getMessage());
			logger.error("Due to internal exception caught, throwing generic OGC error for error handling on the client side.");
			throw new SedmapException(OGCExceptionCode.NoApplicableCode, new Exception(SedmapException.GENERIC_ERROR));
		}
		
		return this; //chain
	}
}
