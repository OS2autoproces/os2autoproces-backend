package dk.digitalidentity.ap.task.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class STSHierarchyWrapper {
	private STSHierarchy result;
	private long status;
}
