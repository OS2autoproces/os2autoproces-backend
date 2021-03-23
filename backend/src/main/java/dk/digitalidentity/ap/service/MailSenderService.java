package dk.digitalidentity.ap.service;

import java.util.List;

public interface MailSenderService {
	void sendMessage(String from, List<String> to, String subject, String body) throws Exception;
}