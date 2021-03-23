package dk.digitalidentity.ap.stub;

import java.util.List;

import org.springframework.stereotype.Service;

import dk.digitalidentity.ap.service.MailSenderService;

@Service
public class StubbedMailSender implements MailSenderService {

	@Override
	public void sendMessage(String from, List<String> to, String subject, String body) throws Exception {
		; // do nothing
	}
}
