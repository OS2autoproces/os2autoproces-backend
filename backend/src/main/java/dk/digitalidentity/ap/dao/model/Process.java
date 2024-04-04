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
import dk.digitalidentity.ap.dao.model.enums.RunPeriod;
import dk.digitalidentity.ap.dao.model.enums.Status;
import dk.digitalidentity.ap.dao.model.enums.Visibility;
import dk.digitalidentity.ap.dao.model.projection.ProcessExtendedProjection;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

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
	@Size(max = 10000)
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
	private Date putIntoOperation;

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
	// by agreement with Sigrid - for Aalborg API usage
	//	@ReadOnlyProperty
	private User reporter;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contact", nullable = false)
	private User contact;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner", nullable = false)
	private User owner;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
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

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinTable(name = "process_services", joinColumns = @JoinColumn(name = "process_id"), inverseJoinColumns = @JoinColumn(name = "service_id"))
	private List<Service> services;

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
	@Size(max = 65)
	private String title;

	@Column
	@NotNull
	@Size(max = 140)
	private String shortDescription;

	@Column
	@Size(max = 10000)
	private String longDescription;

	@Column
	@Size(max = 10000)
	private String internalNotes;

	@Column
	@Size(max = 10000)
	private String processChallenges;

	@Column
	@Size(max = 10000)
	private String solutionRequests;

	@Column
	@NotNull
	@Min(value = 0)
	private int timeSpendOccurancesPerEmployee;

	@Column
	@NotNull
	@Min(value = 0)
	private double timeSpendPerOccurance;

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
	@Size(max = 10000)
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
	@Size(max = 10000)
	private String technicalImplementationNotes;

	@Column
	@Size(max = 10000)
	private String organizationalImplementationNotes;

	@Column
	@NotNull
	@Min(value = 0)
	@Max(value = 3)
	private int rating;

	@Column
	@Size(max = 10000)
	private String ratingComment;

	@Column
	@Size(max = 10000)
	private String automationDescription;

	@Column
	@Size(max = 1000)
	private String searchWords;
	
	@Column
	@Size(max = 300)
	private String codeRepositoryUrl;
	
	@Column
	@Enumerated(EnumType.STRING)
	private RunPeriod runPeriod;
	
	@Column
	@NotNull
	private boolean sepMep;
	
	@Column
	private long childrenCount;

	@Column(name = "expected_development_time")
	private Double expectedDevelopmentTime;

	// this is used for finding out if we should send a notification after one year without change or if we have already sent it
	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column
	private Date noChangesNotificationDate;

	@Column
	@Size(max = 255)
	private String otherContactEmail;

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

	// only to be used by SecurityUtil to make security decisions on the process
	public static Process fromProjection(ProcessExtendedProjection projection) {
		Process p = new Process();
		p.setUsers(projection.getUsers());
		p.setReporter(projection.getReporter());
		p.setType(projection.getType());
		p.setCvr(projection.getCvr());
		
		return p;
	}

	// this seems to be the only safe way to use the filter() method on the readInterceptor
	public Process cloneMe() {
		Process process = new Process();
		process.setAutomationDescription(automationDescription);
		
		if (bookmarkUsers != null) {
			process.setBookmarkUsers(bookmarkUsers.stream().map(bu -> bu.cloneMe()).toList());
		}
		
		if (children != null) {
			process.setChildren(children.stream().map(c -> c.cloneMe()).toList());
		}
		
		process.setChildrenCount(childrenCount);
		process.setCodeRepositoryUrl(codeRepositoryUrl);
		process.setContact(contact.cloneMe());
		process.setCreated(created);
		process.setCvr(cvr);
		process.setDecommissioned(decommissioned);
		
		if (domains != null) {
			process.setDomains(domains);
		}
		
		process.setEsdhReference(esdhReference);
		process.setEvaluatedLevelOfRoi(evaluatedLevelOfRoi);
		process.setExpectedDevelopmentTime(expectedDevelopmentTime);
		process.setForm(form);
		process.setFreetext(freetext);
		process.setId(id);
		process.setIllegalAccessField(illegalAccessField);
		process.setInternalNotes(internalNotes);
		
		if (itSystems != null) {
			process.setItSystems(itSystems.stream().map(i -> i.cloneMe()).toList());
		}
		
		process.setItSystemsDescription(itSystemsDescription);
		process.setKla(kla);
		process.setKlaProcess(klaProcess);
		process.setKle(kle);
		process.setKlId(klId);
		process.setLastChanged(lastChanged);
		process.setLegalClause(legalClause);
		process.setLegalClauseLastVerified(legalClauseLastVerified);
		process.setLevelOfChange(levelOfChange);
		process.setLevelOfDigitalInformation(levelOfDigitalInformation);
		process.setLevelOfProfessionalAssessment(levelOfProfessionalAssessment);
		process.setLevelOfQuality(levelOfQuality);
		process.setLevelOfRoutineWorkReduction(levelOfRoutineWorkReduction);
		process.setLevelOfSpeed(levelOfSpeed);
		process.setLevelOfStructuredInformation(levelOfStructuredInformation);
		process.setLevelOfUniformity(levelOfUniformity);
		
		if (links != null) {
			process.setLinks(links.stream().map(l -> l.cloneMe()).toList());
		}
		
		process.setLongDescription(longDescription);
		process.setMunicipalityName(municipalityName);
		process.setNoChangesNotificationDate(noChangesNotificationDate);
		process.setOrganizationalImplementationNotes(organizationalImplementationNotes);
		
		if (orgUnits != null) {
			process.setOrgUnits(orgUnits.stream().map(o -> o.cloneMe()).toList());
		}
		
		process.setOtherContactEmail(otherContactEmail);
		
		if (owner != null) {
			process.setOwner(owner.cloneMe());
		}
		
		// nope, we should never look up (I hope)... otherwise we could end up cloning the entire database
		// process.setParents(parents);
		
		process.setPhase(phase);
		process.setProcessChallenges(processChallenges);
		process.setPutIntoOperation(putIntoOperation);
		process.setRating(rating);
		process.setRatingComment(ratingComment);

		if (reporter != null) {
			process.setReporter(reporter.cloneMe());
		}
		
		process.setRunPeriod(runPeriod);
		process.setSearchWords(searchWords);
		process.setSepMep(sepMep);
		
		if (services != null) {
			process.setServices(services.stream().map(s -> s.cloneMe()).toList());
		}
		
		process.setShortDescription(shortDescription);
		process.setSolutionRequests(solutionRequests);
		process.setStatus(status);
		process.setStatusText(statusText);
		process.setTargetsCitizens(targetsCitizens);
		process.setTargetsCompanies(targetsCompanies);
		process.setTechnicalImplementationNotes(technicalImplementationNotes);
		
		if (technologies != null) {
			process.setTechnologies(technologies.stream().map(t -> t.cloneMe()).toList());
		}
		
		process.setTimeSpendComment(timeSpendComment);
		process.setTimeSpendComputedTotal(timeSpendComputedTotal);
		process.setTimeSpendEmployeesDoingProcess(timeSpendEmployeesDoingProcess);
		process.setTimeSpendOccurancesPerEmployee(timeSpendOccurancesPerEmployee);
		process.setTimeSpendPercentageDigital(timeSpendPercentageDigital);
		process.setTimeSpendPerOccurance(timeSpendPerOccurance);
		process.setTitle(title);
		process.setType(type);

		if (users != null) {
			process.setUsers(users.stream().map(u -> u.cloneMe()).toList());
		}
		
		process.setVendor(vendor);
		process.setVisibility(visibility);
		
		return process;
	}
}
