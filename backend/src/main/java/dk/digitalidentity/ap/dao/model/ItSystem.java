package dk.digitalidentity.ap.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "it_systems")
public class ItSystem {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "kitos_uuid")
	@JsonIgnore
	private String kitosUUID;

	@Column
	@NotNull
	@Size(max = 255)
	private String name;

	@Column
	@Size(max = 128)
	private String vendor;

	@Column
	private boolean fromKitos;

	public ItSystem cloneMe() {
		ItSystem itSystem = new ItSystem();
		itSystem.setId(id);
		itSystem.setKitosUUID(kitosUUID);
		itSystem.setName(name);
		itSystem.setVendor(vendor);
		itSystem.setFromKitos(fromKitos);
		
		return itSystem;
	}
}
