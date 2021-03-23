package dk.digitalidentity.ap.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganisationImportResponse {
	private long usersCreated;
	private long usersUpdated;
	private long usersDeleted;
	private long ousCreated;
	private long ousUpdated;
	private long ousDeleted;
}
