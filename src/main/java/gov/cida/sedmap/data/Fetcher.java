package gov.cida.sedmap.data;

import gov.cida.sedmap.io.FileDownloadHandler;
import gov.cida.sedmap.io.IoUtils;
import gov.cida.sedmap.ogc.OgcUtils;
import gov.cida.sedmap.web.DataService;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.geotools.jdbc.JDBCDataStoreFactory;
import org.geotools.jdbc.JDBCJNDIDataStoreFactory;
import org.opengis.filter.Filter;

public abstract class Fetcher {

	public static final String SEDMAP_DS = "java:comp/env/jdbc/sedmapDS";

	protected static final Map<String, Object>       DATA_STORE_ENV;
	protected static final Map<String, List<Column>> TABLE_METADATA;
	protected static final Map<String, String>       DATA_TABLES;
	// TODO create this view "select d.* from sm_inst_sample_fact d join sm_inst_stations s on s.USGS_STATION_ID = d.USGS_STATION_ID "

	protected static final Map<String, Class<? extends Formatter>> FILE_FORMATS;
	protected static final Map<String, String> FIELD_TRANSLATIONS;
	protected static final List<String> DATA_TYPES;
	protected static final List<String> DATA_VALUES;

	private static final Logger logger = Logger.getLogger(Fetcher.class);



	// this prevents concurrency issues
	static {
		logger.info("Static Fetcher initialization.");
		FIELD_TRANSLATIONS = configFieldTranslaions();
		FILE_FORMATS       = configFormats();
		DATA_TABLES        = configTables();
		DATA_TYPES         = configDataTypes();
		DATA_VALUES        = configDataValues();
		DATA_STORE_ENV     = configDataStore();
		TABLE_METADATA     = loadTableMetadata();
	}



	protected static Map<String, String> configFieldTranslaions() {
		logger.info("Static Fetcher configFieldTranslaions.");
		Map<String,String> trans = new HashMap<String,String>();
		trans.put("HUC_8","HUC_12");
		return Collections.unmodifiableMap(trans);
	}



	private static List<String> configDataTypes() {
		logger.info("Static Fetcher configDataTypes.");
		return Collections.unmodifiableList( Arrays.asList("daily","discrete") );
	}



	private static List<String> configDataValues() {
		logger.info("Static Fetcher configDataTypes.");
		return Collections.unmodifiableList( Arrays.asList("sites","data") ); // sites first for use with NWIS data
	}



	protected static Map<String, Class<? extends Formatter>> configFormats() {
		logger.info("Static Fetcher configDataTypes.");
		Map<String, Class<? extends Formatter>> formats = new HashMap<String, Class<? extends Formatter>>();
		formats.put("csv", CsvFormatter.class);
		formats.put("rdb", RdbFormatter.class);
		return Collections.unmodifiableMap(formats);
	}



	protected static Map<String, String> configTables() {
		logger.info("Static Fetcher configDataTypes.");
		Map<String,String> tables = new HashMap<String,String>();
		tables.put("daily_sites",    "SM_DAILY_STATIONS");
		tables.put("discrete_sites", "SM_INST_STATIONS");
		tables.put("discrete_data",  "SM_INST_SAMPLE ");
		return Collections.unmodifiableMap(tables);
	}



	protected static Map<String, Object> configDataStore() {
		logger.info("Static Fetcher configDataTypes.");
		Map<String, Object> env = new HashMap<String, Object>();
		// dataStoreEnv.put( JDBCDataStoreFactory.SCHEMA.getName(), "sedmap"); // OPTIONAL
		env.put( JDBCDataStoreFactory.DBTYPE.getName(), "oracle");
		env.put( JDBCDataStoreFactory.EXPOSE_PK.getName(), true);
		env.put( JDBCJNDIDataStoreFactory.JNDI_REFNAME.getName(), SEDMAP_DS);

		return Collections.unmodifiableMap(env);
	}



	// over-ride-able for testing
	protected static Context getContext() throws NamingException {
		if ( DataService.MODE.equals("TEST") ) {
			return DataService.ctx;
		}
		InitialContext ctx = new InitialContext();
		return ctx;
	}



	protected static Map<String, List<Column>> loadTableMetadata() {
		logger.info("Static Fetcher loadTableMetadata.");
		Connection cn = null;
		Statement  st = null;
		try {
			Context ctx = getContext();
			DataSource ds = (DataSource) ctx.lookup(SEDMAP_DS);
			cn = ds.getConnection();
			st = cn.createStatement();

			Map<String, List<Column>> tableData = new HashMap<String, List<Column>>();

			ResultSet rs = null;
			for (String tableName : DATA_TABLES.values()) {
				try {
					rs = st.executeQuery("select * from " +tableName+ " where 0=1");
					List<Column> columnData = getTableColumns(rs);
					tableData.put(tableName, Collections.unmodifiableList(columnData));
					logger.info("Collected " +columnData.size()+ " columns metadata for table " +tableName);
				} catch (Exception e) {
					throw new RuntimeException("Did not find metadata for table "+tableName, e);
				} finally {
					IoUtils.quiteClose(rs);
				}
			}
			return Collections.unmodifiableMap(tableData);
		} catch (Exception e) {
			// when testing we know this will fail
			if ( ! DataService.MODE.equals("TEST") ) {
				throw new RuntimeException("Failed it load table metadata",e);
			}
		} finally {
			IoUtils.quiteClose(st,cn);
		}
		return null;
	}



	protected static List<Column> loadTableMetadata(String tableName) {
		logger.info("Static Fetcher loadTableMetadata.");
		Connection cn = null;
		Statement  st = null;
		ResultSet rs = null;
		try {
			Context ctx = getContext();
			DataSource ds = (DataSource) ctx.lookup(SEDMAP_DS);
			cn = ds.getConnection();
			st = cn.createStatement();
			rs = st.executeQuery("select * from " +tableName+ " where 0=1");
			List<Column> columnData = getTableColumns(rs);
			logger.info("Collected " +columnData.size()+ " columns metadata for table " +tableName);
			return columnData;
		} catch (Exception e) {
			// when testing we know this will fail
			if ( ! DataService.MODE.equals("TEST") ) {
				throw new RuntimeException("Failed it load table metadata",e);
			}
		} finally {
			IoUtils.quiteClose(rs,st,cn);
		}
		return null;
	}


	public static List<Column> getTableColumns(ResultSet rs) throws SQLException {
		List<Column> columnData = new ArrayList<Column>();

		ResultSetMetaData md = rs.getMetaData();

		int columnCount = md.getColumnCount();
		for (int c = 1; c<= columnCount; c++) {
			String name = md.getColumnName(c);
			int    type = md.getColumnType(c);
			int    size = md.getScale(c);
			columnData.add( new Column(name, type, size, false) );
		}

		return  Collections.unmodifiableList(columnData);
	}



	public static List<Column> getTableMetadata(String tableName) {
		List<Column> meta = TABLE_METADATA.get(tableName);

		if (meta == null) {
			meta = loadTableMetadata(tableName);
		}

		return meta;
	}

	protected abstract InputStream handleLocalData(String descriptor, Filter filter, Formatter formatter)
			throws IOException, SQLException, NamingException;
	protected abstract InputStream handleNwisData(String descriptor, Filter filter, Formatter formatter)
			throws IOException, SQLException, NamingException;


	public void doFetch(HttpServletRequest req, FileDownloadHandler handler)
			throws ServletException, IOException {
		logger.debug("doFetch");

		String    dataTypes = getDataTypes(req);
		Formatter formatter = getFormatter(req);
		String    ogcXml    = getFilter(req);
		Filter    filter    = OgcUtils.ogcXml2Filter(ogcXml);

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
						fileData = handleNwisData(descriptor, filter, formatter);
					} else {
						fileData = handleLocalData(descriptor, filter, formatter);
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

}
