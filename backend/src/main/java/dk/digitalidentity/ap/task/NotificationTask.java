package dk.digitalidentity.ap.task;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.dao.model.Notification;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.State;
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
	private static final String LAST_NOTIFICATION_RUN = "LAST_NOTIFICATION_RUN";
	
	@Value("${scheduled.enabled:false}")
	private boolean runScheduled;

	@Value("${email.sender:}")
	private String senderEmailAddress;

	@Autowired
	private MailSenderService mailSenderService;

	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private StateService stateService;
	
	@Autowired
	private ProcessService processService;
	
	@Autowired
	private ProcessModificationsService processModificationsService;

	// send notifications once every 10 minutes
	@Scheduled(fixedRate = 10 * 60 * 1000)
	public void sendNotifications() {
		if (!runScheduled) {
			return;
		}
		
		Date lastRunTimestamp = new Date(0L);
		Date currentTimestamp = stateService.getCurrentTimestamp();
		State state = stateService.getByKey(LAST_NOTIFICATION_RUN);
		if (state != null) {
			lastRunTimestamp = new Date(Long.parseLong(state.getValue()));
		}

		// TODO: we can optimize this by just getting the ID fields
		List<Process> modifiedProcesses = processService.findByLastChangedBetween(lastRunTimestamp, currentTimestamp);
		
		if (modifiedProcesses != null && modifiedProcesses.size() > 0) {
			for (Process process : modifiedProcesses) {
				
				// see if we need to send a notification for this process
				ProcessHistoryPair history = processModificationsService.getHistory(process.getId(), lastRunTimestamp);
				if (history == null || !history.hasChanges()) {
					continue;
				}
				
				try {
					List<Notification> notifications = notificationService.getByProcess(process);
					
					if (notifications != null && notifications.size() > 0) {
						List<String> emails = notifications.stream().filter(n -> n.getUser().getEmail() != null).map(n -> n.getUser().getEmail()).collect(Collectors.toList());

						sendNotification(history, emails);
					}
				}
				catch (Exception ex) {
					log.error("Failed to send notifications for process: " + process.getId(), ex);
				}
			}
		}

		if (state == null) {
			state = new State();
			state.setKey(LAST_NOTIFICATION_RUN);
		}

		state.setValue(Long.toString(currentTimestamp.getTime()));
		stateService.save(state);
	}
	
	private void sendNotification(ProcessHistoryPair history, List<String> emails) {
		String subject = "OS2autoproces (" + history.getId() + "): " + history.getLatest().getTitle();

		StringBuilder builder = new StringBuilder();
		builder.append("Processen med ID " + history.getId() + " er blevet opdateret med følgende oplysninger:<br/><br/>");

		if (!stringEquals(history.getLatest().getTitle(), history.getPrev().getTitle())) {
			builder.append("<b>Navn</b><br/>" + history.getLatest().getTitle() + "<br/><br/>");
		}
		
		if (!stringEquals(history.getLatest().getShortDescription(), history.getPrev().getShortDescription())) {
			builder.append("<b>Resumé</b><br/>" + history.getLatest().getShortDescription() + "<br/><br/>");
		}
		
		if (!stringEquals(history.getLatest().getLongDescription(), history.getPrev().getLongDescription())) {
			builder.append("<b>Beskrivelse</b><br/>" + history.getLatest().getLongDescription() + "<br/><br/>");
		}
		
		if (!history.getLatest().getPhase().equals(history.getPrev().getPhase())) {
			builder.append("<b>Fase</b><br/>" + history.getLatest().getPhase().getValue() + "<br/><br/>");
		}
		
		if (!history.getLatest().getStatus().equals(history.getPrev().getStatus())) {
			builder.append("<b>Status</b><br/>" + history.getLatest().getStatus().getValue() + "<br/><br/>");
		}
		
		if (!stringEquals(history.getLatest().getStatusText(), history.getPrev().getStatusText())) {
			builder.append("<b>Statustekst</b><br/>" + history.getLatest().getStatusText() + "<br/><br/>");
		}

		if (!stringEquals(history.getLatest().getKlId(), history.getPrev().getKlId())) {
			builder.append("<b>KL ID</b><br/>" + history.getLatest().getKlId() + "<br/><br/>");
		}
		
		if (!stringEquals(history.getLatest().getKle(), history.getPrev().getKle())) {
			builder.append("<b>KLE</b><br/>" + history.getLatest().getKle() + "<br/><br/>");
		}
		
		if (!stringEquals(history.getLatest().getForm(), history.getPrev().getForm())) {
			builder.append("<b>FORM</b><br/>" + history.getLatest().getForm() + "<br/><br/>");
		}
		
		if (!stringEquals(history.getLatest().getKla(), history.getPrev().getKla())) {
			builder.append("<b>KLA</b><br/>" + history.getLatest().getKla() + "<br/><br/>");
		}
		
		if (!stringEquals(history.getLatest().getLegalClause(), history.getPrev().getLegalClause())) {
			builder.append("<b>Paragraf</b><br/>" + history.getLatest().getLegalClause() + "<br/><br/>");
		}

		if (!stringEquals(history.getLatest().getProcessChallenges(), history.getPrev().getProcessChallenges())) {
			builder.append("<b>Processudfordringer</b><br/>" + history.getLatest().getProcessChallenges() + "<br/><br/>");
		}
		
		if (!stringEquals(history.getLatest().getSolutionRequests(), history.getPrev().getSolutionRequests())) {
			builder.append("<b>Løsningsforslag</b><br/>" + history.getLatest().getSolutionRequests() + "<br/><br/>");
		}
		
		if (!stringEquals(history.getLatest().getTechnicalImplementationNotes(), history.getPrev().getTechnicalImplementationNotes())) {
			builder.append("<b>Tekniske implementeringsnoter</b><br/>" + history.getLatest().getTechnicalImplementationNotes() + "<br/><br/>");
		}
		
		if (!stringEquals(history.getLatest().getOrganizationalImplementationNotes(), history.getPrev().getOrganizationalImplementationNotes())) {
			builder.append("<b>Organisatoriske implementeringsnoter</b><br/>" + history.getLatest().getOrganizationalImplementationNotes() + "<br/><br/>");
		}
		
		if (history.getLatest().getRating() != history.getPrev().getRating()) {
			builder.append("<b>Vurdering</b><br/>" + history.getLatest().getRating() + "<br/><br/>");
		}

		if (!stringEquals(history.getLatest().getRatingComment(), history.getPrev().getRatingComment())) {
			builder.append("<b>Vurderingskommentar</b><br/>" + history.getLatest().getRatingComment() + "<br/><br/>");
		}

		try {
			mailSenderService.sendMessage(senderEmailAddress, emails, subject, builder.toString());
		}
		catch (Exception ex) {
			log.warn("Error occured while trying to send email. ", ex);
		}
	}
	
	private boolean stringEquals(String latest, String prev) {
		if (latest == null && prev == null) {
			return true;
		}
		
		if (latest != null && !latest.equals(prev)) {
			return false;
		}
		
		if (prev != null && !prev.equals(latest)) {
			return false;
		}

		return true;
	}
}
