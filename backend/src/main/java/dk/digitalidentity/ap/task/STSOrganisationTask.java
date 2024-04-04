package dk.digitalidentity.ap.task;

import java.time.LocalDate;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	// fetch data every evening at 18:30
	@Scheduled(cron = "0 30 18 * * *")
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
			long ouCount = orgUnitDao.count(predicate);
			if (onlyNew && ouCount > 0) {
				continue;
			}

			try {
				// run on a 7 day schedule
				long dayToRun = Long.parseLong(municipality.getCvr()) % 7 + 1;

				if (ouCount == 0 || LocalDate.now().getDayOfWeek().getValue() == dayToRun) {
					service.loadOrganisation(municipality.getCvr());
				}
			}
			catch (Exception ex) {
				log.error("Failed to load organisation for : " + municipality.getName(), ex);
			}
		}
	}
}
