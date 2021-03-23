package dk.digitalidentity.ap.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dk.digitalidentity.ap.api.model.SystemUserDTO;
import dk.digitalidentity.ap.dao.model.Municipality;
import dk.digitalidentity.ap.dao.model.User;
import dk.digitalidentity.ap.security.SecurityUtil;
import dk.digitalidentity.ap.service.MunicipalityService;

@RestController
@CrossOrigin(exposedHeaders = "x-csrf-token")
@RequestMapping("/public")
public class PublicApi {
	
	@Autowired
	private MunicipalityService municipalityService;

	@GetMapping("/whoami")
	public ResponseEntity<SystemUserDTO> whoami() {
		SystemUserDTO result = new SystemUserDTO();

		User user = SecurityUtil.getUser();
		if (user != null) {
			result.setUuid(user.getUuid());
			result.setEmail(user.getEmail());
			result.setName(user.getName());
			result.setCvr(user.getCvr());
			
			// while we could pull this without the user, it does
			// not make sense without the user information
			result.setRoles(SecurityUtil.getRoles());
		}

		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@GetMapping("/municipalities")
	public ResponseEntity<List<Municipality>> getMunicipalities() {
		return new ResponseEntity<>(municipalityService.findAll(), HttpStatus.OK);
	}
	
	@GetMapping("/municipalities/{cvr}")
	public ResponseEntity<?> getMunicipality(@PathVariable("cvr") String cvr) {
		Municipality municipality = municipalityService.getByCvr(cvr);
		if (municipality == null) {
			return ResponseEntity.notFound().build();
		}

		return new ResponseEntity<>(municipality, HttpStatus.OK);
	}
}
