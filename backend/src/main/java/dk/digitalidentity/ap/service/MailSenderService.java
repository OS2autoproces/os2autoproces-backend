package dk.digitalidentity.ap.service;

import java.util.Collection;

public interface MailSenderService {
	void sendMessage(String from, Collection<String> to, String subject, String body) throws Exception;
}