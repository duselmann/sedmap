package gov.cida.sedmap.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import gov.cida.sedmap.io.util.exceptions.SedmapException;

public class BaseHandler implements FileDownloadHandler {
	private static final Logger logger = Logger.getLogger(BaseHandler.class);

	public static final String BOUNDARY_TAG = "--AMZ90RFX875LKMFasdf09DDFF3";
	public static final String MULTI_PART_CONTENT_TYPE = "multipart/x-mixed-replace;boundary="
			+ BOUNDARY_TAG.substring(2);

	protected final HttpServletResponse resp;
	
	protected OutputStream        out;
	protected final String        contentType;
	protected final String        name;


	public BaseHandler(HttpServletResponse res, OutputStream stream, String contentType, String name) {
		this.resp = res;
		this.out  = stream;
		this.contentType = contentType;
		this.name = name;
	}

	@Override
	public void close() throws IOException {
		IoUtils.quietClose(out);
	}

	@Override
	public FileDownloadHandler write(byte[] data) throws SedmapException {
		try {
			out.write(data);
		} catch (IOException e) {
			String msg = "Error writing to stream " + name;
			logger.error(msg,e);
			throw new SedmapException(msg, e);
		}
		
		return this; //chain
	}
	@Override
	public FileDownloadHandler write(byte[] data, int length) throws SedmapException {
		try {
			out.write(data, 0, length);
		} catch (IOException e) {
			String msg = "Error writing to stream " + name;
			logger.error(msg,e);
			throw new SedmapException(msg, e);
		}
		
		return this; //chain
	}
	@Override
	public FileDownloadHandler write(String data) throws SedmapException {
		return this.write(data.getBytes());
	}

	@Override
	public String getContentType() {
		return contentType;
	}


	@Override
	public FileDownloadHandler beginWritingFiles() throws SedmapException {
		try {
			resp.setContentType( getContentType() );
		} catch (Exception e) {
			String msg = "Error setting response content type to " + name;
			logger.error(msg,e);
			throw new SedmapException(msg, e);
		}
		
		return this; //chain
	}


	@Override
	public FileDownloadHandler startNewFile(String contentType, String filename) throws SedmapException {
		return this; //chain
	}


	@Override
	public FileDownloadHandler writeFile(String contentType, String filename, InputStream fileData) throws SedmapException  {

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
		} catch (IOException e) {
			String msg = "Error reading from source";
			logger.error(msg,e);
			throw new SedmapException(msg, e);
		} finally {
			IoUtils.quietClose(fileData);
		}
		return this; //chain
	}



	@Override
	public FileDownloadHandler endNewFile() throws SedmapException {
		// place holder for book-ending
		return this; //chain
	}


	@Override
	public FileDownloadHandler finishWritingFiles() throws SedmapException {
		// if the container handles this then it does not have to be in a finally block
		
		try {
			out.flush();
			IoUtils.quietClose(out);
			out = null;
		} catch (IOException e) {
			String msg = "Error flushing final file in basic handler - likely a zip related issue.";
			logger.error(msg,e);
			throw new SedmapException(msg, e);
		}
		return this; //chain
	}


	@Override
	public boolean isAlive() {
		if (out == null) {
			return false;
		}
		//		try {
		//			out.flush();
		//		} catch (Exception e) {
		//			return false;
		//		}
		return true;
	}
}
