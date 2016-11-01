package gov.cida.sedmap.io;


import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import gov.cida.sedmap.io.util.exceptions.SedmapException;

public class ZipHandler extends BaseHandler {
	private static final Logger logger = Logger.getLogger(ZipHandler.class);

	protected static final String CONTENT_TYPE= "application/zip";
	
	protected final ZipOutputStream     out;
	protected File file;
	protected String entryName;

	public ZipHandler(HttpServletResponse res) throws IOException {
		// I would have liked, and tried many times, to have the zip output stream passed in
		// java strict order of creation made this much more difficult than it needed to be
		super(res, null, CONTENT_TYPE, IoUtils.createTmpFileName("data"));
		file = File.createTempFile(name, ".zip");;
		super.out = out = IoUtils.createTmpZipOutStream(file);;
	}

	public String getFileName() {
		return file.getName();
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
}
