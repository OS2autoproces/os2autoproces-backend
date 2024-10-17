package dk.digitalidentity.ap.dao.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import dk.digitalidentity.ap.dao.model.enums.Employees;
import dk.digitalidentity.ap.dao.model.enums.Inhabitants;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "municipalities")
public class Municipality {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private String name;

	@Column
	private String cvr;

	@Enumerated(EnumType.STRING)
	@Column
	private Inhabitants inhabitants;

	@Enumerated(EnumType.STRING)
	@Column
	private Employees employees;

	@Column
	private String autoOtherContactEmail;
	
	@JsonIgnore
	@Column
	private String apiKey;
	
	@JsonIgnore
	@Column
	private boolean stsSync;
	
	@JsonIgnore
	@Column
	private boolean disabled;
	
	@JsonIgnore
	@Column
	private boolean allowNameUpdate;

	@OneToOne(mappedBy = "municipality")
	private Logo logo;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "local_admin")
	private User localAdmin;

	@OneToMany
	@JoinTable(name = "municipality_technologies", joinColumns = { @JoinColumn(name = "municipality_id") }, inverseJoinColumns = { @JoinColumn(name = "technology_id") })
	private List<Technology> technologies;

	@OneToMany
	@JoinTable(name = "municipality_it_systems", joinColumns = { @JoinColumn(name = "municipality_id") }, inverseJoinColumns = { @JoinColumn(name = "it_system_id") })
	private List<ItSystem> itSystems;
}
