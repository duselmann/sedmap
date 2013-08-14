package gov.cida.sedmap.data;

import gov.cida.sedmap.io.FileDownloadHandler;
import gov.cida.sedmap.io.IoUtils;
import gov.cida.sedmap.web.DataService;

import java.io.IOException;
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
		return Collections.unmodifiableList( Arrays.asList("sites","data") );
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


	public abstract void doFetch(HttpServletRequest req, FileDownloadHandler handler)
			throws ServletException, IOException;
}
