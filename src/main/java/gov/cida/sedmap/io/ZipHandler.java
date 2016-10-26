package gov.cida.sedmap.io;


import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import gov.cida.sedmap.io.util.exceptions.SedmapException;

public class ZipHandler extends BaseHandler {
	private static final Logger logger = Logger.getLogger(ZipHandler.class);

	protected static final String CONTENT_TYPE= "application/zip";
	protected final ZipOutputStream     out;

	protected String entryName;

	public ZipHandler(HttpServletResponse res, OutputStream stream, String name) {
		super(res, new ZipOutputStream(stream), CONTENT_TYPE, name);
		out = (ZipOutputStream) super.out;
	}

	@Override
	public FileDownloadHandler beginWritingFiles() throws SedmapException {
		super.beginWritingFiles();
		resp.addHeader("Content-Disposition", "attachment; filename=data.zip");
		return this; //chain
	}
	
	@Override
	public FileDownloadHandler startNewFile(String contentType, String filename) throws SedmapException {
		entryName = filename;
		
		super.startNewFile(contentType, entryName);
		ZipEntry entry = new ZipEntry(entryName);
		
		try {
			out.putNextEntry(entry);
		} catch (IOException e) {
			String msg = "Error adding new zip entry " + entryName + " to " + name; 
			logger.error(msg,e);
			throw new SedmapException(msg, e);
		}
		
		return this; //chain
	}
	
	@Override
	public FileDownloadHandler endNewFile() throws SedmapException {
		
		try {
			out.closeEntry(); // would be nice if this was a Closeable
			super.endNewFile();
		}  catch (IOException e) {
			String msg = "Error closing zip entry " + entryName + " in " + name; 
			logger.error(msg,e);
			throw new SedmapException(msg, e);
		}
		
		return this; //chain
	}
	
	@Override
	public FileDownloadHandler finishWritingFiles() throws SedmapException {
		// if the container handles this then it does not have to be in a finally block
		
		try {
			out.finish();
			super.finishWritingFiles();
		}  catch (IOException e) {
			String msg = "Error finishing zip file " + name; 
			logger.error(msg,e);
			throw new SedmapException(msg, e);
		}
		
		return this; //chain
	}
}
