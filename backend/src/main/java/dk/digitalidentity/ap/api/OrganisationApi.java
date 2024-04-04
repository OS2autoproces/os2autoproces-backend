package dk.digitalidentity.ap.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dk.digitalidentity.ap.api.model.OrganisationDTO;
import dk.digitalidentity.ap.api.model.OrganisationImportResponse;
import dk.digitalidentity.ap.security.SecurityUtil;
import dk.digitalidentity.ap.service.OrganisationService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/xapi")
public class OrganisationApi {

	@Autowired
	private OrganisationService organisationImporter;

	@PostMapping(value = "/organisation")
	@Transactional(rollbackFor = Exception.class)
	public synchronized ResponseEntity<?> importOrgUnits(@RequestBody OrganisationDTO organisation) {
		try {
			OrganisationImportResponse response = organisationImporter.bigImport(organisation, SecurityUtil.getClientCvr());

			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		catch (Exception ex) {
			log.error("Import failed for " + SecurityUtil.getClientCvr(), ex);
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
}
