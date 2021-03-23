package dk.digitalidentity.ap.task.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class KitosItSystemDTO {
	
	@JsonProperty(value = "Id")
	private long id;
	
	@JsonProperty(value = "Name")
	private String name;
	
	@JsonProperty(value = "BelongsTo")
	private KitosBelongsToDTO vendor;
}
