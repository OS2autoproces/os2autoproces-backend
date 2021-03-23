package dk.digitalidentity.ap.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dk.digitalidentity.ap.dao.model.OrgUnit;
import dk.digitalidentity.ap.dao.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticatedUser implements Serializable {
	private static final long serialVersionUID = -9007571079970703155L;

	private long id;
	private List<String> positionOrgUnitUuids;
	private String name;
	private String uuid;
	private String cvr;
	private String email;
	
	public AuthenticatedUser() {
		;
	}

	public AuthenticatedUser(User user) {
		this.setCvr(user.getCvr());
		this.setEmail(user.getEmail());
		this.setId(user.getId());
		this.setName(user.getName());
		this.setUuid(user.getUuid());
		this.setPositionOrgUnitUuids(new ArrayList<>());

		if (user.getPositions() != null) {
			for (OrgUnit position : user.getPositions()) {
				this.getPositionOrgUnitUuids().add(position.getUuid());
			}
		}
	}
}
