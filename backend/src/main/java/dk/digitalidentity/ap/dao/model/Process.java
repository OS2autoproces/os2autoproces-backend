package dk.digitalidentity.ap.dao.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import dk.digitalidentity.ap.dao.model.enums.Domain;
import dk.digitalidentity.ap.dao.model.enums.Level;
import dk.digitalidentity.ap.dao.model.enums.Phase;
import dk.digitalidentity.ap.dao.model.enums.ProcessType;
import dk.digitalidentity.ap.dao.model.enums.Status;
import dk.digitalidentity.ap.dao.model.enums.Visibility;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Process {
	
	// IDs
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private Long id;

	@Column
	@Size(max = 64)
	private String localId;

	@Column
	@Size(max = 64)
	private String klId;

	@Column
	@Size(max = 300)
	private String esdhReference;

	// metadata

	@Column
	@Enumerated(EnumType.STRING)
	private Phase phase;

	@Column
	@Enumerated(EnumType.STRING)
	private Status status;
	
	@Column(updatable = false, name = "process_type")
	@Enumerated(EnumType.STRING)
	private ProcessType type;

	@Column
	@Size(max = 1200)
	private String statusText;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(updatable = false)
	private Date created;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column
	private Date lastChanged;

	@Temporal(TemporalType.TIMESTAMP)
	@Column
	private Date decommissioned;

	@Column
	@Enumerated(EnumType.STRING)
	private Visibility visibility;

	@Column
	@Size(max = 140)
	private String legalClause;

	@Column
	private Date legalClauseLastVerified;

	@Column
	@Size(max = 8)
	private String kle;
	
	@Column
	@Size(max = 11)
	private String form;

	@Column
	@Size(max = 14)
	private String kla;

	@Column
	@ReadOnlyProperty
	@NotNull
	private boolean klaProcess;

	// relationships

	@OneToMany
	@JoinTable(name = "process_ous", joinColumns = @JoinColumn(name = "process_id"), inverseJoinColumns = @JoinColumn(name = "ou_id"))
	private List<OrgUnit> orgUnits;
	
	@OneToMany
	@JoinTable(name = "process_it_systems", joinColumns = @JoinColumn(name = "process_id"), inverseJoinColumns = @JoinColumn(name = "it_system_id"))
	private List<ItSystem> itSystems;
	
	@Column
	@Size(max = 300)
	private String itSystemsDescription;
	
	@OneToMany
	@JoinTable(name = "process_children", joinColumns = @JoinColumn(name = "process_id"), inverseJoinColumns = @JoinColumn(name = "child_id"))
	private List<Process> children;
	
	@JsonIgnore
	@OneToMany
	@JoinTable(name = "process_children", joinColumns = @JoinColumn(name = "child_id"), inverseJoinColumns = @JoinColumn(name = "process_id"))
	private List<Process> parents;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reporter", nullable = false)
	@ReadOnlyProperty
	private User reporter;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contact", nullable = false)
	private User contact;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner", nullable = false)
	private User owner;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "process_links", joinColumns = @JoinColumn(name = "process_id"), inverseJoinColumns = @JoinColumn(name = "link_id"))
	private List<Link> links;

	@Column
	@Size(max = 255)
	private String vendor;

	@Column
	@ReadOnlyProperty
	private String cvr;

	@Column
	@ReadOnlyProperty
	private String municipalityName;
	
	@OneToMany
	@JoinTable(name = "process_users", joinColumns = { @JoinColumn(name = "process_id") }, inverseJoinColumns = { @JoinColumn(name = "user_id") })
	private List<User> users;

	@OneToMany
	@JoinTable(name = "process_technologies", joinColumns = { @JoinColumn(name = "process_id") }, inverseJoinColumns = { @JoinColumn(name = "technology_id") })
	private List<Technology> technologies;

	@ElementCollection(targetClass = Domain.class)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = "process_domains", joinColumns = @JoinColumn(name = "process_id"))
	@Column(name = "domain")
	private List<Domain> domains;

	@NotAudited
	@JsonIgnore
    @RestResource(exported = false)
	@OneToMany
	@JoinTable(name = "bookmarks", joinColumns = { @JoinColumn(name = "process_id") }, inverseJoinColumns = { @JoinColumn(name = "user_id") })
	private List<User> bookmarkUsers;

	// actual process data

	@Column
	@NotNull
	@Size(max = 50)
	private String title;

	@Column
	@NotNull
	@Size(max = 140)
	private String shortDescription;

	@Column
	@Size(max = 1200)
	private String longDescription;

	@Column
	@Size(max = 2400)
	private String internalNotes;

	@Column
	@Size(max = 1200)
	private String processChallenges;

	@Column
	@Size(max = 2400)
	private String solutionRequests;

	@Column
	@NotNull
	@Min(value = 0)
	private int timeSpendOccurancesPerEmployee;

	@Column
	@NotNull
	@Min(value = 0)
	private int timeSpendPerOccurance;

	@Column
	@NotNull
	@Min(value = 0)
	private int timeSpendEmployeesDoingProcess;

	@Column
	@NotNull
	@Min(value = 0)
	@Max(value = 100)
	private int timeSpendPercentageDigital;

	@Column
	@NotNull
	@ReadOnlyProperty
	private int timeSpendComputedTotal;
	
	@Column
	@Size(max = 300)
	private String timeSpendComment;

	@Column
	@NotNull
	private boolean targetsCompanies;

	@Column
	@NotNull
	private boolean targetsCitizens;

	@Column
	@Enumerated(EnumType.STRING)
	private Level levelOfProfessionalAssessment;

	@Column
	@Enumerated(EnumType.STRING)
	private Level levelOfChange;

	@Column
	@Enumerated(EnumType.STRING)
	private Level levelOfStructuredInformation;

	@Column
	@Enumerated(EnumType.STRING)
	private Level levelOfUniformity;

	@Column
	@Enumerated(EnumType.STRING)
	private Level levelOfDigitalInformation;
	
	@Column
	@Enumerated(EnumType.STRING)
	private Level levelOfQuality;
	
	@Column
	@Enumerated(EnumType.STRING)
	private Level levelOfSpeed;
	
	@Column
	@Enumerated(EnumType.STRING)
	private Level levelOfRoutineWorkReduction;
	
	@Column
	@Enumerated(EnumType.STRING)
	private Level evaluatedLevelOfRoi;

	@Column
	@Size(max = 3000)
	private String technicalImplementationNotes;

	@Column
	@Size(max = 3000)
	private String organizationalImplementationNotes;

	@Column
	@NotNull
	@Min(value = 0)
	@Max(value = 3) // TODO: is max 3?
	private int rating;

	@Column
	@Size(max = 1200)
	private String ratingComment;

	@Column
	@Size(max = 1000)
	private String searchWords;

	// this one exists solely because we need to use it for searching purposes ;)
	@JsonIgnore
	@Column
	private String freetext;

	// this is a hack, to ensure we have a field we can map validation errors to,
	// when we do not want to expose the actual data
	@JsonIgnore
	@Column(name = "freetext", insertable = false, updatable = false)
	private String illegalAccessField;
	
	// these methods exist only so the tests will ignore this method *sigh*
	// TODO: perhaps have our tests look at @JsonIgnore on the fields instead of the methods

	@JsonIgnore
	public List<User> getBookmarkUsers() {
		return bookmarkUsers;
	}
	
	@JsonIgnore
	public String getFreetext() {
		return freetext;
	}
	
	@JsonIgnore
	public String getIllegalAccessField() {
		return "N/A";
	}
}
