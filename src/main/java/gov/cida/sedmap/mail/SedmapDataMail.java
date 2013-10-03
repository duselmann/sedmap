package gov.cida.sedmap.mail;

import org.apache.log4j.Logger;

import gov.cida.sedmap.io.util.SessionUtil;

public class SedmapDataMail {
	private static final Logger logger = Logger.getLogger(SedmapDataMail.class);

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



	public boolean sendErrorMessage(String emailAddr, String errorId) {
		logger.debug("sendMail error attempt "+ emailAddr +":"+ errorId);

		String msgText = ERR_BODY+errorId+"\n";

		JavaMail mailer = new JavaMail();
		// TODO send support an email in production
		// mailer.sendMail(SUBJECT, SENDER_ADDR, SENDER_NAME, SENDER_ADDR, msgText);
		return mailer.sendMail(SUBJECT, SENDER_ADDR, SENDER_NAME, emailAddr, msgText);
	}
}
