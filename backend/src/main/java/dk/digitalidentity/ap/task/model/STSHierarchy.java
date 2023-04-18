package dk.digitalidentity.ap.task.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class STSHierarchy {
	
	private List<STSUser> users;
	
	@JsonProperty(value = "oUs")
	private List<STSOU> ous;
}
