package gov.cida.sedmap.data;

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
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.geotools.data.jdbc.FilterToSQL;
import org.geotools.data.jdbc.FilterToSQLException;
import org.opengis.filter.Filter;

public class JdbcFetcher extends Fetcher {


	private static final Logger logger = Logger.getLogger(JdbcFetcher.class);



	protected static class Results {
		Connection cn;
		Statement  st;
		ResultSet  rs;
	}






	@Override
	protected InputStream handleLocalData(String descriptor, Filter filter, Formatter formatter)
			throws IOException, SQLException, NamingException {
		InputStream fileData = null;
		Results rs = new Results();

		try {
			String where = new FilterToSQL().encodeToString(filter);
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

		} catch (FilterToSQLException e) {
			throw new SQLException("Failed to convert filter to sql where clause.",e);
		} finally {
			IoUtils.quiteClose(rs.rs, rs.st, rs.cn);
		}

		return fileData;
	}


	@Override
	protected InputStream handleNwisData(String descriptor, Filter filter, Formatter formatter)
			throws IOException, SQLException, NamingException {
		throw new RuntimeException("Not implemented.");
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
