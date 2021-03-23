package dk.digitalidentity.ap.dao.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class Kle {

	@Id
	@Column(nullable = false, length = 8)
	private String code;

	@Column(nullable = false, length = 256)
	private String name;
	
	@OneToMany
	@JoinTable(name = "kle_forms", joinColumns = { @JoinColumn(name = "kle_code") }, inverseJoinColumns = { @JoinColumn(name = "form_code") })
	private List<Form> forms;
}
