package gov.cida.sedmap.io.util;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;

public class SessionUtil {

	private static final Logger logger = Logger.getLogger(SessionUtil.class);


	public static String lookup(String property, String defaultValue) {
		try {
			InitialContext ctx = new InitialContext();
			String value = (String) ctx.lookup("java:comp/env/"+property);
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

		// pointer compare is fine
		if (propertyValue == dummy) return defaultValue;

		try {
			value = Integer.parseInt(propertyValue);
		} catch (Exception e) {
			logger.warn(e.getMessage());
			logger.warn("Failed to parse integer for property. " +property+":"+propertyValue+ "  Using default:"+defaultValue);
		}
		return value;
	}

}
