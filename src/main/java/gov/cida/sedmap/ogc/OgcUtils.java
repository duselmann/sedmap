package gov.cida.sedmap.ogc;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.geotools.data.jdbc.FilterToSQL;
import org.geotools.data.jdbc.FilterToSQLException;
import org.geotools.data.oracle.OracleDialect;
import org.geotools.data.oracle.OracleFilterToSQL;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.xml.Parser;
import org.opengis.filter.Filter;

public class OgcUtils {


	private static final Logger logger = Logger.getLogger(OgcUtils.class);
	private static final char INVALID_CHARS[] = new char[]{ '\'',';','(',')' };
	private static final Map<String, String> SQL_TRANSLATIONS = new HashMap<String, String>();

	// TODO if the translation becomes too much more complex - look into Sql Filter geotools API
	static {
		SQL_TRANSLATIONS.put("\\[","(");
		SQL_TRANSLATIONS.put("\\]",")");
		SQL_TRANSLATIONS.put("(\\S+)\\*","'$1%'");
		SQL_TRANSLATIONS.put("is like","like");
	}


	public static Filter ogcXml2Filter(String ogcXml) {
		try {
			logger.debug("ogcXml2Filter");
			// parse the OGC filter
			ByteArrayInputStream ogcStream = new ByteArrayInputStream( ogcXml.getBytes() );
			Parser parser = new Parser( new org.geotools.filter.v1_1.OGCConfiguration() );
			Filter filter = (Filter) parser.parse( ogcStream );
			return filter;
		} catch (Exception e) {
			// 			throws IOException, SAXException, ParserConfigurationException
			// TODO handle the exceptions
			throw new RuntimeException(e);
		}
	}


	public static String ogcXml2Sql(String ogcXml) {
		logger.debug("ogcXml2Sql");
		// parse the OGC filter
		Filter filter = ogcXml2Filter(ogcXml);

		// convert to SQL
		String sql="";
		try {
			JDBCDataStore ds = new JDBCDataStore();
			FilterToSQL  fsql = new OracleFilterToSQL(new OracleDialect(ds));
			sql = fsql.encodeToString(filter);
		} catch (FilterToSQLException e) {
			throw new RuntimeException("Failed to convert to SQL",e);
		}

		return sql;
	}


	protected static void checkForInvalidChars(String sql) {
		logger.debug("checkForInvalidChars");

		for (char invalid : INVALID_CHARS) {
			if (sql.indexOf(invalid)>=0) {
				throw new RuntimeException("found invalid char in query string - '" +invalid+ "' - \"" +sql+ "\"");
			}
		}
	}


	// TODO if the translation becomes too much more complex - look into Sql Filter geotools API
	protected static String sqlTranslation(String query) {
		logger.debug("sqlTranslation");
		checkForInvalidChars(query);

		String sql = sqlTranslation(query, SQL_TRANSLATIONS);
		return sql;
	}
	public static String sqlTranslation(String query, Map<String,String> fieldTranslation) {
		logger.debug("sqlTranslation");
		String sql = replace(query, fieldTranslation);
		return sql;
	}

	protected static String replace(String target, Map<String,String> replacements) {
		String replaced = target;
		for (String replace : replacements.keySet()) {
			String replacement = replacements.get(replace);
			replaced = replaced.replaceAll(replace, replacement);
		}
		return replaced;
	}

}
