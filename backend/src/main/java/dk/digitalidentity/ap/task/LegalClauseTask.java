package dk.digitalidentity.ap.task;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.service.MailSenderService;
import dk.digitalidentity.ap.service.ProcessService;
import lombok.extern.log4j.Log4j;

@Log4j
@Component
@EnableScheduling
public class LegalClauseTask {
	
	@Value("${scheduled.enabled:false}")
	private boolean runScheduled;

	@Value("${email.sender:}")
	private String senderEmailAddress;

	@Autowired
	private MailSenderService mailSenderService;

	@Autowired
	private ProcessService processService;

	// Run every Wednesday at 11:00
	@Scheduled(cron = "0 0 11 * * WED")
	@Transactional
	public void sendNotifications() {
		if (!runScheduled) {
			return;
		}

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		List<Process> processes = processService.getByLegalClausePendingVerification();
		
		for (Process process : processes) {
			if (process.getOwner() != null && process.getOwner().getEmail() != null) {

				List<String> emails = Collections.singletonList(process.getOwner().getEmail());

				String subject = "OS2autoproces paragraf-opfølgning (" + process.getId() + "): " + process.getTitle();
				
				StringBuilder builder = new StringBuilder();
				builder.append("Processen med ID " + process.getId() + " er opmærket med lovparagraf '" + process.getLegalClause() + "', der sidst er blevet valideret " + ((process.getLegalClauseLastVerified() != null) ? format.format(process.getLegalClauseLastVerified()) : "(aldrid)") + "<br/><br/>");
				builder.append("Undersøg venligst om processen stadig overholder den nævnte lovparagraf, og opdater processen i OS2autoproces med den nye valideringsdato.");

				sendNotification(subject, builder.toString(), emails);
			}
		}
	}
	
	private void sendNotification(String subject, String content, List<String> emails) {
		try {
			mailSenderService.sendMessage(senderEmailAddress, emails, subject, content);
		}
		catch (Exception ex) {
			log.warn("Error occured while trying to send email", ex);
		}
	}
}
