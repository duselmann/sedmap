package gov.cida.sedmap.io;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;

public class RawHandler extends BaseHandler {

	protected static final String CONTENT_TYPE= "application/zip"; // TODO make this configurable

	protected final InputStream source;
	protected final File file;

	public RawHandler(HttpServletResponse res, OutputStream stream, File file) {
		super(res, stream, CONTENT_TYPE);
		try {
			this.file = file;
			source = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Failed to open requested data file.",e);
		}
	}

	@Override
	public FileDownloadHandler beginWritingFiles() throws Exception {
		super.beginWritingFiles();
		resp.addHeader("Content-Disposition", "attachment; filename=data.zip");
		resp.addHeader("Content-Length", ""+file.length());

		int count=0;
		byte[] data=new byte[1024<<3]; // 8k buffer

		while ( (count=source.read(data)) >0 ) {
			write(data, count);
		}

		return this; //chain
	}

	@Override
	public void close() throws IOException {
		IoUtils.quiteClose(source);
		super.close();
	}
}
