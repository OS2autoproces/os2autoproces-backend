package dk.digitalidentity.ap.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.ap.dao.NotificationDao;
import dk.digitalidentity.ap.dao.model.Notification;
import dk.digitalidentity.ap.dao.model.Process;

@Service
public class NotificationService {
	
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
}
