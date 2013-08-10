package gov.cida.sedmap.io;


import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

public class ZipHandler extends BaseHandler {

	protected static final String CONTENT_TYPE= "application/zip";
	protected final ZipOutputStream     out;


	public ZipHandler(HttpServletResponse res, OutputStream stream) {
		super(res, new ZipOutputStream(stream), CONTENT_TYPE);
		out = (ZipOutputStream) super.out;
	}



	@Override
	public void beginWritingFiles() throws IOException {
		super.beginWritingFiles();
		resp.addHeader("Content-Disposition", "attachment; filename=data.zip");
	}
	@Override
	public void startNewFile(String contentType, String filename) throws IOException {
		super.startNewFile(contentType, filename);
		ZipEntry entry = new ZipEntry(filename);
		out.putNextEntry(entry);
	}
	@Override
	public void endNewFile() throws IOException {
		out.closeEntry();
		super.endNewFile();
	}
	@Override
	public void finishWritingFiles() throws IOException {
		// if the container handles this then it does not have to be in a finally block
		out.finish();
		super.finishWritingFiles();
	}

}
