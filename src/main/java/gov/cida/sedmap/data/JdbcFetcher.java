package gov.cida.sedmap.data;

import gov.cida.sedmap.io.InputStreamWithFile;
import gov.cida.sedmap.io.IoUtils;
import gov.cida.sedmap.io.WriterWithFile;
import gov.cida.sedmap.io.util.StrUtils;
import gov.cida.sedmap.ogc.FilterLiteralIterator;
import gov.cida.sedmap.ogc.FilterWithViewParams;
import gov.cida.sedmap.ogc.OgcUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.geotools.data.jdbc.FilterToSQLException;

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


	// nice little bundle for the JDBC results set needs
	protected static class Results {
		Connection cn;
		PreparedStatement ps;
		ResultSet  rs;
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
	protected InputStreamWithFile handleSiteData(String descriptor, FilterWithViewParams filter, Formatter formatter)
			throws IOException, SQLException, NamingException {

		InputStreamWithFile fileData = null;
		FileWriter tmpw = null;
		Results      rs = new Results();

		try {
			List<String> columnNames;
			if (descriptor.contains("daily")) {
				columnNames = Arrays.asList(DAILY_SITE_COLUMN_NAMES_FOR_SPREADSHEET);
			} else if (descriptor.contains("discrete")) {
				columnNames = Arrays.asList(DISCRETE_SITE_COLUMN_NAMES_FOR_SPREADSHEET);
			}
			else{
				throw new UnsupportedOperationException("Unknown descriptor: " + descriptor);
			}
			String        header = formatter.fileHeader(columnNames.iterator(), HeaderType.SITE);
			String           sql = buildQuery(descriptor, filter);
			logger.debug(sql);
			rs = initData(sql);
			getData(rs, filter, true);

			File tmp = File.createTempFile(descriptor +'_'+  StrUtils.uniqueName(12), formatter.getFileType());
			tmpw     = new FileWriter(tmp);
			logger.debug( tmp.getAbsolutePath() );

			//logger.debug(header);
			tmpw.write(header);
			while ( rs.rs.next() ) {
				String row = formatter.fileRow(new ResultSetColumnIterator(rs.rs));
				//logger.debug(row);
				tmpw.write(row);
			}

			fileData = new InputStreamWithFile(tmp);

		} catch (FilterToSQLException e) {
			throw new SQLException("Failed to convert filter to sql where clause.",e);
		} finally {
			IoUtils.quiteClose(tmpw, rs.rs, rs.ps, rs.cn);
			// TODO maybe close fileData?
		}

		return fileData;
	}
	@Override
	protected InputStreamWithFile handleDiscreteData(Iterator<String> sites, FilterWithViewParams filter, Formatter formatter)
			throws IOException, SQLException, NamingException {
		if ( !sites.hasNext() ) {
			// return nothing if there are no sites
			return null;
		}

		Results         rs = new Results();
		WriterWithFile tmp = null;
		String  descriptor = "discrete_data";

		try {

			String tableName = Fetcher.conf.DATA_TABLES.get(descriptor);
			String[] columnNames = Column.getColumnNames(getTableMetadata(tableName).iterator());
			String        header = formatter.fileHeader(Arrays.asList(columnNames).iterator(), HeaderType.DISCRETE);
			// TODO use IoUtils tmp file creator
			//			File   tmpFile = File.createTempFile(descriptor + StrUtils.uniqueName(12), formatter.getFileType());
			//			logger.debug(tmpFile.getAbsolutePath());
			//			tmp = new FileWriter(tmpFile);
			tmp = IoUtils.createTmpZipWriter(descriptor, formatter.getFileType());

			// open temp file
			while (sites.hasNext()) {
				try {
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
					rs = initData(sql);
					getData(rs, filter, false);


					//logger.debug(header);
					tmp.write(header);
					while (rs.rs.next()) {
						String row = formatter.fileRow(new ResultSetColumnIterator(rs.rs));
						//logger.debug(row);
						tmp.write(row);
					}

				} finally {
					IoUtils.quiteClose(rs.rs, rs.ps, rs.cn);
				}
			}
		} finally {
			//tmp.delete(); // TODO not for delayed download
			IoUtils.quiteClose(tmp);
		}

		return IoUtils.createTmpZipStream( tmp.getFile() );
	}


	protected String buildQuery(String descriptor, FilterWithViewParams filter) throws FilterToSQLException {
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



	protected Results initData(String sql) throws NamingException, SQLException {
		Results r = new Results();

		try {
			Context ctx = getContext();
			DataSource ds = (DataSource) ctx.lookup(jndiDS);
			r.cn = ds.getConnection();

			r.ps = r.cn.prepareStatement(sql);
		} catch (SQLException e) {
			logger.error(e);
			throw e;
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}

		return r;
	}
	protected Results getData(Results r, FilterWithViewParams filter, boolean doFilterValues) throws NamingException, SQLException {
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
			r.rs = r.ps.executeQuery();
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
