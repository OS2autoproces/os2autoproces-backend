package dk.digitalidentity.ap.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.querydsl.core.types.Predicate;

import dk.digitalidentity.ap.dao.OrgUnitDao;
import dk.digitalidentity.ap.dao.model.Municipality;
import dk.digitalidentity.ap.dao.model.QOrgUnit;
import dk.digitalidentity.ap.service.MunicipalityService;
import lombok.extern.log4j.Log4j;

@Log4j
@Component
@EnableScheduling
public class STSOrganisationTask {

	@Value("${scheduled.enabled:false}")
	private boolean runScheduled;
	
	@Autowired
	private MunicipalityService municipalityService;
	
	@Autowired
	private OrgUnitDao orgUnitDao;
	
	@Autowired
	private STSOrganisationService service;
	
	@Async
	public void init() {
		parse(true);
	}

	// fetch data every night at 03:00
	@Scheduled(cron = "0 0 3 * * *")
	public void parse() {
		parse(false);
	}

	private void parse(boolean onlyNew) {
		if (!runScheduled) {
			return;
		}

		log.info("Running Scheduled Task: " + this.getClass().getName());

		List<Municipality> municipalities = municipalityService.findAll();
		for (Municipality municipality : municipalities) {

			// skip non-sync municipalities
			if (!municipality.isStsSync()) {
				continue;
			}
			
			Predicate predicate = QOrgUnit.orgUnit.cvr.eq(municipality.getCvr());
			if (onlyNew && orgUnitDao.count(predicate) > 0) {
				continue;
			}
			
			try {
				service.loadOrganisation(municipality.getCvr());
			}
			catch (Exception ex) {
				log.error("Failed to load organisation for cvr: " + municipality.getCvr(), ex);
			}
		}
	}
}
