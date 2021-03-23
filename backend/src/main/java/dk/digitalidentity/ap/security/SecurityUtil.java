package dk.digitalidentity.ap.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.dao.model.OrgUnit;
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

	public static User getUser() {
		User user = null;

		if (isUserLoggedIn()) {
			user = (User) ((TokenUser) SecurityContextHolder.getContext().getAuthentication().getDetails()).getAttributes().get("user");
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
		ModelMapper mm = new ModelMapper();
		Process mappedProcess = mm.map(process, Process.class);

		return canEdit(mappedProcess);
	}

	public static boolean canEdit(Process process) {
		if (process.getType().equals(ProcessType.GLOBAL_PARENT)) {
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
				List<OrgUnit> reporterOrgUnits = process.getReporter().getPositions();
				List<OrgUnit> userOrgUnits = SecurityUtil.getUser().getPositions();

				if (Collections.disjoint(reporterOrgUnits, userOrgUnits) == false) {
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
					 CollectionUtils.intersection(getUser().getPositions(), process.getReporter().getPositions()).size() > 0) {
				return true;
			}
			else if (process.getUsers().stream().anyMatch(u -> u.getId() == getUser().getId())) {
				return true;
			}
		}

		return false;
	}
}
