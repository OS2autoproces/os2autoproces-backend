package dk.digitalidentity.ap.dao.model.projection;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import dk.digitalidentity.ap.dao.MunicipalityDao;
import dk.digitalidentity.ap.dao.ProcessSeenByDao;
import dk.digitalidentity.ap.dao.model.Municipality;
import dk.digitalidentity.ap.service.S3Service;
import org.springframework.data.rest.core.config.Projection;

import dk.digitalidentity.ap.config.ApplicationContextProvider;
import dk.digitalidentity.ap.dao.BookmarkDao;
import dk.digitalidentity.ap.dao.NotificationDao;
import dk.digitalidentity.ap.dao.model.ItSystem;
import dk.digitalidentity.ap.dao.model.Link;
import dk.digitalidentity.ap.dao.model.OrgUnit;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.Service;
import dk.digitalidentity.ap.dao.model.Technology;
import dk.digitalidentity.ap.dao.model.User;
import dk.digitalidentity.ap.dao.model.enums.Domain;
import dk.digitalidentity.ap.dao.model.enums.Level;
import dk.digitalidentity.ap.dao.model.enums.Phase;
import dk.digitalidentity.ap.dao.model.enums.ProcessType;
import dk.digitalidentity.ap.dao.model.enums.RunPeriod;
import dk.digitalidentity.ap.dao.model.enums.Status;
import dk.digitalidentity.ap.dao.model.enums.Visibility;
import dk.digitalidentity.ap.security.SecurityUtil;
import org.springframework.util.StringUtils;

@Projection(name = "extended", types = { Process.class })
public interface ProcessExtendedProjection {
	Long getId();
	String getKlId();
	String getEsdhReference();
	Phase getPhase();
	ProcessType getType();
	Date getCreated();
	Date getLastChanged();
	Date getPutIntoOperation();
	Date getDecommissioned();
	String getInternalNotes();
	String getSearchWords();
	Status getStatus();
	String getStatusText();
	String getTitle();
	String getShortDescription();
	String getLongDescription();
	List<Domain> getDomains();
	Visibility getVisibility();
	String getLegalClause();
	Date getLegalClauseLastVerified();
	String getForm();
	String getKle();
	String getKla();
	boolean isKlaProcess();
	User getReporter();
	User getContact();
	String getOtherContactEmail();
	User getOwner();
	String getVendor();
	String getCvr();
	String getMunicipalityName();
	List<User> getUsers();
	List<OrgUnit> getOrgUnits();
	List<ItSystem> getItSystems();
	String getItSystemsDescription();
	List<Technology> getTechnologies();
	List<Link> getLinks();
	List<Process> getChildren();
	List<Process> getParents();
	List<Service> getServices();
	String getProcessChallenges();
	String getSolutionRequests();
	Integer getTimeSpendOccurancesPerEmployee();
	Double getTimeSpendPerOccurance();
	Integer getTimeSpendEmployeesDoingProcess();
	int getTimeSpendComputedTotal();
	Integer getTimeSpendPercentageDigital();
	String getTimeSpendComment();
	Boolean getTargetsCompanies();
	Boolean getTargetsCitizens();
	Level getLevelOfProfessionalAssessment();
	Level getLevelOfChange();
	Level getLevelOfStructuredInformation();
	Level getLevelOfUniformity();
	Level getLevelOfDigitalInformation();
	Level getLevelOfQuality();
	Level getLevelOfSpeed();
	Level getLevelOfRoutineWorkReduction();
	Level getEvaluatedLevelOfRoi();
	String getTechnicalImplementationNotes();
	String getOrganizationalImplementationNotes();
	int getRating();
	String getRatingComment();
	String getAutomationDescription();
	String getCodeRepositoryUrl();
	RunPeriod getRunPeriod();
	boolean isSepMep();
	long getChildrenCount();
	Double getExpectedDevelopmentTime();

	default boolean isCanEdit() {
		return SecurityUtil.canEdit(this);
	}

	default boolean isHasBookmarked() {
		if (SecurityUtil.getRoles().contains(SecurityUtil.ROLE_SYSTEM)) {
			return false;
		}
		
		BookmarkDao bookmarkDao = ApplicationContextProvider.getApplicationContext().getBean("bookmarkDao", BookmarkDao.class);
		if (bookmarkDao == null) {
			return false;
		}
		
		if (SecurityUtil.getUser() == null) {
			return false;
		}

		return (bookmarkDao.getByUserIdAndProcessId(SecurityUtil.getUser().getId(), this.getId()) != null);
	}
	
	default boolean isEmailNotification() {
		if (SecurityUtil.getRoles().contains(SecurityUtil.ROLE_SYSTEM)) {
			return false;
		}

		NotificationDao notificationDao = ApplicationContextProvider.getApplicationContext().getBean("notificationDao", NotificationDao.class);
		if (notificationDao == null) {
			return false;
		}
		
		if (SecurityUtil.getUser() == null) {
			return false;
		}

		return (notificationDao.getByUserIdAndProcessId(SecurityUtil.getUser().getId(), this.getId()) != null);
	}

	default long getSeenByCount() {
		ProcessSeenByDao processSeenByDao = ApplicationContextProvider.getApplicationContext().getBean("processSeenByDao", ProcessSeenByDao.class);
		if (processSeenByDao == null) {
			return 0;
		}

		return processSeenByDao.countByProcessId(this.getId());
	}

	default String getBase64Logo() {
		MunicipalityDao municipalityDao = ApplicationContextProvider.getApplicationContext().getBean("municipalityDao", MunicipalityDao.class);
		S3Service s3Service = ApplicationContextProvider.getApplicationContext().getBean("s3Service", S3Service.class);
		if (municipalityDao == null || s3Service == null) {
			return null;
		}

		Municipality municipality = municipalityDao.getByCvr(this.getCvr());
		if (municipality == null || municipality.getLogo() == null) {
			return null;
		}


		try {
			String url = municipality.getLogo().getUrl();
			String fileName = url.substring(url.lastIndexOf("/") + 1);
			byte[] bytes = s3Service.downloadAsBytes(fileName);
			return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes);
		} catch (Exception ex) {
			return null;
		}
	}

	default String getInhabitants() {
		MunicipalityDao municipalityDao = ApplicationContextProvider.getApplicationContext().getBean("municipalityDao", MunicipalityDao.class);
		if (municipalityDao == null) {
			return null;
		}

		Municipality municipality = municipalityDao.getByCvr(this.getCvr());
		if (municipality == null || municipality.getLogo() == null) {
			return null;
		}

		String inhabitants = null;
		if (municipality.getInhabitants() != null) {
			inhabitants = municipality.getInhabitants().getValue();
		}

		return inhabitants;
	}

	default String getEmployees() {
		MunicipalityDao municipalityDao = ApplicationContextProvider.getApplicationContext().getBean("municipalityDao", MunicipalityDao.class);
		if (municipalityDao == null) {
			return null;
		}

		Municipality municipality = municipalityDao.getByCvr(this.getCvr());
		if (municipality == null || municipality.getLogo() == null) {
			return null;
		}

		String employees = null;
		if (municipality.getEmployees() != null) {
			employees = municipality.getEmployees().getValue();
		}

		return employees;
	}

	default boolean isCanEditOtherContact() {
		MunicipalityDao municipalityDao = ApplicationContextProvider.getApplicationContext().getBean("municipalityDao", MunicipalityDao.class);
		if (municipalityDao == null) {
			return false;
		}

		Municipality municipality = municipalityDao.getByCvr(this.getCvr());
		if (municipality == null) {
			return false;
		}

		return !StringUtils.hasLength(municipality.getAutoOtherContactEmail());
	}
}
