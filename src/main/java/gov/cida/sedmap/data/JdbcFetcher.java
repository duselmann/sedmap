package gov.cida.sedmap.data;

import gov.cida.sedmap.io.FileDownloadHandler;
import gov.cida.sedmap.io.IoUtils;
import gov.cida.sedmap.io.util.StrUtils;
import gov.cida.sedmap.ogc.OgcUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class JdbcFetcher extends Fetcher {


	private static final Logger logger = Logger.getLogger(JdbcFetcher.class);



	protected static class Results {
		Connection cn;
		Statement  st;
		ResultSet  rs;
	}



	@Override
	public void doFetch(HttpServletRequest req, FileDownloadHandler handler)
			throws ServletException, IOException {
		logger.debug("doFetch");

		String    dataTypes = getDataTypes(req);
		Formatter formatter = getFormatter(req);
		String    ogcXml    = getFilter(req);
		String    where     = ogc2sql(ogcXml); // TODO test for SQL injection


		handler.beginWritingFiles(); // start writing files

		for (String value : DATA_VALUES) { // check for daily and discrete
			if ( ! dataTypes.contains(value) ) continue;
			for (String site  : DATA_TYPES) { // check for sites and data
				if ( ! dataTypes.contains(site) ) continue;

				StringBuilder  name = new StringBuilder();
				String   descriptor = name.append(site).append('_').append(value).toString();
				String     filename = descriptor + formatter.getFileType();

				InputStream fileData = null;
				try {
					if ( "daily_data".equals(descriptor) ) {
						fileData = handleNwisData(descriptor, where, formatter);
					} else {
						fileData = handleLocalData(descriptor, where, formatter);
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



	protected InputStream handleLocalData(String descriptor, String where, Formatter formatter)
			throws IOException, SQLException, NamingException {
		InputStream fileData = null;
		Results rs = new Results();

		try {
			String sql = "select * from " +DATA_TABLES.get(descriptor) + where;
			logger.debug(sql);
			rs = getData(sql);

			File   tmp = File.createTempFile(descriptor + StrUtils.uniqueName(12), formatter.getFileType());
			FileWriter tmpw = new FileWriter(tmp);

			String     tableName = Fetcher.DATA_TABLES.get(descriptor);
			List<Column> columns = Fetcher.getTableMetadata(tableName);
			String header = formatter.fileHeader(columns);
			//logger.debug(header);
			tmpw.write(header);
			while (rs.rs.next()) {
				String row = formatter.fileRow(new ResultSetColumnIterator(rs.rs));
				//logger.debug(row);
				tmpw.write(row);
			}
			IoUtils.quiteClose(tmpw);

			fileData = new FileInputStream(tmp);
			tmp.delete(); // TODO not for delayed download

		} finally {
			IoUtils.quiteClose(rs.rs, rs.st, rs.cn);
		}

		return fileData;
	}



	protected InputStream handleNwisData(String descriptor, String where, Formatter formatter)
			throws IOException, SQLException, NamingException {
		InputStream fileData = null;

		// TODO handle nwis request

		return fileData;
	}



	protected String ogc2sql(String ogcXml) {
		String where = "";

		if ("".equals(ogcXml)) return where;
		try {
			where = OgcUtils.ogcXml2Sql(ogcXml);
			if (where != null && where.trim().length()>0) {
				where = OgcUtils.sqlTranslation(where, FIELD_TRANSLATIONS);
			} else {
				where = "";
			}
		} catch (Exception e) {
			// TODO empty results and err msg
			throw new RuntimeException("Failed to parse OGC", e);
		}

		return where;
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
		format = FILE_FORMATS.containsKey(format) ?format :"rdb";

		Formatter formatter = null;
		try {
			formatter = FILE_FORMATS.get(format).newInstance();
		} catch (Exception e) {
			logger.warn("Could not instantiate formatter for '" +format+"' with class "
					+FILE_FORMATS.get(format)+". Using RDB as fall-back.");
			formatter = new RdbFormatter();
		}

		return formatter;
	}



	protected Results getData(String sql) throws NamingException, SQLException {
		Results r = new Results();

		try {
			Context ctx = getContext();
			DataSource ds = (DataSource) ctx.lookup(SEDMAP_DS);
			r.cn = ds.getConnection();
			r.st = r.cn.createStatement();
			r.rs = r.st.executeQuery(sql);
		} catch (SQLException e) {
			logger.error(e);
			throw e;
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}

		return r;
	}
}
