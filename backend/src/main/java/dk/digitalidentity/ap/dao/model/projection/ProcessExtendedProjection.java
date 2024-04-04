package dk.digitalidentity.ap.dao.model.projection;

import java.util.Date;
import java.util.List;

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
	int getTimeSpendOccurancesPerEmployee();
	double getTimeSpendPerOccurance();
	int getTimeSpendEmployeesDoingProcess();
	int getTimeSpendComputedTotal();
	int getTimeSpendPercentageDigital();
	String getTimeSpendComment();
	boolean isTargetsCompanies();
	boolean isTargetsCitizens();
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
		BookmarkDao bookmarkDao = ApplicationContextProvider.getApplicationContext().getBean("bookmarkDao", BookmarkDao.class);
		if (bookmarkDao == null) {

			return false;
		}

		return (bookmarkDao.getByUserIdAndProcessId(SecurityUtil.getUser().getId(), this.getId()) != null);
	}
	
	default boolean isEmailNotification() {
		NotificationDao notificationDao = ApplicationContextProvider.getApplicationContext().getBean("notificationDao", NotificationDao.class);
		if (notificationDao == null) {

			return false;
		}

		return (notificationDao.getByUserIdAndProcessId(SecurityUtil.getUser().getId(), this.getId()) != null);
	}
}
