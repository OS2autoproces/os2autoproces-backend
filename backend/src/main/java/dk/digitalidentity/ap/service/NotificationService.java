package dk.digitalidentity.ap.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.ap.dao.NotificationDao;
import dk.digitalidentity.ap.dao.model.Notification;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.User;

@Service
public class NotificationService {
	
	@Autowired
	private NotificationDao notificationDao;

	public void delete(Notification notification) {
		notificationDao.delete(notification);
	}

	public Notification getByUserAndProcess(User user, Process process) {
		return notificationDao.getByUserAndProcess(user, process);
	}

	public Notification save(Notification notification) {
		return notificationDao.save(notification);
	}
	
	public List<Notification> getByProcess(Process process) {
		return notificationDao.getByProcess(process);
	}
}
