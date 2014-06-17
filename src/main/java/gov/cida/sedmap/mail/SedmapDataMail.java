package gov.cida.sedmap.mail;

import gov.cida.sedmap.io.util.SessionUtil;
import gov.cida.sedmap.io.util.exceptions.SedmapException;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;

public class SedmapDataMail {
	private static final Logger logger = Logger.getLogger(SedmapDataMail.class);
	
	//public static final String ADMIN_ERROR_EMAIL	  = "SedimentPortal_HELP@usgs.gov";
	public static final String ADMIN_ERROR_EMAIL	  = "prusso@usgs.gov";

	protected static final String MSG_ENV_KEY         = "sedmap/email/body";
	protected static final String MSG_DEFAULT         = "\nYour data file is ready for download.\n\nClick on the link below to commence download.\n\nYour file will be retained for 7 days.\n\n";
	protected static final String MSG_BODY;

	protected static final String ERR_ENV_KEY         = "sedmap/email/body";
	protected static final String ERR_DEFAULT         = "\nThere was an error while downloading your data.\n\nSupport has been contacted.\n\n Here is your trouble ticket: ";
	protected static final String ERR_BODY;

	protected static final String LINK_ENV_KEY        = "sedmap/email/link";
	protected static final String LINK_DEFAULT        = "http://localhost:8080/sediment/download?file=";
	protected static final String LINK_STUB;

	protected static final String SENDER_ADDR_ENV_KEY = "sedmap/email/address";
	protected static final String SENDER_ADDR_DEFAULT = "SedimentPortal_HELP@usgs.gov";
	protected static final String SENDER_ADDR;

	protected static final String SENDER_NAME_ENV_KEY = "sedmap/email/name";
	protected static final String SENDER_NAME_DEFAULT = "Sediment Data Portal";
	protected static final String SENDER_NAME;

	protected static final String SUBJECT_ENV_KEY     = "sedmap/email/subject";
	protected static final String SUBJECT_DEFAULT     = "Sediment Data Portal - File Ready";
	protected static final String SUBJECT;



	static {
		ERR_BODY    = SessionUtil.lookup(ERR_ENV_KEY,     ERR_DEFAULT);
		MSG_BODY    = SessionUtil.lookup(MSG_ENV_KEY,     MSG_DEFAULT);
		LINK_STUB   = SessionUtil.lookup(LINK_ENV_KEY,    LINK_DEFAULT);
		SUBJECT     = SessionUtil.lookup(SUBJECT_ENV_KEY, SUBJECT_DEFAULT);
		SENDER_ADDR = SessionUtil.lookup(SENDER_ADDR_ENV_KEY, SENDER_ADDR_DEFAULT);
		SENDER_NAME = SessionUtil.lookup(SENDER_NAME_ENV_KEY, SENDER_NAME_DEFAULT);
	}



	public boolean sendFileMessage(String emailAddr, String fileId) {
		logger.debug("sendMail attempt "+ emailAddr +":"+ fileId);

		String msgText = MSG_BODY+LINK_STUB+fileId+"\n";

		return new JavaMail().sendMail(SUBJECT, SENDER_ADDR, SENDER_NAME, emailAddr, msgText);
	}



	public boolean sendErrorMessage(String emailAddr, String errorId, Exception e) {
		logger.debug("sendMail error attempt "+ emailAddr +":"+ errorId);

		String msgText = ERR_BODY+errorId+"\n";
		String adminText = "Sediment Data Portal Error to user [" + emailAddr + "].\n\nError text sent to user: \n\n********************" + msgText + "\n********************\n\nFull Stack Trace:\n\n";
		
		if(e == null) {
			adminText += "-- No stack trace available --";
		} else {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			
			if(e instanceof SedmapException) {
				((SedmapException) e).getOriginalException().printStackTrace(pw);
			}
			
			adminText += sw.toString();
		}

		notifyAdminOfError("Sediment Portal Error - [" + SUBJECT + "]", adminText);
		
		JavaMail mailer = new JavaMail();
		return mailer.sendMail(SUBJECT, SENDER_ADDR, SENDER_NAME, emailAddr, msgText);
	}
	
	public void notifyAdminOfError(String subject, String message) {
		JavaMail mailer = new JavaMail();
		mailer.sendMail(subject, SENDER_ADDR, SENDER_NAME, ADMIN_ERROR_EMAIL, message);
	}
}
