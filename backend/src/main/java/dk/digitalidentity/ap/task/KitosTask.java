package dk.digitalidentity.ap.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import dk.digitalidentity.ap.dao.model.Service;
import dk.digitalidentity.ap.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import dk.digitalidentity.ap.dao.model.ItSystem;
import dk.digitalidentity.ap.service.ItSystemService;
import dk.digitalidentity.ap.service.kitos.KitosClientService;
import dk.kitos.api.model.ItSystemResponseDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableScheduling
public class KitosTask {

	@Value("${scheduled.enabled:false}")
	private boolean runScheduled;

	@Value("${kitos.rest.username}")
	private String kitosUsername;

	@Value("${kitos.rest.password}")
	private String kitosPassword;

	@Autowired
	private ItSystemService itSystemService;

	@Autowired
	private KitosClientService kitosService;

	@Autowired
	private ServiceService serviceService;

	// fetch data every night at 03:00
	@Scheduled(cron = "0 0 3 * * *")
	public void syncITSystemsFromKitos() {

		if (!runScheduled || !isConfigured()) {
			return;
		}

		log.info("Running Scheduled Task: " + this.getClass().getName());

		List<ItSystem> itSystemsFromKitos = new ArrayList<>();
		try {
			List<ItSystemResponseDTO> kitosResponse = kitosService.listSystems();

			for (ItSystemResponseDTO itSystemDTO : kitosResponse) {
				ItSystem itSystem = new ItSystem();
				itSystem.setName(itSystemDTO.getName());
				itSystem.setKitosUUID(itSystemDTO.getUuid().toString());
				itSystem.setVendor((itSystemDTO.getRightsHolder() != null) ? itSystemDTO.getRightsHolder().getName() : null);
				itSystem.setFromKitos(true);

				itSystemsFromKitos.add(itSystem);
			}
		}
		catch (Exception ex) {
			log.error("Failed to call KITOS", ex);
			return;
		}

		// load ALL it-systems from the database (cache actually) apply filter only from KITOS
		List<ItSystem> itSystemFromDB = itSystemService.findAll().stream().filter(ItSystem::isFromKitos).collect(Collectors.toList());

		// iterate over all the ItSystems from KITOS
		for (ItSystem kitosItSystem : itSystemsFromKitos) {
			// for each ItSystem from KITOS, iterate over all the it-systems
			// from the database, and look for one that has a matching systemId
			ItSystem foundItSystem = null;
			for (ItSystem dbItSystem : itSystemFromDB) {
				if (Objects.equals(kitosItSystem.getKitosUUID(), dbItSystem.getKitosUUID())) {
					foundItSystem = dbItSystem;
					break;
				}
			}

			// if one is found, update the Name/Vendor attributes (if they are
			// different), and call dao.save() on the modified database object
			if (foundItSystem != null) {
				boolean modified = false;

				// name
				if (!kitosItSystem.getName().equals(foundItSystem.getName())) {
					String oldName = foundItSystem.getName();
					foundItSystem.setName(kitosItSystem.getName());

					// update names on services
					List<Service> services = serviceService.getServicesByName(oldName);
					for (Service service : services) {
						service.setName(kitosItSystem.getName());
					}
					serviceService.saveAll(services);

					modified = true;
				}

				// vendor (might be null)
				if ((kitosItSystem.getVendor() == null && foundItSystem.getVendor() != null) ||
					(kitosItSystem.getVendor() != null && !kitosItSystem.getVendor().equals(foundItSystem.getVendor()))) {
					foundItSystem.setVendor(kitosItSystem.getVendor());
					modified = true;
				}

				if (modified) {
					itSystemService.save(foundItSystem);
				}
			}
			else {
				// if none is found, call dao.save() on the object returned from KITOS,
				// as it is a new it-system and we should create it
				itSystemService.save(kitosItSystem);
			}

			// note that we do not delete it-systems.... we might have references to it-systems in our database, so deleting them would
			// be a bad idea, even if they have been deleted from KITOS
		}
	}

	public boolean isConfigured() {
		if (!StringUtils.hasLength(kitosUsername) || !StringUtils.hasLength(kitosPassword)) {
			log.warn("No KITOS username/password configured!");
			return false;
		}

		return true;
	}
}
