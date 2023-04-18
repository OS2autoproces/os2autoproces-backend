package dk.digitalidentity.ap.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.dao.model.Municipality;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.User;
import dk.digitalidentity.ap.dao.model.enums.ProcessType;
import dk.digitalidentity.ap.dao.model.enums.Visibility;
import dk.digitalidentity.ap.dao.model.projection.ProcessExtendedProjection;
import dk.digitalidentity.saml.model.TokenUser;

@Component
public class SecurityUtil {
	public static final String ROLE_USER = "ROLE_USER";
	public static final String ROLE_LOCAL_SUPERUSER = "ROLE_LOCAL_SUPERUSER";
	public static final String ROLE_SUPERUSER = "ROLE_SUPERUSER";
	public static final String ROLE_ADMINISTRATOR = "ROLE_ADMINISTRATOR";
	public static final String ROLE_FRONTPAGE_EDITOR = "ROLE_FRONTPAGE_EDITOR";
	public static final String ROLE_SYSTEM = "ROLE_SYSTEM";

	public static String getCvr() {
		String cvr = null;

		if (isUserLoggedIn()) {
			cvr = ((TokenUser) SecurityContextHolder.getContext().getAuthentication().getDetails()).getCvr();
		}

		return cvr;
	}

	public static String getClientCvr() {
		String cvr = null;

		if (isClientLoggedIn()) {
			cvr = ((TokenClient) SecurityContextHolder.getContext().getAuthentication()).getCvr();
		}

		return cvr;
	}

	public static AuthenticatedUser getUser() {
		AuthenticatedUser user = null;

		if (isUserLoggedIn()) {
			user = (AuthenticatedUser) ((TokenUser) SecurityContextHolder.getContext().getAuthentication().getDetails()).getAttributes().get("user");
		}

		return user;
	}

	// should only be used for logging purposes - it is not a useful ID for anything else
	public static String getUserId() {
		String name = null;

		if (isUserLoggedIn()) {
			name = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}

		return name;
	}

	public static List<String> getRoles() {
		List<String> roles = new ArrayList<>();

		if (isUserLoggedIn()) {
			for (GrantedAuthority grantedAuthority : (SecurityContextHolder.getContext().getAuthentication()).getAuthorities()) {
				roles.add(grantedAuthority.getAuthority());
			}
		}

		return roles;
	}

	private static boolean isUserLoggedIn() {
		if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().getDetails() != null && SecurityContextHolder.getContext().getAuthentication().getDetails() instanceof TokenUser) {
			return true;
		}

		return false;
	}
	
	private static boolean isClientLoggedIn() {
		if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication() instanceof TokenClient) {
			return true;
		}

		return false;
	}

	public static boolean canEdit(ProcessExtendedProjection process) {		
		return canEdit(Process.fromProjection(process));
	}
	
	public static void logoutSystem() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}

	public static void loginSystem() {
		Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(ROLE_SYSTEM));

		User user = new User();
		user.setCvr("00000000");
		user.setActive(true);
		user.setEmail("system@os2autoproces.eu");
		user.setName("SYSTEM");
		user.setUuid("00000000-0000-4000-0000-000000000000");

		TokenUser token = TokenUser.builder().authorities(authorities).cvr("00000000").username("SYSTEM").attributes(new HashMap<>()).build();
		token.getAttributes().put("user", new AuthenticatedUser(user));
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("dummy", null, authorities);
		auth.setDetails(token);

		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	public static boolean isLoggedInAsSystemAccount() {
		return "00000000".equals(getCvr());
	}

	public static User loginMunicipality(Municipality municipality) {
		Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(ROLE_SUPERUSER));
		
		User user = new User();
		user.setCvr(municipality.getCvr());
		user.setActive(true);
		user.setEmail(null);
		user.setName("systemkonto");
		user.setUuid("00000000-0000-4000-0000-0000" + municipality.getCvr());

		TokenUser token = TokenUser.builder().authorities(authorities).cvr(municipality.getCvr()).username("systemkonto").attributes(new HashMap<>()).build();
		token.getAttributes().put("user", new AuthenticatedUser(user));
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("dummy", null, authorities);
		auth.setDetails(token);

		SecurityContextHolder.getContext().setAuthentication(auth);
		
		return user;
	}

	public static boolean canEdit(Process process) {
		if (SecurityUtil.getUser() == null) {
			return false;
		}
		else if (getRoles().contains(SecurityUtil.ROLE_SYSTEM)) {
			return true;
		}
		else if (process.getType().equals(ProcessType.GLOBAL_PARENT)) {
			return true;
		}
		else if (!process.getCvr().equals(SecurityUtil.getCvr())) {
			return false;
		}
		else {
			// superusers can always modify processes
			if (SecurityUtil.getRoles().contains(SecurityUtil.ROLE_SUPERUSER)) {
				return true;
			}

			// local superusers can modify processes within own orgunit
			if (SecurityUtil.getRoles().contains(SecurityUtil.ROLE_LOCAL_SUPERUSER)) {
				List<String> reporterOrgUnits = process.getReporter().getPositions().stream().map(p -> p.getUuid()).collect(Collectors.toList());
				List<String> userOrgUnits = SecurityUtil.getUser().getPositionOrgUnitUuids();

				if (!Collections.disjoint(reporterOrgUnits, userOrgUnits)) {
					return true;
				}
			}

			// and original reporter + associated users can modify
			if ((process.getReporter().getId() == SecurityUtil.getUser().getId()) ||
				(process.getUsers().stream().map(u -> u.getId()).collect(Collectors.toList()).contains(SecurityUtil.getUser().getId()))) {

				return true;
			}
		}
		
		return false;
	}

	public static boolean canRead(Process process) {
		if (getUser() == null) {
			return false;
		}
		
		if (getRoles().contains(SecurityUtil.ROLE_SYSTEM)) {
			return true;
		}
		
		boolean isPublic = process.getVisibility().equals(Visibility.PUBLIC);
		boolean isMunicipalityShared = process.getVisibility().equals(Visibility.MUNICIPALITY);
		boolean isSameCvr = process.getCvr().equals(getCvr());
		boolean isReporter = (process.getReporter() != null && process.getReporter().getId() == getUser().getId());
		
		if (isReporter || isPublic || process.getType().equals(ProcessType.GLOBAL_PARENT)) {
			return true;
		}
		else if (isSameCvr) {
			if (isMunicipalityShared) {
				return true;
			}
			else if (getRoles().contains(SecurityUtil.ROLE_SUPERUSER)) {
				return true;
			}
			else if (getRoles().contains(SecurityUtil.ROLE_LOCAL_SUPERUSER) && 
					 	CollectionUtils.intersection(
							 getUser().getPositionOrgUnitUuids(),
							 process.getReporter().getPositions().stream().map(p -> p.getUuid()).collect(Collectors.toList())
					 	).size() > 0) {
				return true;
			}
			else if (process.getUsers().stream().anyMatch(u -> u.getId() == getUser().getId())) {
				return true;
			}
		}

		return false;
	}
}
