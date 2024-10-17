package dk.digitalidentity.ap.api;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dk.digitalidentity.ap.service.ProcessSeenByService;
import dk.digitalidentity.ap.service.ServiceService;
import dk.digitalidentity.ap.service.model.ProcessSeenByChartDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import dk.digitalidentity.ap.dao.MunicipalityDao;
import dk.digitalidentity.ap.dao.ProcessCountHistoryDao;
import dk.digitalidentity.ap.dao.ProcessDao;
import dk.digitalidentity.ap.dao.TechnologyDao;
import dk.digitalidentity.ap.dao.model.Municipality;
import dk.digitalidentity.ap.dao.model.ProcessCountHistory;
import dk.digitalidentity.ap.dao.model.Technology;
import dk.digitalidentity.ap.dao.model.enums.Phase;
import dk.digitalidentity.ap.security.AuthenticatedUser;
import dk.digitalidentity.ap.security.SecurityUtil;
import dk.digitalidentity.ap.service.ItSystemService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@SecurityRequirement(name = "Authorization")
@Tag(name = "Chart API")
public class ChartApi {

	@Autowired
	private ProcessDao processDao;

	@Autowired
	private TechnologyDao technologyDao;

	@Autowired
	private ItSystemService itSystemService;

	@Autowired
	private MunicipalityDao municipalityDao;

	@Autowired
	private ProcessCountHistoryDao processCountHistoryDao;

	@Autowired
	private ServiceService serviceService;

	@Autowired
	private ProcessSeenByService processSeenByService;

	// maps does not work well with vue.js and typescript that why we use lists
	record OrganisationChartDTO(List<String> totalLabels, List<Long> totalData, List<String> ownLabels, List<Long> ownData) {}
	@GetMapping("/api/charts/phase")
	public ResponseEntity<?> getPhaseChartData() {
		AuthenticatedUser authenticatedUser = SecurityUtil.getUser();
		if (authenticatedUser == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No logged in user.");
		}

		List<String> totalLabels = new ArrayList<>();
		List<Long> totalData = new ArrayList<>();
		List<String> ownLabels = new ArrayList<>();
		List<Long> ownData = new ArrayList<>();
		for (Phase phase : Phase.values()) {
			totalLabels.add(phase.getValue());
			totalData.add(processDao.countByPhase(phase));
			ownLabels.add(phase.getValue());
			ownData.add(processDao.countByPhaseAndCvr(phase, authenticatedUser.getCvr()));
		}

		return ResponseEntity.ok(new OrganisationChartDTO(totalLabels, totalData, ownLabels, ownData));
	}

	@GetMapping("/api/charts/history")
	public ResponseEntity<?> getHistoryChartData() {
		AuthenticatedUser authenticatedUser = SecurityUtil.getUser();
		if (authenticatedUser == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No logged in user.");
		}

		List<String> totalLabels = new ArrayList<>();
		List<Long> totalData = new ArrayList<>();
		List<String> ownLabels = new ArrayList<>();
		List<Long> ownData = new ArrayList<>();
		List<String> quarters = generateLastEightQuarters();

		for (int i = 0; i < quarters.size(); i++) {
			String quarter = quarters.get(i);
			if (i == quarters.size() - 1) {
				totalLabels.add(quarter);
				totalData.add(processDao.countBySepMepFalseAndKlaProcessFalse());
				ownLabels.add(quarter);
				ownData.add(processDao.countByCvrAndSepMepFalseAndKlaProcessFalse(authenticatedUser.getCvr()));
			} else {
				ProcessCountHistory historyTotal = processCountHistoryDao.findByCvrAndCountedQuarter(null, quarter);
				totalLabels.add(quarter);
				if (historyTotal == null) {
					log.warn("Missing rows in ProcessCountHistory tabel to generate history chart for dashboard for quarter " + quarter);
					totalData.add(0L);
				} else {
					totalData.add(historyTotal.getProcessCount());
				}

				ProcessCountHistory historyOwn = processCountHistoryDao.findByCvrAndCountedQuarter(authenticatedUser.getCvr(), quarter);
				ownLabels.add(quarter);
				if (historyOwn == null) {
					log.warn("Missing rows in ProcessCountHistory tabel to generate history chart for dashboard for quarter " + quarter + " and cvr " + authenticatedUser.getCvr());
					ownData.add(0L);
				} else {
					ownData.add(historyOwn.getProcessCount());
				}
			}
		}

		return ResponseEntity.ok(new OrganisationChartDTO(totalLabels, totalData, ownLabels, ownData));
	}

	@GetMapping("/api/charts/technology")
	public ResponseEntity<?> getTechnologyChartData() {
		AuthenticatedUser authenticatedUser = SecurityUtil.getUser();
		if (authenticatedUser == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No logged in user.");
		}

		Map<String, Long> totalMap = new HashMap<>();
		Map<String, Long> ownMap = new HashMap<>();
		
		for (Technology technology : technologyDao.findAll()) {
			long totalCount = processDao.countByTechnologiesContaining(technology);

			if (totalCount != 0) {
				totalMap.put(technology.getName(), totalCount);

				long ownCount = processDao.countByCvrAndTechnologiesContaining(authenticatedUser.getCvr(), technology);
				if (ownCount != 0) {
					ownMap.put(technology.getName(), ownCount);
				}
			}
		}

		List<String> totalLabels = new ArrayList<>();
		List<Long> totalData = new ArrayList<>();
		fillChartLists(totalMap, totalLabels, totalData);

		List<String> ownLabels = new ArrayList<>();
		List<Long> ownData = new ArrayList<>();
		fillChartLists(ownMap, ownLabels, ownData);

		return ResponseEntity.ok(new OrganisationChartDTO(totalLabels, totalData, ownLabels, ownData));
	}

	@GetMapping("/api/charts/system")
	public ResponseEntity<?> getITSystemChartData() {
		AuthenticatedUser authenticatedUser = SecurityUtil.getUser();
		if (authenticatedUser == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No logged in user.");
		}

		/* slow - so slow
		Map<String, Long> totalMap = new HashMap<>();
		Map<String, Long> ownMap = new HashMap<>();
		for (ItSystem itSystem : itSystemService.findAll()) {
			long totalCount = processDao.countByItSystemsContaining(itSystem);
			if (totalCount != 0) {
				totalMap.put(itSystem.getName(), totalCount);

				long ownCount = processDao.countByCvrAndItSystemsContaining(authenticatedUser.getCvr(), itSystem);
				if (ownCount != 0) {
					ownMap.put(itSystem.getName(), ownCount);
				}
			}
		}
		*/

		// fast - so fast
		Map<String, Long> usage = serviceService.getUsageGlobal();
		Map<String, Long> usageCvr = serviceService.getUsageByCvr(authenticatedUser.getCvr());

		List<String> totalLabels = new ArrayList<>();
		List<Long> totalData = new ArrayList<>();
		fillChartLists(usage, totalLabels, totalData);

		List<String> ownLabels = new ArrayList<>();
		List<Long> ownData = new ArrayList<>();
		fillChartLists(usageCvr, ownLabels, ownData);

		return ResponseEntity.ok(new OrganisationChartDTO(totalLabels, totalData, ownLabels, ownData));
	}

	record OrganisationProcessCountChartDTO(String cvr, String municipalityName, long processCount) {}
	@GetMapping("/api/charts/organisation")
	public ResponseEntity<?> getOrganisationChartData() {

		List<OrganisationProcessCountChartDTO> result = new ArrayList<>();
		for (Municipality municipality : municipalityDao.findAll()) {
			result.add(new OrganisationProcessCountChartDTO(municipality.getCvr(), municipality.getName(), processDao.countByCvr(municipality.getCvr())));
		}
		return ResponseEntity.ok(result);
	}

	@GetMapping("/api/charts/seenby")
	public ResponseEntity<?> getSeenByChart() {
		List<ProcessSeenByChartDTO> result = processSeenByService.getTop10ProcessIdsWithTitleAndCount();
		return ResponseEntity.ok(result);
	}

	private List<String> generateLastEightQuarters() {
		LocalDate today = LocalDate.now();
		int year = today.getYear();
		int quarter = (today.getMonthValue() - 1) / 3 + 1;
		List<String> quarters = new ArrayList<>();

		for (int i = 0; i < 8; i++) {
			String quarterName = "Q" + quarter + " " + year;
			quarters.add(quarterName);
			if (quarter == 1) {
				year--;
				quarter = 4;
			} else {
				quarter--;
			}
		}

		Collections.reverse(quarters);

		return quarters;
	}

	private void fillChartLists(Map<String, Long> dataMap, List<String> labels, List<Long> data) {
		List<Map.Entry<String, Long>> sortedEntries = dataMap.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.limit(10)
				.collect(Collectors.toList());

		for (Map.Entry<String, Long> entry : sortedEntries) {
			labels.add(entry.getKey());
			data.add(entry.getValue());
		}
	}
}
