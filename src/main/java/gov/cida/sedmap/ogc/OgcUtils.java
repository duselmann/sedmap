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
import org.geotools.data.oracle.OracleDialect;
import org.geotools.data.oracle.OracleFilterToSQL;
import org.geotools.filter.AbstractFilter;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.jdbc.JDBCDataStoreFactory;
import org.geotools.jdbc.JDBCFeatureReader;
import org.geotools.jdbc.JDBCJNDIDataStoreFactory;
import org.geotools.jdbc.NonIncrementingPrimaryKeyColumn;
import org.geotools.jdbc.PrimaryKey;
import org.geotools.jdbc.PrimaryKeyColumn;
import org.geotools.xml.Parser;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.PropertyName;

import gov.cida.sedmap.io.util.StrUtils;
import gov.cida.sedmap.io.util.exceptions.SedmapException;
import gov.cida.sedmap.io.util.exceptions.SedmapException.OGCExceptionCode;

public class OgcUtils {


	private static final Logger logger = Logger.getLogger(OgcUtils.class);
	private static final char INVALID_CHARS[] = new char[]{ '\'',';','(',')' };
	private static final Map<String, String> SQL_TRANSLATIONS = new HashMap<String, String>();
	@SuppressWarnings("deprecation")
	private static final AbstractFilter ALL_FILTER = new AbstractFilter(null) {
		@Override
		public boolean evaluate(Object object) {
			return Filter.INCLUDE.evaluate(object);
		}
		@Override
		public org.geotools.filter.Filter or(Filter filter) {
			return null;
		}
		@Override
		public org.geotools.filter.Filter not() {
			return null;
		}
		@Override
		public org.geotools.filter.Filter and(Filter filter) {
			return null;
		}
	};

	// TODO if the translation becomes too much more complex - look into Sql Filter geotools API
	static {
		SQL_TRANSLATIONS.put("\\[","(");
		SQL_TRANSLATIONS.put("\\]",")");
		SQL_TRANSLATIONS.put("(\\S+)\\*","'$1%'");
		SQL_TRANSLATIONS.put("is like","like");
	}


	public static AbstractFilter ogcXmlToFilter(String ogcXml) throws Exception {
		if (StrUtils.isEmpty(ogcXml)) {
			return OgcUtils.ALL_FILTER;
		}

		try {
			logger.debug("ogcXml2Filter");
			// parse the OGC filter
			ByteArrayInputStream ogcStream = new ByteArrayInputStream( ogcXml.getBytes() );
			Parser parser = new Parser( new org.geotools.filter.v1_1.OGCConfiguration() );
			AbstractFilter filter = (AbstractFilter) parser.parse( ogcStream );
			return filter;
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error("Due to internal exception caught, throwing InvalidParameterValue OGC error for error handling on the client side.");
			throw new SedmapException(OGCExceptionCode.InvalidParameterValue, new Exception("Failed to parse OGC.",e));
		}
	}

	public static String ogcXmlToParameterQueryWherClause(String ogcXml) throws Exception {
		logger.debug("ogcXml2Sql");
		// parse the OGC filter
		Filter filter = ogcXmlToFilter(ogcXml);
		return ogcXmlToParameterQueryWhereClause(filter);
	}
	public static String ogcXmlToParameterQueryWhereClause(Filter filter) throws SedmapException {
		// convert to SQL
		String sql=" 1=1 ";
		if (isAllFilter(filter)) {
			return sql;
		}

		try {
			JDBCDataStore ds = new JDBCDataStore();
			OracleFilterToSQL osql = new OracleFilterToSQL(new OracleDialect(ds));

			List<PrimaryKeyColumn> columns = new ArrayList<PrimaryKeyColumn>();
			columns.add(new NonIncrementingPrimaryKeyColumn("SITE_NO", String.class));
			PrimaryKey pk = new PrimaryKey("DIALY_STATION", columns);
			osql.setPrimaryKey(pk);
			osql.setInline(true); // prevent addition of WHERE key word in clause
			StringWriter buf = new StringWriter();
			osql.setWriter(buf);
			osql.encode(filter);

			sql = buf.getBuffer().toString();
		} catch (Exception e) {
			logger.error("Failed to convert to SQL.  Exception is: " + e.getMessage());
			logger.error("Due to internal exception caught, throwing OperationNotSupported OGC error for error handling on the client side.");
			throw new SedmapException(OGCExceptionCode.OperationNotSupported, new Exception("Failed to parse OGC.",e));
		}

		return sql;
	}


	protected static void checkForInvalidChars(String sql) throws Exception {
		logger.debug("checkForInvalidChars");

		for (char invalid : INVALID_CHARS) {
			if (sql.indexOf(invalid)>=0) {
				logger.error("found invalid char in query string - '" +invalid+ "' - \"" +sql+ "\"");
				logger.error("Due to internal exception caught, throwing InvalidParameterValue OGC error for error handling on the client side.");
				throw new SedmapException(OGCExceptionCode.InvalidParameterValue, new Exception("Failed to parse OGC.  Found invalid char in query string - '" +invalid+ "' - \"" +sql+ "\""));
			}
		}
	}


	// TODO if the translation becomes too much more complex - look into Sql Filter geotools API
	protected static String sqlTranslation(String query) throws Exception {
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



	public static DataStore jndiOracleDataStore(String jndiName) throws Exception {
		Map<String, Object> dataStoreEnv = new HashMap<String, Object>();
		// dataStoreEnv.put( JDBCDataStoreFactory.SCHEMA.getName(), "sedmap"); // OPTIONAL
		dataStoreEnv.put( JDBCDataStoreFactory.DBTYPE.getName(), "oracle");
		dataStoreEnv.put( JDBCDataStoreFactory.EXPOSE_PK.getName(), true);
		dataStoreEnv.put( JDBCJNDIDataStoreFactory.JNDI_REFNAME.getName(), jndiName);
		
		DataStore store;
		try {
			store =  DataStoreFinder.getDataStore(dataStoreEnv);
		} catch (Exception e) {
			String msg = "Error fetching oracle JDBC data source";
			logger.error(msg,e);
			throw new SedmapException(msg, e);
		}

		return store;
	}



	public static JDBCFeatureReader executeQuery(DataStore store, String tableName, Filter filter, String ... properties)
			throws Exception {
		Transaction trans = new DefaultTransaction("read-only");
		return executeQuery(trans, store, tableName, filter, properties);
	}
	
	public static JDBCFeatureReader executeQuery(Transaction trans, DataStore store, String tableName, Filter filter, String ... properties)
			throws SedmapException {
		Query query;
		if (properties.length==0) {
			query = new Query(tableName, filter);
		} else {
			query = new Query(tableName, filter, properties);
		}

		JDBCFeatureReader reader;
		
		try {
			reader = (JDBCFeatureReader) store.getFeatureReader(query, trans);
		} catch (IOException e) {
			String msg = "Error getting feature reader";
			logger.error(msg,e);
			throw new SedmapException(msg, e);
		}
		
		return reader;
	}

	@SafeVarargs
	public static String findFilterValue(Filter filter, String param, Class<? extends BinaryComparisonOperator> ... opcodes) {
		return findFilterValue(new FilterWrapper(filter), param, opcodes);
	}
	@SafeVarargs
	public static String findFilterValue(FilterWrapper filter, String param, Class<? extends BinaryComparisonOperator> ... opcodes) {
		Filter found = findFilter(false, filter, param, opcodes);
		return getValue(found);
	}

	// TODO this will likely need some work to be more robust
	// used to find the literal value of the parameter.
	// this might not be used in favor of the removeFilter method
	@SafeVarargs
	public static Filter findFilter(Filter filter, String param, Class<? extends BinaryComparisonOperator> ... opcodes) {
		return findFilter(false, new FilterWrapper(filter), param, opcodes);
	}

	// TODO this will likely need some work to be more robust
	// removes a filter and returns is litter value
	// used to remove parameter query values not associated with columns in the table
	// for example: yearStart and yearEnd would not match up to a column
	@SafeVarargs
	public static String removeFilter(Filter filter, String param, Class<? extends BinaryComparisonOperator> ... opcodes) {
		return removeFilter(new FilterWrapper(filter), param, opcodes);
	}
	@SafeVarargs
	public static String removeFilter(FilterWrapper filter, String param, Class<? extends BinaryComparisonOperator> ... opcodes) {
		Filter removed = findFilter(true, filter, param, opcodes);
		return getValue(removed);
	}

	@SafeVarargs
	protected static Filter findFilter(boolean remove, FilterWrapper filter, String param, Class<? extends BinaryComparisonOperator> ... opcodes) {
		Filter found = null;
		if ( isAllFilter(filter) ) {
			return found;
		}

		if ( filter.isaLogicFilter() ) {
			for (Filter child : filter.getChildren() ) {
				FilterWrapper wrapped = new FilterWrapper(child, filter);
				found = findFilter(remove, wrapped, param, opcodes);
				if (found != null) {
					return found;
				}
			}
		} else { // TODO this will fail on geometry filters and will need attention when that is incorporated
			// expression1 is the parameter name while experssion2 is the literal value
			PropertyName property = filter.getExpression1();
			if ( property.getPropertyName().equalsIgnoreCase(param) ) {
				boolean isOpcode  =  opcodes.length == 0; // no given opcode means accept parameter name match
				for (Class<?> opcode : opcodes) {
					// assignment is desired behavior, the additional parenthesis prevent the compile error.
					if ( (isOpcode = filter.isInstanceOf(opcode)) ) {
						break;
					}
				}
				if (isOpcode) {
					if (remove) {
						filter.remove();
					}
					return filter.getInnerFilter();
				}
			}
		}

		return found;
	}


	// gets the value from a the literal portion of a filter
	// returns null if the filter is not a literal
	protected static String getValue(Filter filter) {
		String value = ""; // TODO do we want null?
		if (filter == null) return value;

		FilterWrapper fw;
		if ( filter instanceof FilterWrapper ) {
			fw = (FilterWrapper)filter;
		} else {
			fw = new FilterWrapper(filter);
		}

		return fw.getExpression2();
	}

	public static boolean isAllFilter(Filter filter) {
		if (filter instanceof FilterWrapper) {
			filter = ((FilterWrapper)filter).getInnerFilter();
		}
		return filter==ALL_FILTER;
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
