package dk.digitalidentity.ap.api;

import java.util.List;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import dk.digitalidentity.ap.dao.model.Municipality;
import dk.digitalidentity.ap.dao.model.User;
import dk.digitalidentity.ap.security.SecurityUtil;
import dk.digitalidentity.ap.service.MunicipalityService;
import dk.digitalidentity.ap.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Hidden
public class LoginApi {

	@Autowired
	private MunicipalityService municipalityService;
	
	@Autowired
	private UserService userService;

	// to access this endpoint, the caller has to go through XApiSecurityFilter,
	// so we can safely issue a valid user token
	@PostMapping(value = "/xapi/auth")
	public ResponseEntity<?> auth() {
		try {
			Municipality municipality = municipalityService.getByCvr(SecurityUtil.getClientCvr());
			if (municipality != null) {
				User user = SecurityUtil.loginMunicipality(municipality);
				
				// optimistic creation of system users
				if (user != null) {
					List<User> existingUsers = userService.getByUuidAndCvrIncludingInactive(user.getUuid(), user.getCvr());
					if (existingUsers.size() == 0) {
						userService.save(user);
					}
				}

				return new ResponseEntity<>(HttpStatus.OK);
			}

			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		catch (Exception ex) {
			log.error("Login failed for " + SecurityUtil.getClientCvr(), ex);

			return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
}
