package gov.cida.sedmap.io;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import gov.cida.sedmap.io.util.exceptions.SedmapException;

public class RawHandler extends BaseHandler {

	protected static final String CONTENT_TYPE= "application/zip"; // TODO make this configurable

	protected final InputStream source;
	protected final File file;

	public RawHandler(HttpServletResponse res, OutputStream stream, File file) {
		super(res, stream, CONTENT_TYPE, file.getName());
		try {
			this.file = file;
			source = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Failed to open requested data file.",e);
		}
	}

	@Override
	public FileDownloadHandler beginWritingFiles() throws SedmapException {
		super.beginWritingFiles();
		resp.addHeader("Content-Disposition", "attachment; filename=data.zip");
		resp.addHeader("Content-Length", ""+file.length());

		try {
			IOUtils.copy(source, this.out);
		} catch (IOException e) {
			throw new SedmapException("Failed to copy date to target: " + name , e);
		}

		return this; //chain
	}

	@Override
	public void close() throws IOException {
		IoUtils.quiteClose(source);
		super.close();
	}
}
