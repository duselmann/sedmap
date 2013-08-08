package gov.cida.sedmap.util;

import gov.cida.sedmap.data.RdbFormatter;
import gov.cida.sedmap.web.DataFetch;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class IoUtils {

	private static final Logger logger = Logger.getLogger(DataFetch.class);

	public static final String LINE_SEPARATOR  = System.getProperty("line.separator");



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
			InputStreamReader in = new InputStreamReader(RdbFormatter.class.getResourceAsStream(resource), "UTF-8");
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
}
