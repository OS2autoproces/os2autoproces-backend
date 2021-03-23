package dk.digitalidentity.ap.api.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganisationDTO {
	private List<OrgUnitDTO> orgUnits;
	private List<UserDTO> users;
}
