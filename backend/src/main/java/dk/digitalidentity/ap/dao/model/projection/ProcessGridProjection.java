package dk.digitalidentity.ap.dao.model.projection;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.rest.core.config.Projection;

import dk.digitalidentity.ap.config.ApplicationContextProvider;
import dk.digitalidentity.ap.dao.AttachmentDao;
import dk.digitalidentity.ap.dao.BookmarkDao;
import dk.digitalidentity.ap.dao.model.OrgUnit;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.Technology;
import dk.digitalidentity.ap.dao.model.User;
import dk.digitalidentity.ap.dao.model.enums.Domain;
import dk.digitalidentity.ap.dao.model.enums.Phase;
import dk.digitalidentity.ap.dao.model.enums.ProcessType;
import dk.digitalidentity.ap.dao.model.enums.RunPeriod;
import dk.digitalidentity.ap.dao.model.enums.Status;
import dk.digitalidentity.ap.dao.model.enums.Visibility;
import dk.digitalidentity.ap.security.SecurityUtil;

@Projection(name = "grid", types = { Process.class })
public interface ProcessGridProjection {
	Long getId();
	String getTitle();
	String getShortDescription();
	int getRating();
	Phase getPhase();
	Date getLastChanged();
	ProcessType getType();
	Status getStatus();
	List<Domain> getDomains();
	String getKle();
	String getLegalClause();
	String getMunicipalityName();
	Visibility getVisibility();
	long getChildrenCount();
	Integer getTimeSpendOccurancesPerEmployee();
	Double getTimeSpendPerOccurance();
	Integer getTimeSpendPercentageDigital();
	List<Technology> getTechnologies();
	RunPeriod getRunPeriod();

	// the fields below are not used in the ui, but are used in the method search match
	String getLongDescription();
	String getSearchWords();
	String getAutomationDescription();
	String getRatingComment();
	String getTimeSpendComment();
	String getTechnicalImplementationNotes();
	String getOrganizationalImplementationNotes();
	String getSolutionRequests();
	String getProcessChallenges();
	String getOtherContactEmail();
	List<OrgUnit> getOrgUnits();
	User getReporter();
	User getContact();
	User getOwner();

	default boolean isHasBookmarked() {
		if (SecurityUtil.getRoles().contains(SecurityUtil.ROLE_SYSTEM)) {
			return false;
		}

		BookmarkDao bookmarkDao = ApplicationContextProvider.getApplicationContext().getBean("bookmarkDao", BookmarkDao.class);
		if (bookmarkDao == null) {
			return false;
		}

		return (bookmarkDao.getByUserIdAndProcessId(SecurityUtil.getUser().getId(), this.getId()) != null);
	}

	default boolean isHasAttachments() {
		AttachmentDao attachmentDao = ApplicationContextProvider.getApplicationContext().getBean("attachmentDao", AttachmentDao.class);
		if (attachmentDao == null) {
			return false;
		}

		return (attachmentDao.countByProcessId(this.getId()) != 0);
	}

	default String getSearchWord() {
		return SecurityUtil.getFreeTextSearch();
	}

	default String getSearchMatch() {
		String result = null;
		String search = SecurityUtil.getFreeTextSearch();
		if (search != null) {
			if (this.getTitle() != null && this.getTitle().toLowerCase().contains(search.toLowerCase())) {
				result = shortenString(this.getTitle(), search);
			} else if (this.getShortDescription() != null && this.getShortDescription().toLowerCase().contains(search.toLowerCase())) {
				return shortenString(this.getShortDescription(), search);
			} else if (this.getKle() != null &&  this.getKle().toLowerCase().contains(search.toLowerCase())) {
				result = shortenString(this.getKle(), search);
			} else if (this.getLongDescription() != null &&  this.getLongDescription().toLowerCase().contains(search.toLowerCase())) {
				result = shortenString(this.getLongDescription(), search);
			} else if (this.getSearchWords() != null && this.getSearchWords().toLowerCase().contains(search.toLowerCase())) {
				result = shortenString(this.getSearchWords(), search);
			} else if (this.getAutomationDescription() != null && this.getAutomationDescription().toLowerCase().contains(search.toLowerCase())) {
				result = shortenString(this.getAutomationDescription(), search);
			} else if (this.getRatingComment() != null && this.getRatingComment().toLowerCase().contains(search.toLowerCase())) {
				result = shortenString(this.getRatingComment(), search);
			} else if (this.getTimeSpendComment() != null && this.getTimeSpendComment().toLowerCase().contains(search.toLowerCase())) {
				result = shortenString(this.getTimeSpendComment(), search);
			} else if (this.getTechnicalImplementationNotes() != null && this.getTechnicalImplementationNotes().toLowerCase().contains(search.toLowerCase())) {
				result = shortenString(this.getTechnicalImplementationNotes(), search);
			} else if (this.getOrganizationalImplementationNotes() != null && this.getOrganizationalImplementationNotes().toLowerCase().contains(search.toLowerCase())) {
				result = shortenString(this.getOrganizationalImplementationNotes(), search);
			} else if (this.getSolutionRequests() != null && this.getSolutionRequests().toLowerCase().contains(search.toLowerCase())) {
				result = shortenString(this.getSolutionRequests(), search);
			} else if (this.getProcessChallenges() != null && this.getProcessChallenges().toLowerCase().contains(search.toLowerCase())) {
				result = shortenString(this.getProcessChallenges(), search);
			} else if (this.getOtherContactEmail() != null && this.getOtherContactEmail().toLowerCase().contains(search.toLowerCase())) {
				result = shortenString(this.getOtherContactEmail(), search);
			} else if (this.getReporter() != null && this.getReporter().getName().toLowerCase().contains(search.toLowerCase())) {
				result = this.getReporter().getName();
			} else if (this.getContact() != null && this.getContact().getName().toLowerCase().contains(search.toLowerCase())) {
				result = this.getContact().getName();
			} else if (this.getOwner() != null && this.getOwner().getName().toLowerCase().contains(search.toLowerCase())) {
				result = this.getOwner().getName();
			} else if (this.getOrgUnits() != null && this.getOrgUnits().stream().anyMatch(o -> o.getName().toLowerCase().contains(search.toLowerCase()))) {
				result = this.getOrgUnits().stream().filter(o -> o.getName().toLowerCase().contains(search.toLowerCase())).map(o -> o.getName()).collect(Collectors.joining(","));
			}
		}

		return result;
	}

	private String shortenString(String original, String targetWord) {
		if (original == null || original.length() <= 200) {
			return original;
		}

		// find the first occurrence of targetWord within original
		int targetIndex = original.toLowerCase().indexOf(targetWord.toLowerCase());

		// if targetWord is not found or found after the first 200 characters
		if (targetIndex == -1 || targetIndex > 200) {
			// find the start index for the desired number of characters before targetWord
			int charsBefore = 20;
			int startIndex = Math.max(0, original.lastIndexOf(" ", targetIndex - charsBefore) + 1);

			// return a substring starting from startIndex, but with a total length of 200, and add "..." at the beginning and end
			int endIndex = Math.min(startIndex + 200, original.length());
			return (startIndex > 0 ? "..." : "") + original.substring(startIndex, endIndex) + (endIndex < original.length() ? "..." : "");
		} else {
			// find the start index for the desired number of characters before targetWord in the else block
			int charsBefore = 20;
			int startIndex = Math.max(0, original.lastIndexOf(" ", Math.min(targetIndex, targetIndex - charsBefore)) + 1);

			// return a substring starting from startIndex, but with a total length of 200, and add "..." at the beginning and end
			int endIndex = Math.min(startIndex + 200, original.length());
			return (startIndex > 0 ? "..." : "") + original.substring(startIndex, endIndex) + (endIndex < original.length() ? "..." : "");
		}
	}
}
