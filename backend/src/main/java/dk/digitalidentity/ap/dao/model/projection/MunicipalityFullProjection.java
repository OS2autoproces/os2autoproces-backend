package dk.digitalidentity.ap.dao.model.projection;
import dk.digitalidentity.ap.dao.model.ItSystem;
import dk.digitalidentity.ap.dao.model.Logo;
import dk.digitalidentity.ap.dao.model.Municipality;
import dk.digitalidentity.ap.dao.model.Technology;
import dk.digitalidentity.ap.dao.model.User;
import dk.digitalidentity.ap.dao.model.enums.Employees;
import dk.digitalidentity.ap.dao.model.enums.Inhabitants;
import org.springframework.data.rest.core.config.Projection;

import java.util.List;

// for some reason a projection is needed to get jpa search endpoints to return the id
@Projection(name = "full", types = { Municipality.class })
public interface MunicipalityFullProjection {
	long getId();
	String getName();
	String getCvr();
	Inhabitants getInhabitants();
	Employees getEmployees();
	Logo getLogo();
	String getAutoOtherContactEmail();
	User getLocalAdmin();
	List<Technology> getTechnologies();
	List<ItSystem> getItSystems();
}