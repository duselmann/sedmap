package gov.cida.sedmap.io.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import gov.cida.sedmap.io.IoUtils;
import gov.cida.sedmap.io.util.exceptions.SedmapException;
import gov.cida.sedmap.io.util.exceptions.SedmapException.OGCExceptionCode;
import gov.cida.sedmap.mail.SedmapDataMail;

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
	

	public static String handleExceptionResponseServices(HttpServletResponse res, SedmapException e) {
		String errorid = StrUtils.uniqueName(17);
		logger.error( errorid ); // log it right away in case something else goes wrong
		
		StringBuffer errorResponse = new StringBuffer();
		errorResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		errorResponse.append("<ExceptionReport version=\"1.0\">");
		errorResponse.append("<Exception exceptionCode=\"" + OGCExceptionCode.getStringFromType(e.getExceptionCode()) + "\">");
		
		// TODO I am concerned that this tells the user too much - to give them the exception message
		errorResponse.append("<ExceptionText>" + e.getExceptionMessage().trim() + " - ERROR CODE [" + errorid + "]</ExceptionText>");
		errorResponse.append("</Exception>");
		errorResponse.append("</ExceptionReport>");

		res.setContentType( "text/xml" );
		try (OutputStream out = res.getOutputStream()) {
			out.write(errorResponse.toString().getBytes());
		} catch (IOException ioe) {
			logger.error("failed to send error for " + errorid, ioe);
		}
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		
		String adminMessage = "Error caught in Sediment Data Portal.  OGC Exception Message sent to user:\n\n" 
				+ errorResponse.toString() + "\n\nFull Stack Trace: \n\n" + sw.toString() + "\n\n";
		
		logger.error("Sending Error Message to Sedmap Admin: [\n" + adminMessage + "\n]");
		SedmapDataMail mailer = new SedmapDataMail();
		mailer.notifyAdminOfError("Sediment Portal Error - ID [" + errorid + "]", adminMessage);
		
		IoUtils.quiteClose(pw,sw);
		
		return errorid;
	}

}
