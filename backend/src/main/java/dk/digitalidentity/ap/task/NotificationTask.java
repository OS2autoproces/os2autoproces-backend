package dk.digitalidentity.ap.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.service.NotificationService;

@Component
@EnableScheduling
public class NotificationTask {
	@Value("${scheduled.enabled:false}")
	private boolean runScheduled;

	@Value("${email.sender:}")
	private String senderEmailAddress;

	@Value("${email.enabled:false}")
	private boolean emailActive;

	@Autowired
	private NotificationService notificationService;

	// send notifications once every 10 minutes
	@Scheduled(fixedRate = 10 * 60 * 1000)
	public void sendNotifications() {
		if (!runScheduled || !emailActive) {
			return;
		}

		notificationService.sendNotifications(senderEmailAddress);
	}
}
