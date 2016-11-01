package gov.cida.sedmap.io.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class SessionUtil {

	private static final Logger logger = Logger.getLogger(SessionUtil.class);

	protected static Context ctx;
	
	public static void setContext(Context context) {
		ctx = context;
	}
	
	public static Context getContext() throws NamingException {
		if (ctx == null) {
			ctx = new InitialContext();
		}
		return ctx;
	}

	public static DataSource lookupDataSource(String jndiDS) throws NamingException {
		return (DataSource) getContext().lookup(jndiDS);
	}
	
	public static String lookup(String property, String defaultValue) {
		try {
			String value = (String) getContext().lookup("java:comp/env/"+property);
			return value;
		} catch (Exception e) {
			logger.warn(e.getMessage());
			logger.warn("Using default value, "+ defaultValue +", for " + property);
			return defaultValue;
		}
	}



	public static int lookup(String property, int defaultValue) {
		int value = defaultValue;

		String dummy = ""+defaultValue;
		String propertyValue = lookup(property, dummy);

		try {
			value = Integer.parseInt(propertyValue);
		} catch (Exception e) {
			logger.warn(e.getMessage());
			logger.warn("Failed to parse integer for property. " +property+":"+propertyValue+ "  Using default:"+defaultValue);
		}
		return value;
	}

}
