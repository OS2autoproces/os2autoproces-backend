package dk.digitalidentity.ap.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import dk.digitalidentity.ap.dao.model.Comment;
import dk.digitalidentity.ap.dao.model.State;
import dk.digitalidentity.ap.dao.model.enums.Phase;
import dk.digitalidentity.ap.dao.model.enums.Visibility;
import dk.digitalidentity.ap.security.SecurityUtil;
import dk.digitalidentity.ap.service.model.ProcessHistoryPair;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.ap.dao.NotificationDao;
import dk.digitalidentity.ap.dao.model.Notification;
import dk.digitalidentity.ap.dao.model.Process;
import org.springframework.transaction.annotation.Transactional;

@Log4j
@Service
public class NotificationService {

	private static final String LAST_NOTIFICATION_RUN = "LAST_NOTIFICATION_RUN";

	@Autowired
	private StateService stateService;

	@Autowired
	private MailSenderService mailSenderService;

	@Autowired
	private ProcessService processService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private ProcessModificationsService processModificationsService;

	@Autowired
	private NotificationDao notificationDao;

	public void delete(Notification notification) {
		notificationDao.delete(notification);
	}

	public Notification getByUserAndProcess(long userId, Process process) {
		return notificationDao.getByUserIdAndProcess(userId, process);
	}

	public Notification save(Notification notification) {
		return notificationDao.save(notification);
	}
	
	public List<Notification> getByProcess(Process process) {
		return notificationDao.getByProcess(process);
	}

	// moved this from NotificationTask
	@Transactional
	public void sendNotifications(String senderEmailAddress) {
		SecurityUtil.loginSystem();
		Date lastRunTimestamp = new Date(0L);
		Date currentTimestamp = stateService.getCurrentTimestamp();
		State state = stateService.getByKey(LAST_NOTIFICATION_RUN);
		if (state != null) {
			lastRunTimestamp = new Date(Long.parseLong(state.getValue()));
		}

		List<Process> modifiedProcesses = processService.findByLastChangedBetween(lastRunTimestamp, currentTimestamp);

		if (modifiedProcesses != null && modifiedProcesses.size() > 0) {
			for (Process process : modifiedProcesses) {

				// see if we need to send a notification for this process
				ProcessHistoryPair history = processModificationsService.getHistory(process.getId(), lastRunTimestamp);
				if (history == null || !history.hasChanges()) {
					continue;
				}

				// if the process phase is changed to operation, but visibility is not public, send notification to owner
				if (process.getPhase().equals(Phase.OPERATION) &&
						!history.getLatest().getPhase().equals(history.getPrev().getPhase()) &&
						!process.getVisibility().equals(Visibility.PUBLIC)) {

					if (process.getOwner() != null && process.getOwner().getEmail() != null) {

						// TODO why are we sending an empty message?
						String subject = "";
						String message = "";

						try {
							mailSenderService.sendMessage(senderEmailAddress, Collections.singletonList(process.getOwner().getEmail()), subject, message);
						}
						catch (Exception ex) {
							log.error("Failed to send phase-change notification for process: " + process.getId(), ex);
						}
					}
				}

				try {
					List<Notification> notifications = notificationDao.getByProcess(process);

					if (notifications != null && notifications.size() > 0) {
						List<String> emails = notifications.stream().filter(n -> n.getUser().getEmail() != null).map(n -> n.getUser().getEmail()).collect(Collectors.toList());

						sendNotification(history, emails, senderEmailAddress);
					}
				}
				catch (Exception ex) {
					log.error("Failed to send notifications for process: " + process.getId(), ex);
				}
			}
		}

		// let's see if there are any new comments
		List<Comment> comments = commentService.getByCreatedBetween(lastRunTimestamp, currentTimestamp);
		for (Comment comment : comments) {
			try {
				List<Notification> notifications = notificationDao.getByProcess(comment.getProcess());

				if (notifications != null && notifications.size() > 0) {

					// all subscribers
					Set<String> emails = notifications.stream().filter(n -> n.getUser().getEmail() != null).map(n -> n.getUser().getEmail()).collect(Collectors.toSet());

					// all associated users
					emails.addAll(comment.getProcess().getUsers().stream().filter(u -> u.getEmail() != null).map(u -> u.getEmail()).collect(Collectors.toSet()));

					// always contact person
					String contactPerson = (comment.getProcess().getContact() != null) ? comment.getProcess().getContact().getEmail() : null;
					if (contactPerson != null) {
						emails.add(contactPerson);
					}

					sendNotification(comment, emails, senderEmailAddress);
				}
			}
			catch (Exception ex) {
				log.error("Failed to send notifications for comment: " + comment.getId(), ex);
			}

		}

		if (state == null) {
			state = new State();
			state.setKey(LAST_NOTIFICATION_RUN);
		}

		state.setValue(Long.toString(currentTimestamp.getTime()));
		stateService.save(state);
		SecurityUtil.logoutSystem();
	}


	private void sendNotification(Comment comment, Collection<String> emails, String senderEmailAddress) {
		String subject = "OS2autoproces (" + comment.getProcess().getId() + "): " + comment.getProcess().getTitle();

		StringBuilder builder = new StringBuilder();
		builder.append("Processen med ID " + comment.getProcess().getId() + " er blevet opdateret med en kommentar:<br/><br/>");

		builder.append("<b>" + comment.getName() + "</b></br>");
		builder.append(comment.getMessage());

		try {
			mailSenderService.sendMessage(senderEmailAddress, emails, subject, builder.toString());
		}
		catch (Exception ex) {
			log.warn("Error occured while trying to send email. ", ex);
		}
	}

	private void sendNotification(ProcessHistoryPair history, List<String> emails, String senderEmailAddress) {
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
