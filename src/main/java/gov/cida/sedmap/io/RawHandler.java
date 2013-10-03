package gov.cida.sedmap.io;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;

public class RawHandler extends BaseHandler {

	protected static final String CONTENT_TYPE= "application/zip"; // TODO make this configurable

	protected final InputStream source;

	public RawHandler(HttpServletResponse res, OutputStream stream, InputStream fis) {
		super(res, stream, CONTENT_TYPE);
		source = fis;
	}

	@Override
	public FileDownloadHandler beginWritingFiles() throws IOException {
		super.beginWritingFiles();
		resp.addHeader("Content-Disposition", "attachment; filename=data.zip");

		int count=0;
		byte[] data=new byte[1024<<3]; // 8k buffer

		while ( (count=source.read(data)) >0 ) {
			write(data, count);
		}

		return this; //chain
	}
}
