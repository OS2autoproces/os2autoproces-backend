package dk.digitalidentity.ap.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import dk.digitalidentity.ap.dao.model.Kle;
import dk.digitalidentity.ap.service.KleService;
import dk.digitalidentity.ap.service.KleToFormService;
import dk.kleonline.emner.EmneType;
import dk.kleonline.emner.KLEEmner;
import dk.kleonline.grupper.GruppeType;
import dk.kleonline.grupper.KLEGrupper;
import dk.kleonline.hovedgrupper.HovedgruppeType;
import dk.kleonline.hovedgrupper.KLEHovedgrupper;
import lombok.extern.log4j.Log4j;

@Component
@EnableScheduling
@EnableAsync
@Log4j
public class KleTask {
	
	@Value("${scheduled.enabled:false}")
	private boolean runScheduled;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private KleService kleService;
	
	@Autowired
	private KleToFormService kleToFormService;

	@Async
	public void init() {
		if (runScheduled && kleService.count() == 0) {
			parse();
		}
	}

	// Run every Saturday at 21:00
	@Scheduled(cron = "0 0 21 * * SAT")
	@Transactional(rollbackFor = Exception.class)
	public synchronized void parse() {
		if (!runScheduled) {
			return;
		}

		log.info("Running Scheduled Task: " + this.getClass().getName());

		List<Kle> updatedKleList = getUpdatedKleList();
		List<Kle> kleList = kleService.findAll();

		for (Kle updatedKle : updatedKleList) {
			boolean found = false;
			boolean nameChange = false;

			for (Iterator<Kle> iterator = kleList.iterator(); iterator.hasNext();) {
				Kle kle = iterator.next();

				if (kle.getCode().equals(updatedKle.getCode())) {
					found = true;

					if (!kle.getName().equals(updatedKle.getName())) {
						nameChange = true;
					}

					iterator.remove();
					break;
				}
			}

			if (found && nameChange) {
				Kle kle = kleService.getByCode(updatedKle.getCode());
				kle.setName(updatedKle.getName());

				kleService.save(kle);
			}
			else if (!found) {
				kleService.save(updatedKle);
			}
		}
		
		kleToFormService.setKleLoaded();
	}

	public List<Kle> getUpdatedKleList() {
		List<Kle> updatedKleList = new ArrayList<Kle>();

		HttpEntity<KLEHovedgrupper> responseMainGroupEntity = restTemplate.getForEntity("http://api.kle-online.dk/resources/hovedgrupper", KLEHovedgrupper.class);
		HttpEntity<KLEGrupper> responseGroupEntity = restTemplate.getForEntity("http://api.kle-online.dk/resources/grupper", KLEGrupper.class);
		HttpEntity<KLEEmner> responseSubjectEntity = restTemplate.getForEntity("http://api.kle-online.dk/resources/emner", KLEEmner.class);

		KLEHovedgrupper kleHovedgrupper = responseMainGroupEntity.getBody();
		KLEGrupper kleGrupper = responseGroupEntity.getBody();
		KLEEmner kleEmner = responseSubjectEntity.getBody();

		for (HovedgruppeType hg : kleHovedgrupper.getHovedgruppe()) {
			Kle kle = new Kle();
			kle.setCode(hg.getHovedgruppeNr());
			kle.setName(hg.getHovedgruppeTitel());
			
			updatedKleList.add(kle);
		}

		for (GruppeType group : kleGrupper.getGruppe()) {
			Kle kle = new Kle();
			kle.setCode(group.getGruppeNr());
			kle.setName(group.getGruppeTitel());

			updatedKleList.add(kle);
		}

		for (EmneType subject : kleEmner.getEmne()) {
			Kle kle = new Kle();
			kle.setCode(subject.getEmneNr());
			kle.setName(subject.getEmneTitel());

			updatedKleList.add(kle);
		}

		return updatedKleList;
	}
	
}
