package dk.digitalidentity.ap.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import dk.digitalidentity.ap.smtp.TransportErrorHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("!test")
public class AmazonSESService implements MailSenderService {

	@Value("${email.username:}")
	private String smtpUsername;

	@Value("${email.password:}")
	private String smtpPassword;

	@Value("${email.host:}")
	private String smtpHost;

	@Value("${email.active:true}")
	private boolean emailActive;

	@Override
	public void sendMessage(String from, Collection<String> emails, String subject, String body) throws Exception {
		if (!isConfigured()) {
			log.warn("AmazonSESService is not configured with a username/password - not sending emails!");
			return;
		}

		Properties props = System.getProperties();
		props.put("mail.transport.protocol", "smtps");
		props.put("mail.smtp.port", 25);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.starttls.required", "true");

		Session session = Session.getDefaultInstance(props);

		MimeMessage msg = new MimeMessage(session);

		InternetAddress addressFrom = new InternetAddress();
		addressFrom.setAddress(from);

		List<Address> recipients = new ArrayList<>();
		for (String email : emails) {
			InternetAddress internetAddress = new InternetAddress();
			internetAddress.setAddress(email);
			recipients.add(internetAddress);
		}

		msg.setFrom(addressFrom);
		msg.setRecipients(Message.RecipientType.BCC, recipients.toArray(new Address[0]));
		msg.setSubject(subject, "UTF-8");
		msg.setText(body, "UTF-8");
		msg.setHeader("Content-Type", "text/html; charset=UTF-8");

		Transport transport = session.getTransport();
		try {
			transport.connect(smtpHost, smtpUsername, smtpPassword);
			transport.addTransportListener(new TransportErrorHandler());
			transport.sendMessage(msg, msg.getAllRecipients());
		}
		finally {
			try {
				transport.close();
			}
			catch (Exception ex) {
				log.warn("Error occured while trying to terminate connection", ex);
			}
		}
	}

	private boolean isConfigured() {
		if (smtpHost == null || smtpHost.length() == 0 || smtpPassword == null || smtpPassword.length() == 0 || smtpUsername == null || smtpUsername.length() == 0) {
			return false;
		}
		
		if (!emailActive) {
			return false;
		}

		return true;
	}
}