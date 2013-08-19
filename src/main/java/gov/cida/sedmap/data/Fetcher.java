package gov.cida.sedmap.data;

import gov.cida.sedmap.io.FileDownloadHandler;
import gov.cida.sedmap.io.FileInputStreamWithFile;
import gov.cida.sedmap.io.IoUtils;
import gov.cida.sedmap.io.util.StringValueIterator;
import gov.cida.sedmap.ogc.OgcUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.opengis.filter.Filter;

public abstract class Fetcher {

	public static final String SEDMAP_DS = "java:comp/env/jdbc/sedmapDS";

	private static final Logger logger = Logger.getLogger(Fetcher.class);

	public static FetcherConfig conf;


	protected String getDataTable(String descriptor) {
		return conf.DATA_TABLES.get(descriptor);
	}
	protected List<Column> getTableMetadata(String tableName) {
		return conf.getTableMetadata(tableName);
	}
	protected Context getContext() throws NamingException {
		return conf.getContext();
	}


	protected abstract InputStream handleLocalData(String descriptor, Filter filter, Formatter formatter)
			throws IOException, SQLException, NamingException;
	protected abstract InputStream handleNwisData(Iterator<String> sites, Filter filter, Formatter formatter)
			throws IOException, SQLException, NamingException;


	public void doFetch(HttpServletRequest req, FileDownloadHandler handler)
			throws ServletException, IOException {
		logger.debug("doFetch");

		String    dataTypes = getDataTypes(req);
		Formatter formatter = getFormatter(req);
		String    ogcXml    = getFilter(req);
		Filter    filter    = OgcUtils.ogcXml2Filter(ogcXml);

		handler.beginWritingFiles(); // start writing files

		Iterator<String> dailySites = null;

		for (String value : conf.DATA_VALUES) { // check for daily and discrete
			if ( ! dataTypes.contains(value) ) continue;
			for (String site  : conf.DATA_TYPES) { // check for sites and data
				if ( ! dataTypes.contains(site) ) continue;

				StringBuilder  name = new StringBuilder();
				String   descriptor = name.append(site).append('_').append(value).toString();
				String     filename = descriptor + formatter.getFileType();

				InputStream fileData = null;
				try {
					if ( "daily_data".equals(descriptor) ) {
						fileData = handleNwisData(dailySites, filter, formatter);
					} else {
						fileData = handleLocalData(descriptor, filter, formatter);
						if (descriptor.contains("daily")) dailySites = makeSiteIterator(fileData, formatter);
					}
					handler.writeFile(formatter.getContentType(), filename, fileData);

				} catch (Exception e) {
					logger.error("failed to fetch from DB", e);
					// TODO empty results and err msg to user
					return;
				} finally {
					IoUtils.quiteClose(fileData);
				}
			}
		}
		handler.finishWritingFiles(); // done writing files

	}


	// TODO this needs a bit of finessing because we want to be able to read the site data twice.
	// once for the use return download and again for the NWIS site list.
	// right now, it assumes that the data comes from a cached file.
	// it uses the formatter that created the file to know how to split the file
	protected Iterator<String> makeSiteIterator(InputStream fileData, Formatter formatter) throws IOException {

		LinkedList<String> sites = new LinkedList<String>();

		if (fileData instanceof FileInputStreamWithFile) {
			File file = ((FileInputStreamWithFile) fileData).getFile();
			InputStreamReader in  = new InputStreamReader( IoUtils.createTmpZipStream(file) );
			BufferedReader reader = new BufferedReader(in);

			String line;
			while ( (line=reader.readLine()) != null ) {
				int pos = line.indexOf( formatter.getSeparator() );
				if (pos == -1) continue; // trap empty lines
				String site = line.substring(0, pos );
				// only preserve site numbers and not comments or header info
				if (site.matches("^\\d+$")) {
					sites.add(site);
				}
			}
		}

		return new StringValueIterator( sites.iterator() );
	}



	protected String getFilter(HttpServletRequest req) {
		String ogcXml = req.getParameter("filter");

		if (ogcXml == null) {
			logger.warn("Failed to locate OGC 'filter' parameter - using default");
			// TODO empty result
			return "";
		}

		return ogcXml;
	}



	protected String getDataTypes(HttpServletRequest req) {
		String types = req.getParameter("dataTypes");

		// expecting a string with terms "daily_discrete_sites_data" in it
		// sites means site data - no samples
		// data  means site data - no site info
		// daily and discrete refer to samples
		// - "daily_discrete_sites_data" means they want all data
		// - "daily_sites" means they want all daily site info only

		if (types == null) {
			logger.warn("Failed to locate 'dataTypes' parameter - using default");
			types = "daily_discrete_sites";
		}

		return types;
	}



	protected Formatter getFormatter(HttpServletRequest req) {
		String format = req.getParameter("format");
		format = conf.FILE_FORMATS.containsKey(format) ?format :"rdb";

		Formatter formatter = null;
		try {
			formatter = conf.FILE_FORMATS.get(format).newInstance();
		} catch (Exception e) {
			logger.warn("Could not instantiate formatter for '" +format+"' with class "
					+conf.FILE_FORMATS.get(format)+". Using RDB as fall-back.");
			formatter = new RdbFormatter();
		}

		return formatter;
	}

}
