package gov.cida.sedmap.io.util;

import gov.cida.sedmap.io.util.exceptions.SedmapException;
import gov.cida.sedmap.io.util.exceptions.SedmapException.OGCExceptionCode;

import java.io.IOException;

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
	
	/**
	 * ErrUtils.handleExceptionResponseServices()
	 * @param req
	 * @param res
	 * @param e
	 * @return
	 * <br /><br />
	 * This method will push a standard OGC error response back to the client dictating what
	 * the issue was.
	 * <br /><br />
	 * OGC Error Messages taken from: http://www.ogcnetwork.net/node/198
	 * <br /><br />
	 * An example of an OGC XML error is:<br /><br />
	 * <pre>
	 * {@code
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * <ExceptionReport version="1.0">
	 *    <Exception exceptionCode="ResourceNotFound">
	 *       <ExceptionText>Internal Server error. Java exception:
	 *         java.lang.NullPointerException
	 *         at org.apache.xerces.framework.XMLParser.parse(XMLParser.java:1094)
	 *         at com.wolf.tra.xglanls.XGLanls.analyse(XGLanls.java:32)
	 *       <ExceptionText>
	 *    </Exception>
	 * </ExceptionReport>
	 * }
	 * 
	 * <table border="1" cellpadding="8" cellspacing="1"><tbody><tr><td width="153" valign="top"><p align="center"><strong>exceptionCode value </strong></p></td><td width="149" valign="top"><p align="center"><strong>Meaning of code </strong></p></td><td width="149" valign="top"><p align="center"><strong>“locator” value </strong></p></td></tr><tr><td width="153" valign="top"><p>OperationNotSupported </p></td><td width="149" valign="top"><p>Request is for an operation that is not supported by this server </p></td><td width="149" valign="top"><p>Name of operation not supported </p></td></tr><tr><td width="153" valign="top"><p>MissingParameterValue </p></td><td width="149" valign="top"><p>Operation request does not include a parameter value, and this server did not declare a default value for that parameter </p></td><td width="149" valign="top"><p>Name of missing parameter </p></td></tr><tr><td width="153" valign="top"><p>InvalidParameterValue </p></td><td width="149" valign="top"><p>Operation request contains an invalid parameter value a </p></td><td width="149" valign="top"><p>Name of parameter with invalid value </p></td></tr><tr><td width="153" valign="top"><p>ResourceNotFound</p></td><td width="149" valign="top"><p>This is a configuration error. This could be a plugin not found in a java environment where the classloader misses the class in the CLASSPATH or a required interface in a class is not implemented.</p></td><td width="149" valign="top"><p>A short description what went wrong (i.e. the name of the faulty or required class).</p></td></tr><tr><td width="153" valign="top"><p>NoApplicableCode </p></td><td width="149" valign="top"><p>No other exceptionCode specified by this service and server applies to this exception </p></td><td width="149" valign="top"><p>None, omit “locator” parameter </p></td></tr><tr><td width="451" colspan="3" valign="top"><p>a When an invalid parameter value is received, it seems desirable to place the invalid value(s) in ExceptionText string(s) associated with the InvalidParameterValue value. </p></td></tr></tbody></table>
	 * </pre>
	 * <br /><br />
	 * 	Exception Codes:
	 * 		OperationNotSupported
	 * 		MissingParameterValue
	 * 		InvalidParameterValue
	 * 		ResourceNotFound
	 * 		NoApplicableCode
	 * 
	 * <br /><br />
	 * Its probably smart not to show a stack trace w/ class names and line numbers so
	 * in the <ExceptionText> element we will just put the exception message
	 * 		
	 */
	public static String handleExceptionResponseServices(HttpServletResponse res, SedmapException e) {
		String errorid = StrUtils.uniqueName(17);
		logger.error( errorid ); // log it right away in case something else goes wrong
		
		String exceptionCode = "<Exception exceptionCode=\"" + OGCExceptionCode.getStringFromType(e.getExceptionCode()) + "\">";
		String exceptionText = "<ExceptionText>" + e.getExceptionMessage().trim() + " - ERROR CODE (" + errorid + ")</ExceptionText>";

		res.setContentType( "text/xml" );
		try {
			res.getOutputStream().write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>".getBytes());
			res.getOutputStream().write("<ExceptionReport version=\"1.0\">".getBytes());
			res.getOutputStream().write(exceptionCode.getBytes());
			res.getOutputStream().write(exceptionText.getBytes());
			res.getOutputStream().write("</Exception>".getBytes());
			res.getOutputStream().write("</ExceptionReport>".getBytes());
			res.getOutputStream().flush();
			res.getOutputStream().close();
		} catch (IOException e1) {
			logger.error("failed to send error for " + errorid, e1);
		}

		return errorid;
	}

}
