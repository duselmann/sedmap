package gov.cida.sedmap.io.util;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

public class ErrUtils {

	private static final Logger logger = Logger.getLogger(ErrUtils.class);



	public static void handleExceptionResponse(HttpServletRequest req, HttpServletResponse res, Throwable e) {
		String tag    = StrUtils.uniqueName(9);
		HttpSession s = req.getSession();
		s.setAttribute("errorid",tag);
		logger.error( tag +" url: "+ req.getRequestURL() +'?'+ req.getQueryString() );
		logger.error("This marker tag is for the following exception: " + tag, e);
		try {
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (IOException ioe) {
			logger.error("failed to send error for " + tag, ioe);
		}
	}

}
