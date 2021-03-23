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

				String subject = "Validering af proces med ID [" + process.getId() + "] i OS2autoproces";
				
				StringBuilder builder = new StringBuilder();
				
				String link = "https://www.os2autoproces.eu/details/" + process.getId();
				
				builder.append("Det er nu tid til at validere processen [" + process.getTitle() + "] med ID [" + process.getId() + "] der er indberettet i OS2autoproces i forhold til lovgivningen.<br/><br/>");
				builder.append("Processen er opmærket med lovparagraf [" + process.getLegalClause() + "] og er sidst blevet valideret [" + ((process.getLegalClauseLastVerified() != null) ? format.format(process.getLegalClauseLastVerified()) : "aldrig") + "].<br/><br/>");
				builder.append("Undersøg venligst om processen stadig overholder den nævnte lovparagraf og opdater feltet “Sidst kontrolleret i forhold til §” under fanen Drift med den nye valideringsdato.<br/><br/>");

				builder.append("Find processen her: <a href=\"" + link + "\">[link]</a> eller søg på processen ID direkte i OS2autoproces.<br/><br/>");
				
				builder.append("Ved spørgsmål kontakt din kontaktperson for OS2autoproces.");

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
