package dk.digitalidentity.ap.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dk.digitalidentity.ap.dao.model.Bookmark;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.User;
import dk.digitalidentity.ap.dao.model.projection.ProcessGridProjection;
import dk.digitalidentity.ap.security.AuthenticatedUser;
import dk.digitalidentity.ap.security.SecurityUtil;
import dk.digitalidentity.ap.service.BookmarkService;
import dk.digitalidentity.ap.service.ProcessService;
import dk.digitalidentity.ap.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkApi {

	@Autowired
	private BookmarkService bookmarkService;

	@Autowired
	private ProcessService processService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ProjectionFactory factory;

	@GetMapping
	public ResponseEntity<?> getBookmarks() {
		AuthenticatedUser authenticatedUser = SecurityUtil.getUser();
		if (authenticatedUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("There is no user in organisation matching logged in user: " + SecurityUtil.getUserId());
		}
		
		List<Bookmark> bookmarks = bookmarkService.getByUser(authenticatedUser.getId());
		List<Process> processes = bookmarks.stream().map(b -> b.getProcess()).collect(Collectors.toList());

		// filter only those user can read
		processes = processes.stream().filter(p -> SecurityUtil.canRead(p)).collect(Collectors.toList());

		// convert to grid projection
	    List<ProcessGridProjection> projected = processes.stream().map(p -> factory.createProjection(ProcessGridProjection.class, p)).collect(Collectors.toList());
		
		return ResponseEntity.ok(projected);
	}

	@PutMapping("/{processId}")
	public ResponseEntity<?> addBookmark(@PathVariable("processId") long processId) {
		Process process = processService.findOne(processId);
		if (process == null) {
			return ResponseEntity.notFound().build();
		}
		
		AuthenticatedUser authenticatedUser = SecurityUtil.getUser();
		if (authenticatedUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("There is no user in organisation matching logged in user: " + SecurityUtil.getUserId());
		}

		if (!SecurityUtil.canRead(process)) {
			log.warn("No access to read this Process.");
			return ResponseEntity.notFound().build();
		}

		Bookmark bookmark = bookmarkService.getByUserAndProcess(authenticatedUser.getId(), process);
		if (bookmark != null) {
			return ResponseEntity.ok("");
		}

		User user = userService.getByUuidAndCvr(authenticatedUser.getUuid(), authenticatedUser.getCvr());
		
		Bookmark newBookmark = new Bookmark();
		newBookmark.setProcess(process);
		newBookmark.setUser(user);
		newBookmark = bookmarkService.save(newBookmark);

		return ResponseEntity.ok("");
	}

	@DeleteMapping("/{processId}")
	public ResponseEntity<?> removeBookmark(@PathVariable("processId") long processId) {
		Process process = processService.findOne(processId);
		if (process == null) {
			return ResponseEntity.notFound().build();
		}

		AuthenticatedUser user = SecurityUtil.getUser();
		if (user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("There is no user in organisation matching logged in user: " + SecurityUtil.getUserId());
		}

		Bookmark bookmark = bookmarkService.getByUserAndProcess(user.getId(), process);
		if (bookmark == null) {
			return ResponseEntity.ok("");
		}

		bookmarkService.delete(bookmark);

		return ResponseEntity.ok("");
	}
}