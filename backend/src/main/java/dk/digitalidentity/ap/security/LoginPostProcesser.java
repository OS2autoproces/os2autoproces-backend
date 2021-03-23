package dk.digitalidentity.ap.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import dk.digitalidentity.ap.dao.model.Administrator;
import dk.digitalidentity.ap.dao.model.OrgUnit;
import dk.digitalidentity.ap.dao.model.User;
import dk.digitalidentity.ap.service.AdministratorService;
import dk.digitalidentity.ap.service.MunicipalityService;
import dk.digitalidentity.ap.service.UserService;
import dk.digitalidentity.saml.extension.SamlLoginPostProcessor;
import dk.digitalidentity.saml.model.TokenUser;
import lombok.extern.log4j.Log4j;

@Log4j
@Component
public class LoginPostProcesser implements SamlLoginPostProcessor {
	private UserService userService;
	private AdministratorService administratorService;
	private MunicipalityService municipalityService;
	
	public LoginPostProcesser(UserService userService, AdministratorService administratorService, MunicipalityService municipalityService) {
		this.userService = userService;
		this.administratorService = administratorService;
		this.municipalityService = municipalityService;
	}

	@Override
	@Transactional
	public void process(TokenUser tokenUser) {
		String uuid = (String) tokenUser.getAttributes().get(TokenUser.ATTRIBUTE_UUID);
		
		if (municipalityService.getByCvr(tokenUser.getCvr()) == null) {
			log.warn("User with uuid=" + uuid.toLowerCase() + ", belongs to unknown organisation with cvr=" + tokenUser.getCvr());

			throw new UsernameNotFoundException("Ukendt CVR: " + tokenUser.getCvr());			
		}
		
		User user = null;
		List<User> users = userService.getByUuidAndCvrIncludingInactive(uuid.toLowerCase(), tokenUser.getCvr());
		
		// find any existing user account (prefer active ones)
		if (users != null && users.size() > 0) {
			Optional<User> activeUser = users.stream().filter(u -> u.isActive()).findFirst();
			
			if (activeUser.isPresent()) {
				user = activeUser.get();
			}
			else {
				user = users.get(0);
			}
		}

		// no existing account, create one
		if (user == null) {
			// simple check to make sure we have a uuid to work with
			if (uuid.length() == 0) {
				log.warn("User with uuid=" + uuid.toLowerCase() + ", does not exist in organisation with cvr=" + tokenUser.getCvr());

				throw new UsernameNotFoundException(tokenUser.getCvr() + "/" + uuid.toLowerCase());
			}
			
			Object givenName = tokenUser.getAttributes().get("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname");
			Object surName = tokenUser.getAttributes().get("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname");
			String name = tokenUser.getUsername();
			if (givenName != null && surName != null) {
				name = (String) givenName + " " + (String) surName;
			}
			else {
				String nameCandidate = getNameIdValue("CN", name);
				if (nameCandidate.length() > 0) {
					name = nameCandidate;
				}				
			}

			Object emailValue = tokenUser.getAttributes().get("email");
			String email = "";
			if (emailValue != null) {
				email = (String) emailValue;
			}

			// default to creating a simple user with this uuid within that CVR
			user = new User();
			user.setUuid(uuid.toLowerCase());
			user.setCvr(tokenUser.getCvr());
			user.setName(name);
			user.setEmail(email);
			user.setActive(true);
			user.setPositions(new ArrayList<>());
			userService.save(user);
		}
		
		// quick-fix for users that are actually inactive, but are logging in anyway (reactive account)
		if (!user.isActive()) {
			user.setActive(true);
			userService.save(user);
		}

		// flex relations, so we are sure they are loaded into memory (db-session expires at the end of this call)
		for (OrgUnit orgUnit : user.getPositions()) {
			orgUnit.getCvr();
		}

		List<GrantedAuthority> newAuthorities = new ArrayList<>();
		newAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

		for (GrantedAuthority authority : tokenUser.getAuthorities()) {
			switch (authority.getAuthority()) {
				case "ROLE_http://os2autoproces.eu/roles/usersystemrole/local_superuser/1":
					newAuthorities.add(new SimpleGrantedAuthority(SecurityUtil.ROLE_LOCAL_SUPERUSER));
					break;
				case "ROLE_http://os2autoproces.eu/roles/usersystemrole/superuser/1":
					newAuthorities.add(new SimpleGrantedAuthority(SecurityUtil.ROLE_SUPERUSER));
					break;
				default:
					break;
			}
		}
		
		List<Administrator> administratorRoles = administratorService.getByUserUuid(uuid);
		if (administratorRoles != null && administratorRoles.size() > 0) {
			for (Administrator administrator : administratorRoles) {
				switch (administrator.getRole()) {
					case "frontpage_editor":
						newAuthorities.add(new SimpleGrantedAuthority(SecurityUtil.ROLE_FRONTPAGE_EDITOR));
						break;
					case "administrator":
						newAuthorities.add(new SimpleGrantedAuthority(SecurityUtil.ROLE_ADMINISTRATOR));
						break;
					default:
						break;		
				}
			}
		}

		tokenUser.getAttributes().put("user", new AuthenticatedUser(user));
		tokenUser.setAuthorities(newAuthorities);
	}
	
	private static String getNameIdValue(String field, String nameId) {
		StringBuilder builder = new StringBuilder();

		int idx = nameId.indexOf(field + "=");
		if (idx >= 0) {
			for (int i = idx + field.length() + 1; i < nameId.length(); i++) {
				if (nameId.charAt(i) == ',') {
					break;
				}

				builder.append(nameId.charAt(i));
			}
		}

		return builder.toString();
	}
}
