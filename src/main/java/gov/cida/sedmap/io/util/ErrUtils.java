package gov.cida.sedmap.io.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

public class ErrUtils {

	private static final Logger logger = Logger.getLogger(ErrUtils.class);



	public static String handleExceptionResponse(HttpServletRequest req, HttpServletResponse res, Throwable e) {
		String errorid = StrUtils.uniqueName(9);
		logger.error( errorid ); // log it right away in case something else goes wrong

		try {
			HttpSession s = req.getSession();
			s.setAttribute("errorid",errorid);
			logger.error( errorid +" url: "+ req.getRequestURL() +'?'+ req.getQueryString() );
			logger.error("This marker tag is for the following exception: " + errorid, e);
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (Exception ioe) {
			logger.error("failed to send error for " + errorid, ioe);
		}

		return errorid;
	}

}
