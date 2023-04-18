package dk.digitalidentity.ap.api;

import dk.digitalidentity.ap.api.model.ProfileDTO;
import dk.digitalidentity.ap.dao.model.User;
import dk.digitalidentity.ap.security.AuthenticatedUser;
import dk.digitalidentity.ap.security.SecurityUtil;
import dk.digitalidentity.ap.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class ProfileApi {

	@Autowired
	private UserService userService;

	@PutMapping("/api/profile")
	public ResponseEntity<?> editItSystem(@RequestBody ProfileDTO profileDTO) {
		if (profileDTO == null) {
			return ResponseEntity.badRequest().body("No profile information supplied in payload");
		}

		AuthenticatedUser authenticatedUser = SecurityUtil.getUser();
		if (authenticatedUser == null || !Objects.equals(authenticatedUser.getCvr(), profileDTO.getCvr()) || !Objects.equals(authenticatedUser.getUuid(), profileDTO.getUuid())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("It is only allowed to edit the logged in user's profile.");
		}

		User user = userService.getByUuidAndCvr(profileDTO.getUuid(), profileDTO.getCvr());
		if (user == null) {
			return ResponseEntity.badRequest().body("Failed to find user with uuid " + profileDTO.getUuid() + " and cvr " + profileDTO.getCvr());
		}

		user.setName(profileDTO.getName());
		user.setEmail(profileDTO.getEmail());

		return ResponseEntity.ok(userService.save(user));
	}
}
