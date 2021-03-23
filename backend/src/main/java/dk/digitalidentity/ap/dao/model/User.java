package dk.digitalidentity.ap.dao.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "users")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column
	@NotNull
	@Size(max = 36)
	private String uuid;

	@Column
	@NotNull
	@Size(max = 128)
	private String name;

	@Column
	@Size(max = 255)
	private String email;

	@OneToMany
	@JoinTable(name = "users_ous", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "ou_id"))
	private List<OrgUnit> positions;

	@Column
	@NotNull
	private boolean active;

	@NotNull
	@Size(max = 8)
	private String cvr;
}
