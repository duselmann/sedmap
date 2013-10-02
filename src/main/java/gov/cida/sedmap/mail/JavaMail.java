package gov.cida.sedmap.mail;


import gov.cida.sedmap.io.util.SessionUtil;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.log4j.Logger;

public class JavaMail {

	private static final Logger logger = Logger.getLogger(JavaMail.class);

	private static final String MAIL_HOST_ENV_KEY   = "sedmap/email/host";
	private static final String MAIL_HOST_DEFAULT   = "localhost";
	private static final String MAIL_HOST;

	private static final Properties properties;

	static {
		MAIL_HOST   = SessionUtil.lookup(MAIL_HOST_ENV_KEY, MAIL_HOST_DEFAULT);

		properties  = new Properties();
		properties.setProperty("mail.smtp.host", MAIL_HOST);

	}



	public JavaMail() {
	}



	public boolean sendMail(String subject, String senderAddr, String senderName, String emailAddr, String msgText) {
		try {
			Session session  = Session.getDefaultInstance(properties, null);
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(senderAddr, senderName));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(emailAddr));
			msg.setSubject(subject);
			msg.setText(msgText);
			Transport.send(msg);

		} catch (Exception e) {
			logger.warn("Failed to send email because of the following exception. ", e);
			return false;
		}
		return true;
	}
}



