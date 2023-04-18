package dk.digitalidentity.ap.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "ous")
public class OrgUnit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	@NotNull
	@Size(max = 36)
	private String uuid;

	@Column
	@Size(max = 128)
	private String name;

	@Column
	@NotNull
	private boolean active;

	@NotNull
	@Size(max = 8)
	private String cvr;
}
