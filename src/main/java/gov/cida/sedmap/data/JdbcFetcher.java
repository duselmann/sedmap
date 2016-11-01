package gov.cida.sedmap.data;

import static gov.cida.sedmap.data.DataFileMgr.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import gov.cida.sedmap.io.IoUtils;
import gov.cida.sedmap.io.WriterWithFile;
import gov.cida.sedmap.io.util.SessionUtil;
import gov.cida.sedmap.io.util.exceptions.SedmapException;
import gov.cida.sedmap.io.util.exceptions.SedmapException.OGCExceptionCode;
import gov.cida.sedmap.ogc.FilterLiteralIterator;
import gov.cida.sedmap.ogc.FilterWithViewParams;
import gov.cida.sedmap.ogc.OgcUtils;

public class JdbcFetcher extends Fetcher {


	private static final Logger logger = Logger.getLogger(JdbcFetcher.class);

	private static final Map<String,String> dataQueries  = new HashMap<String, String>();
	private static final String[] DEFAULT_SITE_COLUMN_NAMES = {
		"SITE_NO",
		"SNAME",
		"LATITUDE",
		"LONGITUDE",
		"NWISDA1",
		"STATE",
		"COUNTY_NAME",
		"ECO_L3_CODE",
		"ECO_L3_NAME",
		"ECO_L2_CODE",
		"ECO_L2_NAME",
		"ECO_L1_NAME",
		"ECO_L1_CODE",
		"HUC_REGION_NAME",
		"HUC_SUBREGION_NAME",
		"HUC_BASIN_NAME",
		"HUC_SUBBASIN_NAME",
		"HUC_2",
		"HUC_4",
		"HUC_6",
		"HUC_8",
		"PERM",
		"BFI",
		"KFACT",
		"RFACT",
		"PPT30",
		"URBAN",
		"FOREST",
		"AGRIC",
		"MAJ_DAMS",
		"NID_STOR",
		"CLAY",
		"SAND",
		"SILT",
		"BENCHMARK_SITE",
		"NHDP1",
		"NHDP5",
		"NHDP10",
		"NHDP20",
		"NHDP25",
		"NHDP30",
		"NHDP40",
		"NHDP50",
		"NHDP60",
		"NHDP70",
		"NHDP75",
		"NHDP80",
		"NHDP90",
		"NHDP95",
		"NHDP99"
	};
	static{
		StringBuilder sb = new StringBuilder();
		for(String name : DEFAULT_SITE_COLUMN_NAMES){
			sb.append("s.");
			sb.append(name);
			sb.append(",");
		}
		DEFAULT_SITE_COLUMN_NAMES_FOR_DOWNLOAD = sb.toString();

		//build column names for discrete site spreadsheet
		int discreteSiteColumnNamesForSpreadsheetLength = DEFAULT_SITE_COLUMN_NAMES.length + 1;
		String[] discreteSiteColumnNamesForSpreadsheet = Arrays.copyOf(DEFAULT_SITE_COLUMN_NAMES, discreteSiteColumnNamesForSpreadsheetLength);
		discreteSiteColumnNamesForSpreadsheet[discreteSiteColumnNamesForSpreadsheetLength -1] = "sample_count";
		DISCRETE_SITE_COLUMN_NAMES_FOR_SPREADSHEET = discreteSiteColumnNamesForSpreadsheet;

		//build column names for daily site spreadsheet
		int dailySiteColumnNamesForSpreadsheetLength = DEFAULT_SITE_COLUMN_NAMES.length + 1;
		String[] dailySiteColumnNamesForSpreadsheet = Arrays.copyOf(DEFAULT_SITE_COLUMN_NAMES, dailySiteColumnNamesForSpreadsheetLength);
		dailySiteColumnNamesForSpreadsheet[dailySiteColumnNamesForSpreadsheetLength - 1] = "sample_years";
		DAILY_SITE_COLUMN_NAMES_FOR_SPREADSHEET = dailySiteColumnNamesForSpreadsheet;

	}
	private static final String DEFAULT_SITE_COLUMN_NAMES_FOR_DOWNLOAD;
	public static final String[] DISCRETE_SITE_COLUMN_NAMES_FOR_SPREADSHEET;
	public static final String[] DAILY_SITE_COLUMN_NAMES_FOR_SPREADSHEET;
	public static final String DEFAULT_DAILY_SITE_SQL = "" // this is for consistent formatting
			+ " select "
			+ DEFAULT_SITE_COLUMN_NAMES_FOR_DOWNLOAD
			+ "   NVL(y.sample_years,0) as sample_years "
			+ " from sedmap.DAILY_STATIONS_DL s "
			+ " left join ( "
			+ "    select site_no, count(*) sample_years "
			+ "      from sedmap.daily_year y "
			+ "     where y.SAMPLE_YEAR>=? " //yr1
			+ "       and y.SAMPLE_YEAR<=? " //yr2
			+ "     group by site_no) y "
			+ "   on (y.site_no = s.site_no) "
			+ " where sample_years > 0 and ";

	static final String DEFAULT_DISCRETE_SITE_SQL = ""
			+ " select "
			+ DEFAULT_SITE_COLUMN_NAMES_FOR_DOWNLOAD
			+ "   NVL(y.sample_count,0) as sample_count "
			+ " from sedmap.DISCRETE_STATIONS_DL s "
			+ " left join ( "
			+ "    select site_no, count(*) sample_count "
			+ "      from sedmap.discrete_sample_fact  "
			+ "     where EXTRACT(YEAR FROM \"DATETIME\")>=? " // yr1
			+ "       and EXTRACT(YEAR FROM \"DATETIME\")<=? " // yr2
			+ "     group by site_no) y "
			+ "   on (y.site_no = s.site_no) "
			+ " where sample_count > 0 and ";

	static final String DEFAULT_DISCRETE_DATA_SQL = ""
			+ " select * "
			+ "   from sedmap.discrete_sample_fact "
			+ "  where EXTRACT(YEAR FROM \"DATETIME\")>=? " // yr1
			+ "    and EXTRACT(YEAR FROM \"DATETIME\")<=? " // yr2
			+ "    and site_no in (_siteList_) " // replaced with list of sites
			+ "  order by site_no, datetime";


	protected String jndiDS;

	static {
		// default queries that could be changed other need like testing
		putQuery("daily_sites",           DEFAULT_DAILY_SITE_SQL);
		putQuery("discrete_sites",        DEFAULT_DISCRETE_SITE_SQL);
		putQuery("discrete_data",         DEFAULT_DISCRETE_DATA_SQL);
	}
	public static void putQuery(String descriptor, String sql) {
		dataQueries.put(descriptor, sql);
	}
	protected static String getQuery(String descriptor) {
		return dataQueries.get(descriptor);
	}


	public JdbcFetcher() {
	}
	public JdbcFetcher(String jndiJdbc) {
		jndiDS = jndiJdbc;
	}

	@Override
	public Fetcher initJndiJdbcStore(String jndiJdbc) throws IOException {
		jndiDS = jndiJdbc;
		return this;
	}



	@Override
	protected File handleSiteData(String descriptor, FilterWithViewParams filter, Formatter formatter)
			throws Exception {

		try (Results rs = new Results()) {
			List<String> columnNames;
			if (descriptor.contains("daily")) {
				columnNames = Arrays.asList(DAILY_SITE_COLUMN_NAMES_FOR_SPREADSHEET);
			} else if (descriptor.contains("discrete")) {
				columnNames = Arrays.asList(DISCRETE_SITE_COLUMN_NAMES_FOR_SPREADSHEET);
			} else {
				throw new SedmapException(OGCExceptionCode.OperationNotSupported, new UnsupportedOperationException("Unknown descriptor: " + descriptor));
			}
			String header = formatter.fileHeader(columnNames.iterator(), HeaderType.SITE);
			String    sql = buildQuery(descriptor, filter);
			logger.debug(sql);
			openQuery(rs, sql);
			fetchData(rs, filter, true);

			try ( WriterWithFile tmp = IoUtils.createTmpZipWriter(descriptor, formatter.getFileType()) ) {
				//logger.debug(header);
				tmp.write(header);
				while ( rs.rs.next() ) {
					String row = formatter.fileRow(new ResultSetColumnIterator(rs.rs));
					//logger.debug(row);
					tmp.write(row);
				}
				// preserve the file for once out of the block where java7 closes it
				return tmp.getFile();
			}
		}
	}
	@Override
	protected File handleDiscreteData(Iterator<String> sites, FilterWithViewParams filter, Formatter formatter)
			throws Exception {
		if ( !sites.hasNext() ) {
			// return nothing if there are no sites
			return null;
		}

		String descriptor    = DISCRETE_FILENAME;
		String tableName     = Fetcher.conf.DATA_TABLES.get(descriptor);
		String[] columnNames = Column.getColumnNames(getTableMetadata(tableName).iterator());
		String header        = formatter.fileHeader(Arrays.asList(columnNames).iterator(), HeaderType.DISCRETE);
		
		try (WriterWithFile tmp = IoUtils.createTmpZipWriter(descriptor, formatter.getFileType()) ) {

			// open temp file
			while ( sites.hasNext() ) {
				try (Results rs = new Results()) {
					int batch = 0;
					StringBuilder sitesClause = new StringBuilder();
					String join="";
					while (++batch<999 && sites.hasNext() ) {
						sitesClause.append(join).append("'").append(sites.next()).append("'");
						join=",";
					}
					String sql = getQuery(descriptor);
					sql=sql.replace("_siteList_", sitesClause.toString() );
					logger.debug(sql);
					openQuery(rs,sql);
					fetchData(rs, filter, false);


					//logger.debug(header);
					tmp.write(header);
					while (rs.rs.next()) {
						String row = formatter.fileRow(new ResultSetColumnIterator(rs.rs));
						//logger.debug(row);
						tmp.write(row);
					}
				}
			}
			return tmp.getFile();
		}
	}


	protected String buildQuery(String descriptor, FilterWithViewParams filter) throws SedmapException {
		//		FilterToSQL trans = new FilterToSQL();
		//		trans.setInline(true);

		String where = OgcUtils.ogcXmlToParameterQueryWhereClause(filter.getFilter());
		where = where.replaceAll("\"?SITE_NO\"?", "s.site_no"); // TODO this is a hack

		//		trans.encodeToString(filter);
		String sql = getQuery(descriptor) + where; // + getQuery(descriptor+"_amount");
		sql = where.length()==2 ?sql.substring(0,sql.length()-7) :sql;
		return sql + " order by s.site_no ";
	}



	protected String ogc2sql(String ogcXml) {
		String where = "";

		if ("".equals(ogcXml)) return where;
		try {
			where = OgcUtils.ogcXmlToParameterQueryWherClause(ogcXml);
			if (where != null && where.trim().length()>0) {
				where = OgcUtils.sqlTranslation(where, conf.FIELD_TRANSLATIONS);
			} else {
				where = "";
			}
		} catch (Exception e) {
			// TODO empty results and err msg
			throw new RuntimeException("Failed to parse OGC", e);
		}

		return where;
	}



	protected Results openQuery(Results results, String sql) throws SedmapException {
		try {
			DataSource ds = SessionUtil.lookupDataSource(jndiDS);
			results.cn = ds.getConnection();			
		} catch (NamingException e) {
			String msg = "Error fetching JDBC data source";
			logger.error(msg,e);
			throw new SedmapException(msg, e);
		} catch (SQLException e) {
			String msg = "Error fetching JDBC connection";
			logger.error(msg,e);
			throw new SedmapException(msg, e);
		}
		
		try {
			results.ps = results.cn.prepareStatement(sql);
		} catch (SQLException e) {
			String msg = "Error creating SQL statement on JDBC connection";
			logger.error(msg,e);
			throw new SedmapException(msg, e);
		}

		return results;
	}
	protected Results fetchData(Results r, FilterWithViewParams filter, boolean doFilterValues) throws NamingException, SQLException, Exception {
		try {
			int index = 1;
			for (String value : filter) {
				logger.debug("setting value " + value);
				r.ps.setString(index++, value);
			}

			// this is because only sites uses the filter
			if (doFilterValues) {
				FilterLiteralIterator values = new FilterLiteralIterator(filter.getFilter());
				for (String value : values) {
					if ( ! value.endsWith("%") ) { // TODO this compensates for geoTools inconsistency
						logger.debug("setting value " + value);
						r.ps.setString(index++, value);
					} else {
						logger.debug("skipping like " + value);
					}
				}
			}
			logger.info("executeQuery: start");
			r.rs = r.ps.executeQuery();
			logger.info("executeQuery: finish");
		} catch (SQLException e) {
			logger.error("executeQuery: error", e);
			throw new SedmapException("Error while querying discrete sites", e);
		} catch (Exception e) {
			String msg = "Non-SQL error when querying or discrete sites.";
			logger.error(msg, e);
			throw new SedmapException(msg, e);
		}

		return r;
	}
}
