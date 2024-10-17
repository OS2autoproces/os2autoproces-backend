package dk.digitalidentity.ap.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dk.digitalidentity.ap.task.STSOrganisationService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/ops")
@SecurityRequirement(name = "Authorization")
@Tag(name = "STS Organisation API")
public class OpsApi {

	@Autowired
	private STSOrganisationService stsOrganisationService;

	@Operation(summary = "Trigger STS Organisation update")
	@GetMapping(value = "/organisation/{cvr}")
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<?> triggerOrganisationUpdate(@PathVariable("cvr") String cvr) throws Exception {

		Runnable r = new Runnable() {
			public void run() {
				try {
					stsOrganisationService.loadOrganisation(cvr);
				}
				catch (Exception ex) {
					log.error("failed", ex);
				}
			}
		};

		new Thread(r).start();

		return ResponseEntity.ok().build();
	}
}
