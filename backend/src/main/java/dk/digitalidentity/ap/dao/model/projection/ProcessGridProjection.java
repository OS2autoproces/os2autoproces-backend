package dk.digitalidentity.ap.dao.model.projection;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.data.rest.core.config.Projection;

import dk.digitalidentity.ap.config.ApplicationContextProvider;
import dk.digitalidentity.ap.dao.BookmarkDao;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.enums.Domain;
import dk.digitalidentity.ap.dao.model.enums.Phase;
import dk.digitalidentity.ap.dao.model.enums.ProcessType;
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
	ProcessType getType();
	Status getStatus();
	List<Domain> getDomains();
	String getKle();
	String getLegalClause();
	String getMunicipalityName();
	Visibility getVisibility();
	
	default boolean isHasBookmarked() {
		BookmarkDao bookmarkDao = ApplicationContextProvider.getApplicationContext().getBean("bookmarkDao", BookmarkDao.class);
		if (bookmarkDao == null) {
			Logger log = Logger.getLogger(ProcessExtendedProjection.class);
			log.error("BookmarkDao is null.");

			return false;
		}

		return (bookmarkDao.getByUserAndProcessId(SecurityUtil.getUser(), this.getId()) != null);
	}
}
