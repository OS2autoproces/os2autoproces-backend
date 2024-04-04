package dk.digitalidentity.ap.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.service.ProcessService;

@Component
@EnableScheduling
public class NoChangesInAYearNotificationTask {
	
	@Value("${scheduled.enabled:false}")
	private boolean runScheduled;

	@Value("${email.sender:}")
	private String senderEmailAddress;

	@Value("${email.enabled:false}")
	private boolean emailActive;

	@Value("${os2autoproces.frontend.baseurl:}")
	private String frontendBaseUrl;

	@Autowired
	private ProcessService processService;

	@Scheduled(fixedRate = 60*1000)
	// daily at 7 am
	@Scheduled(cron = "0 0 7 * * *")
	public void sendNotifications() {
		if (!runScheduled || !emailActive) {
			return;
		}

		processService.sendNoChangesInAYearNotifications(frontendBaseUrl, senderEmailAddress);
	}
}
