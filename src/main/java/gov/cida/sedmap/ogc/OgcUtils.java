package gov.cida.sedmap.ogc;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Query;
import org.geotools.data.Transaction;
import org.geotools.data.jdbc.FilterToSQLException;
import org.geotools.data.oracle.OracleDialect;
import org.geotools.data.oracle.OracleFilterToSQL;
import org.geotools.filter.BinaryComparisonAbstract;
import org.geotools.filter.BinaryLogicAbstract;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.jdbc.JDBCDataStoreFactory;
import org.geotools.jdbc.JDBCFeatureReader;
import org.geotools.jdbc.JDBCJNDIDataStoreFactory;
import org.geotools.jdbc.NonIncrementingPrimaryKeyColumn;
import org.geotools.jdbc.PrimaryKey;
import org.geotools.jdbc.PrimaryKeyColumn;
import org.geotools.xml.Parser;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;

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

	public static String ogcXmlToParameterQueryWherClause(String ogcXml) {
		logger.debug("ogcXml2Sql");
		// parse the OGC filter
		Filter filter = ogcXml2Filter(ogcXml);
		return ogcXmlToParameterQueryWherClause(filter);
	}
	public static String ogcXmlToParameterQueryWherClause(Filter filter) {
		// convert to SQL
		String sql="";
		try {
			JDBCDataStore ds = new JDBCDataStore();
			OracleFilterToSQL osql = new OracleFilterToSQL(new OracleDialect(ds));

			List<PrimaryKeyColumn> columns = new ArrayList<PrimaryKeyColumn>();
			columns.add(new NonIncrementingPrimaryKeyColumn("USGS_STATION_ID", String.class));
			PrimaryKey pk = new PrimaryKey("SM_DIALY_STATION", columns);
			osql.setPrimaryKey(pk);
			osql.setInline(true); // prevent addition of WHERE key word in clause
			StringWriter buf = new StringWriter();
			osql.setWriter(buf);
			osql.encode(filter);

			sql = buf.getBuffer().toString();
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



	public static DataStore jndiOracleDataStore(String jndiName) throws IOException {
		Map<String, Object> dataStoreEnv = new HashMap<String, Object>();
		// dataStoreEnv.put( JDBCDataStoreFactory.SCHEMA.getName(), "sedmap"); // OPTIONAL
		dataStoreEnv.put( JDBCDataStoreFactory.DBTYPE.getName(), "oracle");
		dataStoreEnv.put( JDBCDataStoreFactory.EXPOSE_PK.getName(), true);
		dataStoreEnv.put( JDBCJNDIDataStoreFactory.JNDI_REFNAME.getName(), jndiName);
		DataStore store =  DataStoreFinder.getDataStore(dataStoreEnv);

		return store;
	}



	public static JDBCFeatureReader executeQuery(DataStore store, String tableName, Filter filter, String ... properties)
			throws IOException {
		Transaction trans = new DefaultTransaction("read-only");
		return executeQuery(trans, store, tableName, filter, properties);
	}
	public static JDBCFeatureReader executeQuery(Transaction trans, DataStore store, String tableName, Filter filter, String ... properties)
			throws IOException {
		Query query;
		if (properties.length==0) {
			query = new Query(tableName, filter);
		} else {
			query = new Query(tableName, filter, properties);
		}

		JDBCFeatureReader reader = (JDBCFeatureReader) store.getFeatureReader(query, trans);
		return reader;
	}

	public static String findFilterValue(Filter filter, String param) {
		filter = findFilter(filter, param);

		String value = null;

		if (filter instanceof BinaryComparisonAbstract) {
			BinaryComparisonAbstract comp = (BinaryComparisonAbstract) filter;
			Literal literal = (Literal) comp.getExpression2();
			value = literal.getValue().toString();
		}

		return value;
	}


	// TODO this will likely need some work to be more robust
	// used to find the literal value of the parameter.
	// this might not be used in favor of the removeFilter method
	public static Filter findFilter(Filter filter, String param) {
		Filter found = null;

		FilterWrapper wrapper = new FilterWrapper( filter );

		if ( wrapper.isaLogicFilter() ) {
			BinaryLogicAbstract logical = (BinaryLogicAbstract) filter;
			for (Filter child : logical.getChildren() ) {
				found = findFilter(child, param);
				if (found != null) {
					return found;
				}
			}
		} else { // TODO this will fail on geometry filters and will need attention when that is incorporated
			BinaryComparisonAbstract comp = (BinaryComparisonAbstract) filter;
			// expression1 is the parameter name while experssion2 is the literal value
			PropertyName property = (PropertyName) comp.getExpression1();
			if ( property.getPropertyName().equals(param) ) {
				return filter;
			}
		}

		return found;
	}

	// TODO this will likely need some work to be more robust
	// removes a filter and returns is litter value
	// used to remove parameter query values not associated with columns in the table
	// for example: yearStart and yearEnd would not match up to a column
	public static String removeFilter(Filter filter, String param) {
		return removeFilter(new FilterWrapper(filter), param);
	}

	public static String removeFilter(FilterWrapper filter, String param) {
		String value = null;

		if ( filter.isaLogicFilter() ) {
			for (Filter child : filter.getChildren() ) {
				FilterWrapper wrapped = new FilterWrapper(child, filter);
				value = removeFilter(wrapped, param);
				if (value != null) {
					return value;
				}
			}
		} else { // TODO this will fail on geometry filters and will need attention when that is incorporated
			// expression1 is the parameter name while experssion2 is the literal value
			PropertyName property = filter.getExpression1();
			if ( property.getPropertyName().equals(param) ) {

				filter.remove();
				return filter.getExpression2();
			}
		}

		return value;
	}


	// gets the value from a the literal portion of a filter
	// returns null if the filter is not a literal
	public static String getValue(Filter filter) {
		FilterWrapper fw = new FilterWrapper(filter);
		if ( filter instanceof FilterWrapper ) {
			fw = (FilterWrapper)filter;
		}
		if ( fw.isaLiteralFilter() ) {
			return fw.getExpression2();
		}
		return null;
	}

	//	StringBuilder buf = new StringBuilder();
	//	while (reader.hasNext()) {
	//		SimpleFeature feature = reader.next();
	//
	//		int attribCount = feature.getAttributeCount();
	//		int expectedCount = 4;
	//		assertEquals(expectedCount, attribCount);
	//
	//		List<Object> values = feature.getAttributes();
	//		Collection<Property> props = feature.getProperties();
	//		Iterator<Property> iterator = props.iterator();
	//
	//		for (int a=0; a<4; a++) {
	//			Property prop = iterator.next();
	//			buf.append(prop.getName());
	//			buf.append(":");
	//
	//			// String attr = feature.getAttribute(a).toString();
	//			String attr = values.get(a).toString();
	//			buf.append(attr);
	//			buf.append(", ");
	//		}
	//		buf.append(IoUtils.LINE_SEPARATOR);
	//
	//	}


}
