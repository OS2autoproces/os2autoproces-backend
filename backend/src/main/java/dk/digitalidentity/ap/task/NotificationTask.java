package dk.digitalidentity.ap.task;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.dao.model.Comment;
import dk.digitalidentity.ap.dao.model.Notification;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.State;
import dk.digitalidentity.ap.dao.model.enums.Phase;
import dk.digitalidentity.ap.dao.model.enums.Visibility;
import dk.digitalidentity.ap.service.CommentService;
import dk.digitalidentity.ap.service.MailSenderService;
import dk.digitalidentity.ap.service.NotificationService;
import dk.digitalidentity.ap.service.ProcessModificationsService;
import dk.digitalidentity.ap.service.ProcessService;
import dk.digitalidentity.ap.service.StateService;
import dk.digitalidentity.ap.service.model.ProcessHistoryPair;
import lombok.extern.log4j.Log4j;

@Log4j
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
