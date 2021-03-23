package dk.digitalidentity.ap.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Form {

	@Id
	@Column(nullable = false)
	private String code;

	@Column(nullable = false)
	private String description;
	
	@Column(nullable = true)
	private String legalClause;
}
