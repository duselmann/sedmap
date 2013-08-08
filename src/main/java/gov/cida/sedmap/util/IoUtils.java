package gov.cida.sedmap.util;

import gov.cida.sedmap.web.DataService;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class IoUtils {

	private static final Logger logger = Logger.getLogger(DataService.class);

	public static final String LINE_SEPARATOR  = System.getProperty("line.separator");

	private static final String BOUNDARY_TAG = "--AMZ90RFX875LKMFasdf09DDFF3";
	private static final String MULTI_PART_CONTENT_TYPE = "multipart/x-mixed-replace;boundary=" + BOUNDARY_TAG.substring(2);

	private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-~";

	public static void quiteClose(Object ... open) {

		if (open == null) return;

		for (Object o : open) {
			if (o == null) continue;

			try {
				logger.debug("Closing resource. " + o.getClass().getName());
				if (o instanceof Connection) if ( !((Connection)o).isClosed() ) ((Connection)o).close();
				else if (o instanceof Statement)  if ( !( (Statement)o).isClosed() ) ( (Statement)o).close();
				else if (o instanceof ResultSet)  if ( !( (ResultSet)o).isClosed() ) ( (ResultSet)o).close();
				else if (o instanceof Closeable) ((Closeable)o).close();
				else throw new UnsupportedOperationException("Cannot handle closing instances of " + o.getClass().getName());

			} catch (UnsupportedOperationException e) {
				throw e;

			} catch (Exception e) {
				logger.warn("Failed to close resource. " + o.getClass().getName());
			}
		}
	}



	public static String readTextResource(String resource) {
		StringBuilder contents = new StringBuilder();

		try {
			InputStreamReader in = new InputStreamReader(IoUtils.class.getResourceAsStream(resource), "UTF-8");
			BufferedReader input = new BufferedReader(in);
			try {
				String line = null;
				while ( (line = input.readLine()) != null ) {
					contents.append(line).append(LINE_SEPARATOR);
				}
			} finally {
				IoUtils.quiteClose(input);
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
			throw new RuntimeException("Failed to open the rdb-metadata.txt file.",ex);
		}
		return contents.toString();
	}



	public static void beginWritingFiles(HttpServletResponse resp) {
		resp.setContentType(MULTI_PART_CONTENT_TYPE);
	}
	public static void startNewFile(HttpServletResponse resp, String contentType, String filename) throws IOException {
		resp.getWriter().write(BOUNDARY_TAG, 0, BOUNDARY_TAG.length());
		resp.getWriter().write(contentType);
		filename = "Content-Disposition: attachment; filename=" +filename +IoUtils.LINE_SEPARATOR;
		resp.getWriter().write(filename);
	}
	public static void finishWritingFiles(HttpServletResponse resp) throws IOException {
		String endBndry = BOUNDARY_TAG + "--";
		resp.getWriter().write(endBndry);  // write the ending boundary
		resp.getWriter().close(); // TODO should we do this or leave to the container?
	}

	public static void writeFile(HttpServletResponse resp, String contentType, String filename, InputStream fileData)
			throws IOException {

		startNewFile(resp, contentType, filename);

		BufferedInputStream buf = new BufferedInputStream(fileData);
		InputStreamReader input = new InputStreamReader(buf);
		BufferedReader   reader = new BufferedReader(input);

		String line = "";
		while ((line = reader.readLine()) != null) {
			resp.getWriter().write(line);
		}
		quiteClose(fileData);
	}



	public static String uniqueName(int length) {
		StringBuilder name = new StringBuilder();

		SecureRandom rand = new SecureRandom(); // TODO maybe include seed bytes

		for (int c=0; c<length; c++) {
			int ch = rand.nextInt( CHARS.length() );
			name.append( CHARS.charAt(ch) );
		}

		return name.toString();
	}
}
