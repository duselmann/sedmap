package gov.cida.sedmap.mail;

import org.apache.log4j.Logger;

import gov.cida.sedmap.io.util.SessionUtil;

public class SedmapDataMail {
	private static final Logger logger = Logger.getLogger(SedmapDataMail.class);

	protected static final String MSG_ENV_KEY = "sedmap/email/body";
	protected static final String MSG_DEFAULT = "\nYour data file is ready for download.\n\nClick on the link below to commence download.\n\nYour file will be retained for 7 days.\n\n";
	protected static final String MSG_BODY;

	protected static final String LINK_ENV_KEY = "sedmap/email/link";
	protected static final String LINK_DEFAULT = "http://localhost:8080/sediment/download?file=";
	protected static final String LINK_STUB;

	protected static final String SENDER_ADDR_ENV_KEY = "sedmap/sender/addr";
	protected static final String SENDER_ADDR_DEFAULT = "SedimentPortal_HELP@usgs.gov";
	protected static final String SENDER_ADDR;

	protected static final String SENDER_NAME_ENV_KEY = "sedmap/sender/name";
	protected static final String SENDER_NAME_DEFAULT = "Sediment Data Portal";
	protected static final String SENDER_NAME;

	protected static final String SUBJECT_ENV_KEY     = "sedmap/sender/subject";
	protected static final String SUBJECT_DEFAULT     = "Sediment Data Portal - File Ready";
	protected static final String SUBJECT;



	static {
		MSG_BODY    = SessionUtil.lookup(MSG_ENV_KEY,  MSG_DEFAULT);
		LINK_STUB   = SessionUtil.lookup(LINK_ENV_KEY, LINK_DEFAULT);
		SENDER_ADDR = SessionUtil.lookup(SENDER_ADDR_ENV_KEY,SENDER_ADDR_DEFAULT);
		SENDER_NAME = SessionUtil.lookup(SENDER_NAME_ENV_KEY, SENDER_NAME_DEFAULT);
		SUBJECT     = SessionUtil.lookup(SUBJECT_ENV_KEY, SUBJECT_DEFAULT);
	}

	public boolean sendFileMessage(String emailAddr, String fileName) {
		logger.debug("sendMail attempt "+ emailAddr +":"+ fileName);

		String msgText = MSG_BODY+LINK_STUB+fileName+"\n";

		return new JavaMail().sendMail(SUBJECT, SENDER_ADDR, SENDER_NAME, emailAddr, msgText);

	}
}
