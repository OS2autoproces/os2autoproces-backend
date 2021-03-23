package dk.digitalidentity.ap.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "municipalities")
public class Municipality {
	
	@Id
	@Column
	@JsonIgnore
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column
	private String name;

	@Column
	private String cvr;
	
	@JsonIgnore
	@Column
	private String apiKey;
	
	@JsonIgnore
	@Column
	private boolean stsSync;
}
