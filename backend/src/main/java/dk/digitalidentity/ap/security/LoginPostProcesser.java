package dk.digitalidentity.ap.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import dk.digitalidentity.ap.dao.model.OrgUnit;
import dk.digitalidentity.ap.dao.model.User;
import dk.digitalidentity.ap.service.UserService;
import dk.digitalidentity.saml.extension.SamlLoginPostProcessor;
import dk.digitalidentity.saml.model.TokenUser;
import lombok.extern.log4j.Log4j;

@Log4j
@Component
public class LoginPostProcesser implements SamlLoginPostProcessor {
	private UserService userService;

	public LoginPostProcesser(UserService userService) {
		this.userService = userService;
	}

	@Override
	@Transactional
	public void process(TokenUser tokenUser) {
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
				case "ROLE_http://os2autoproces.eu/roles/usersystemrole/frontpage_editor/1":
					newAuthorities.add(new SimpleGrantedAuthority(SecurityUtil.ROLE_FRONTPAGE_EDITOR));
					break;
				case "ROLE_http://os2autoproces.eu/roles/usersystemrole/administrator/1":
					newAuthorities.add(new SimpleGrantedAuthority(SecurityUtil.ROLE_ADMINISTRATOR));
					break;
				default:
					break;
			}
		}

		tokenUser.setAuthorities(newAuthorities);

		String uuid = (String) tokenUser.getAttributes().get(TokenUser.ATTRIBUTE_UUID);
		User user = userService.getByUuidAndCvr(uuid.toLowerCase(), tokenUser.getCvr());
		if (user == null) {
			String msg = "User with uuid=" + uuid.toLowerCase() + ", does not exist in organisation with cvr=" + tokenUser.getCvr();
			log.error(msg);

			throw new UsernameNotFoundException(tokenUser.getCvr() + "/" + uuid.toLowerCase());
		}

		// flex relations, so we are sure they are loaded into memory (db-session expires at the end of this call)
		for (OrgUnit orgUnit : user.getPositions()) {
			orgUnit.getCvr();
		}

		tokenUser.getAttributes().put("user", user);
	}
}
