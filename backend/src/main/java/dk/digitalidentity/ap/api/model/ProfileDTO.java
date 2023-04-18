package dk.digitalidentity.ap.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDTO {
	private String name;
	private String email;
	private String uuid;
	private String cvr;
}
