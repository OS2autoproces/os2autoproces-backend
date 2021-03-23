package dk.digitalidentity.ap.api.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
	private String uuid;
	private String name;
	private String email;
	private List<String> positions;
}
