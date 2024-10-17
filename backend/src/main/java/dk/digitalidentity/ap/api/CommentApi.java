package dk.digitalidentity.ap.api;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dk.digitalidentity.ap.dao.model.Comment;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.service.CommentService;
import dk.digitalidentity.ap.service.ProcessService;

@RestController
@RequestMapping("/api/comments")
@SecurityRequirement(name = "Authorization")
@Tag(name = "Comment API")
public class CommentApi {

	@Autowired
	private CommentService commentService;

	@Autowired
	private ProcessService processService;

	@GetMapping("/{processId}")
	public ResponseEntity<?> getComments(@PathVariable("processId") long processId) {
		Process process = processService.findOne(processId);
		if (process == null) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.ok(commentService.getByProcess(process));
	}
	
	@PutMapping("/{processId}")
	public ResponseEntity<?> addComment(@PathVariable("processId") long processId, @RequestBody @Valid Comment comment) {
		// findOne() only returns processes that the currently logged in user can read ;)
		Process process = processService.findOne(processId);
		if (process == null) {
			return ResponseEntity.notFound().build();
		}

		comment.setProcess(process);
		comment = commentService.save(comment);

		return ResponseEntity.ok(comment);
	}
}