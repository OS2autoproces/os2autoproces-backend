package dk.digitalidentity.ap.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dk.digitalidentity.ap.dao.model.Notification;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.User;
import dk.digitalidentity.ap.security.SecurityUtil;
import dk.digitalidentity.ap.service.NotificationService;
import dk.digitalidentity.ap.service.ProcessService;
import lombok.extern.log4j.Log4j;

@Log4j
@RestController
@CrossOrigin(exposedHeaders = "x-csrf-token")
@RequestMapping("/api/notifications")
public class NotificationApi {

	@Autowired
	private NotificationService notiticationService;

	@Autowired
	private ProcessService processService;

	@PutMapping("/{processId}")
	public ResponseEntity<?> addNotification(@PathVariable("processId") long processId) {
		Process process = processService.findOne(processId);
		if (process == null) {
			return ResponseEntity.notFound().build();
		}
		
		User user = SecurityUtil.getUser();
		if (user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("There is no user in organisation matching logged in user: " + SecurityUtil.getUserId());
		}

		if (!SecurityUtil.canRead(process)) {
			log.warn("No access to read this Process.");
			return ResponseEntity.notFound().build();
		}

		Notification notification = notiticationService.getByUserAndProcess(user, process);
		if (notification != null) {
			return ResponseEntity.ok("");
		}

		Notification newNotification = new Notification();
		newNotification.setProcess(process);
		newNotification.setUser(user);
		newNotification = notiticationService.save(newNotification);

		return ResponseEntity.ok("");
	}

	@DeleteMapping("/{processId}")
	public ResponseEntity<?> removeNotification(@PathVariable("processId") long processId) {
		Process process = processService.findOne(processId);
		if (process == null) {
			return ResponseEntity.notFound().build();
		}

		User user = SecurityUtil.getUser();
		if (user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("There is no user in organisation matching logged in user: " + SecurityUtil.getUserId());
		}

		Notification notification = notiticationService.getByUserAndProcess(user, process);
		if (notification == null) {
			return ResponseEntity.ok("");
		}

		notiticationService.delete(notification);

		return ResponseEntity.ok("");
	}
}