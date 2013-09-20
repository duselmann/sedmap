package gov.cida.sedmap.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

public class BaseHandler implements FileDownloadHandler {

	public static final String BOUNDARY_TAG = "--AMZ90RFX875LKMFasdf09DDFF3";
	public static final String MULTI_PART_CONTENT_TYPE = "multipart/x-mixed-replace;boundary="
			+ BOUNDARY_TAG.substring(2);

	protected final HttpServletResponse resp;
	protected final OutputStream        out;
	protected final String       contentType;


	public BaseHandler(HttpServletResponse res, OutputStream stream, String contentType) {
		resp = res;
		out  = stream;
		this.contentType = contentType;
	}

	@Override
	public void close() throws IOException {
		out.close();
	}

	@Override
	public void write(byte[] data) throws IOException {
		out.write(data);
	}
	@Override
	public void write(String data) throws IOException {
		this.write(data.getBytes());
	}

	@Override
	public String getContentType() {
		return contentType;
	}


	@Override
	public void beginWritingFiles() throws IOException {
		resp.setContentType( getContentType() );
	}


	@Override
	public void startNewFile(String contentType, String filename) throws IOException {
	}


	@Override
	public void writeFile(String contentType, String filename, InputStream fileData) throws IOException {

		try {
			startNewFile(contentType, filename);

			BufferedInputStream buf = new BufferedInputStream(fileData);
			InputStreamReader input = new InputStreamReader(buf);
			BufferedReader   reader = new BufferedReader(input);

			String line = "";
			while ((line = reader.readLine()) != null) {
				//logger.debug(line);
				write(line);
				write(IoUtils.LINE_SEPARATOR);
			}
			write(IoUtils.LINE_SEPARATOR);
			endNewFile();
		} finally {
			IoUtils.quiteClose(fileData);
		}
	}



	@Override
	public void endNewFile() throws IOException {
		// place holder for book-ending
	}


	@Override
	public void finishWritingFiles() throws IOException {
		// if the container handles this then it does not have to be in a finally block
		out.flush();
		out.close();
	}

}
