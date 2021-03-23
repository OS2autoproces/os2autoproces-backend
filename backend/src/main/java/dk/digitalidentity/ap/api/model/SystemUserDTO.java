package dk.digitalidentity.ap.api.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SystemUserDTO {
	private String uuid;
	private String email;
	private String name;
	private String cvr;
	private List<String> roles;
}