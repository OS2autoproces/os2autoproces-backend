package dk.digitalidentity.ap.task.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class STSUser {	
	private String uuid;
	private String name;
	private String userId;
	private String email;
	private String telephone;
	private List<STSPosition> positions;
}
