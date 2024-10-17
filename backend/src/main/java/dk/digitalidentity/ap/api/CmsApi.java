package dk.digitalidentity.ap.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dk.digitalidentity.ap.dao.model.CmsEntry;
import dk.digitalidentity.ap.security.RequireFrontPageEditorRole;
import dk.digitalidentity.ap.service.CmsService;

@RestController
@SecurityRequirement(name = "Authorization")
@Tag(name = "CMS API")
public class CmsApi {
	
	@Autowired
	private CmsService cmsService;

	@GetMapping("/public/cms")
	public ResponseEntity<?> list() {
		return ResponseEntity.ok(cmsService.findAll());
	}
	
	@GetMapping("/public/cms/{label}")
	public ResponseEntity<?> getValue(@PathVariable String label) {
		CmsEntry cmsEntry = cmsService.getByLabel(label);
		if (cmsEntry == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(cmsEntry);
	}

	@RequireFrontPageEditorRole
	@PostMapping("/api/cms/{label}")
	public ResponseEntity<CmsEntry> setValue(@PathVariable String label, @RequestBody String content) {
		CmsEntry cmsEntry = cmsService.getByLabel(label);
		if (cmsEntry == null) {
			cmsEntry = new CmsEntry();
			cmsEntry.setLabel(label);
		}

		cmsEntry.setContent(content);
		cmsService.save(cmsEntry);

		return ResponseEntity.ok(cmsEntry);
	}
	
	@RequireFrontPageEditorRole
	@DeleteMapping("/api/cms/{label}")
	public ResponseEntity<?> deleteValue(@PathVariable String label) {
		CmsEntry cmsEntry = cmsService.getByLabel(label);
		if (cmsEntry != null) {
			cmsService.delete(cmsEntry);
		}

		return ResponseEntity.ok().build();
	}
}
