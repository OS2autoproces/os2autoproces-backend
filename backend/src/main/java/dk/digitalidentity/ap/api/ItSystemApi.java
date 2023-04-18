package dk.digitalidentity.ap.api;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dk.digitalidentity.ap.dao.model.ItSystem;
import dk.digitalidentity.ap.service.ItSystemService;

@RestController
@RequestMapping("/api/managedItSystem")
public class ItSystemApi {

	@Autowired
	private ItSystemService itSystemService;

	@PostMapping("/")
	public ResponseEntity<?> createItSystem(@RequestBody @Valid ItSystem itSystem) {
		if (itSystem == null) {
			return ResponseEntity.badRequest().body("No it-system supplied in payload");			
		}

		ItSystem dbItSystem = new ItSystem();
		dbItSystem.setId(0L);
		dbItSystem.setFromKitos(false);
		dbItSystem.setName(itSystem.getName());
		dbItSystem.setVendor(itSystem.getVendor());
		dbItSystem.setSystemId(itSystem.getSystemId());

		return ResponseEntity.ok(itSystemService.save(dbItSystem));
	}

	@PutMapping("/{itSystemId}")
	public ResponseEntity<?> editItSystem(@RequestBody @Valid ItSystem itSystem, @PathVariable("itSystemId") long itSystemId) {
		if (itSystem == null) {
			return ResponseEntity.badRequest().body("No it-system supplied in payload");			
		}

		ItSystem dbItSystem = itSystemService.findById(itSystemId);
		if (dbItSystem == null) {
			return ResponseEntity.badRequest().body("No it-system with ID " + itSystemId + " exists in backend");				
		}

		if (dbItSystem.isFromKitos()) {
			return ResponseEntity.badRequest().body("ItSystem is managed by KITOS, and cannot be edited through API");
		}

		dbItSystem.setName(itSystem.getName());
		dbItSystem.setVendor(itSystem.getVendor());
		dbItSystem.setSystemId(itSystem.getSystemId());

		return ResponseEntity.ok(itSystemService.save(dbItSystem));
	}

	@DeleteMapping("/{itSystemId}")
	public ResponseEntity<?> deleteItSystem(@PathVariable("itSystemId") long itSystemId) {
		ItSystem itSystem = itSystemService.findById(itSystemId);
		if (itSystem == null) {
			return ResponseEntity.badRequest().body("No it-system with ID " + itSystemId + " exists in backend");				
		}
		
		if (itSystem.isFromKitos()) {
			return ResponseEntity.badRequest().body("ItSystem is managed by KITOS, and cannot be deleted through API");
		}

		itSystemService.delete(itSystem);

		return ResponseEntity.ok().build();
	}
}