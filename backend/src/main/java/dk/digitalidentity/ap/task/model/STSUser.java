package dk.digitalidentity.ap.task.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class STSUser {	
	private String uuid;
	private String name;
	private String userId;
	private String email;
	private String telephone;
	private List<STSPosition> positions;
}
