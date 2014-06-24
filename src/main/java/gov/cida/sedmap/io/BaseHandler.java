package gov.cida.sedmap.io;

import gov.cida.sedmap.io.util.exceptions.SedmapException;
import gov.cida.sedmap.io.util.exceptions.SedmapException.OGCExceptionCode;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class BaseHandler implements FileDownloadHandler {
	private static final Logger logger = Logger.getLogger(BaseHandler.class);

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
	public FileDownloadHandler write(byte[] data) throws Exception {
		try {
			out.write(data);
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.error("Due to internal exception caught, throwing generic OGC error for error handling on the client side.");
			throw new SedmapException(OGCExceptionCode.NoApplicableCode, new Exception(SedmapException.GENERIC_ERROR));
		}
		
		return this; //chain
	}
	@Override
	public FileDownloadHandler write(byte[] data, int length) throws Exception {
		try {
			out.write(data, 0, length);
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.error("Due to internal exception caught, throwing generic OGC error for error handling on the client side.");
			throw new SedmapException(OGCExceptionCode.NoApplicableCode, new Exception(SedmapException.GENERIC_ERROR));
		}
		
		return this; //chain
	}
	@Override
	public FileDownloadHandler write(String data) throws Exception {
		try {
			this.write(data.getBytes());
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.error("Due to internal exception caught, throwing generic OGC error for error handling on the client side.");
			throw new SedmapException(OGCExceptionCode.NoApplicableCode, new Exception(SedmapException.GENERIC_ERROR));
		}
		
		return this; //chain
	}

	@Override
	public String getContentType() {
		return contentType;
	}


	@Override
	public FileDownloadHandler beginWritingFiles() throws Exception {
		try {
			resp.setContentType( getContentType() );
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error("Due to internal exception caught, throwing generic OGC error for error handling on the client side.");
			throw new SedmapException(OGCExceptionCode.NoApplicableCode, new Exception(SedmapException.GENERIC_ERROR));
		}
		
		return this; //chain
	}


	@Override
	public FileDownloadHandler startNewFile(String contentType, String filename) throws Exception {
		return this; //chain
	}


	@Override
	public FileDownloadHandler writeFile(String contentType, String filename, InputStream fileData) throws Exception {

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
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error("Due to internal exception caught, throwing generic OGC error for error handling on the client side.");
			throw new SedmapException(OGCExceptionCode.NoApplicableCode, new Exception(SedmapException.GENERIC_ERROR));
		} finally {
			IoUtils.quiteClose(fileData);
		}
		return this; //chain
	}



	@Override
	public FileDownloadHandler endNewFile() throws Exception {
		// place holder for book-ending
		return this; //chain
	}


	@Override
	public FileDownloadHandler finishWritingFiles() throws Exception {
		// if the container handles this then it does not have to be in a finally block
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.error("Due to internal exception caught, throwing generic OGC error for error handling on the client side.");
			throw new SedmapException(OGCExceptionCode.NoApplicableCode, new Exception(SedmapException.GENERIC_ERROR));
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
