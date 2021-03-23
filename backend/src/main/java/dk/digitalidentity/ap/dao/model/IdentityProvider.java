package dk.digitalidentity.ap.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Data
public class IdentityProvider {
	
	@Id
	@Column
	@JsonIgnore
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private String entityId;
	
	@Column
	private String name;
	
	@Column
	@JsonIgnore
	private String metadata;

	@Column
	@JsonIgnore
	private String cvr;
}
