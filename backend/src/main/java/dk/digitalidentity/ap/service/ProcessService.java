package dk.digitalidentity.ap.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dk.digitalidentity.ap.dao.ProcessCountHistoryDao;
import dk.digitalidentity.ap.dao.ProcessDao;
import dk.digitalidentity.ap.dao.model.Form;
import dk.digitalidentity.ap.dao.model.Municipality;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.ProcessCountHistory;
import dk.digitalidentity.ap.dao.model.enums.Phase;
import dk.digitalidentity.ap.dao.model.enums.Visibility;
import dk.digitalidentity.ap.security.SecurityUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProcessService {
	
	@Autowired
	private ProcessDao processDao;

	@Autowired
	private MailSenderService mailSenderService;

	@Autowired
	private MunicipalityService municipalityService;

	@Autowired
	private ProcessCountHistoryDao processCountHistoryDao;

	public Process findOne(long id) {
		return processDao.findById(id).orElse(null);
	}
	
	public List<Process> findAll() {
		return processDao.findAll();
	}

	public Process save(Process process) {
		return processDao.save(process);
	}

	public List<Process> findByLastChangedBetween(Date begin, Date end) {
		return processDao.findByLastChangedBetween(begin, end);
	}
	
	public List<String> getAllSearchWords() {
		return processDao.getAllSearchWords();
	}

	public List<Process> getByLegalClausePendingVerification() {
		return processDao.getByLegalClausePendingVerification();
	}
	
	public void setProcessVisibility(Process process, Visibility visibility) {
		processDao.setProcessVisibility(process.getId(), visibility.toString());
	}

	public void updateLegalClauses(List<Form> forms) {
		try {
			SecurityUtil.loginSystem();

			for (Form form : forms) {
				List<Process> processes = processDao.findByForm(form.getCode());

				for (Process process : processes) {
					if (!Objects.equals(process.getLegalClause(), form.getLegalClause())) {
						try {
							process.setLegalClause(form.getLegalClause());
	
							processDao.save(process);
						}
						catch (Exception ex) {
							log.error("Failed to update legalClause on process " + process.getId(), ex);
						}
					}
				}
			}
		}
		finally {
			SecurityUtil.logoutSystem();
		}
	}

	@Transactional
	public void sendNoChangesInAYearNotifications(String frontendBaseUrl, String senderEmailAddress) {
		SecurityUtil.loginSystem();
		Date currentTimestamp = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentTimestamp);
		calendar.add(Calendar.YEAR, -1);
		Date aYearAgo = calendar.getTime();

		List<Process> notModifiedProcesses = processDao.findByLastChangedBefore(aYearAgo);

		if (notModifiedProcesses != null && !notModifiedProcesses.isEmpty()) {
			for (Process process : notModifiedProcesses) {

				// check if municipality is disabled
				Municipality municipality = municipalityService.getByCvr(process.getCvr());
				if (municipality != null && municipality.isDisabled()) {
					continue;
				}

				// only send if visibility is public
				if (!process.getVisibility().equals(Visibility.PUBLIC)) {
					continue;
				}

				// only send if phase eq Udvikling, Implementering or Drift
				if (!process.getPhase().equals(Phase.DEVELOPMENT) || !process.getPhase().equals(Phase.IMPLEMENTATION) || !process.getPhase().equals(Phase.OPERATION)) {
					continue;
				}

				// check if we have already sent the notification less than a year ago
				if (process.getNoChangesNotificationDate() != null && !process.getNoChangesNotificationDate().before(aYearAgo)) {
					continue;
				}

				if (process.getOwner() != null && process.getOwner().getEmail() != null) {
					String subject = "Opdatering af indhold - Proces " + process.getId() + " - " + process.getTitle();

					StringBuilder builder = new StringBuilder();
					builder.append("<p>Kære Kontaktperson</p>");
					builder.append("<p>Det er nu over 1 år siden, at denne proces er blevet opdateret. Du bedes tjekke om data på processen er korrekt, særligt:</p>");
					builder.append("<ul>");
					builder.append("<li>Kontaktperson/kontakt information</li>");
					builder.append("<li>Status på processen</li>");
					builder.append("<li>Synlighed på processer i drift</li>");
					builder.append("</ul>");
					builder.append("<p>Du kan tilgå processen direkte ved at følge dette link: </p>");
					builder.append("<a href=\"").append(frontendBaseUrl).append("details/").append(process.getId()).append("\">").append(frontendBaseUrl).append("details/").append(process.getId()).append("</a>");
					builder.append("<br/><br/><br/>");
					builder.append("<p>Ved at vedligeholde data i OS2autoproces hjælper vi hinanden til at skabe bedre automatiseringsprocesser. Tak fra os alle :)</p");
					builder.append("<br/>");
					builder.append("<p>Foretager du ændringer på processen opdateres feltet Sidst opdateret, og du vil få en reminder igen om et år. Skal der ikke foretages ændringer nu, men ønsker du en reminder om et år, kan du f.eks. skrive en note om du har tjekket processen i feltet Interne noter, så vil Sidst opdateret også blive opdateret.</p>");
					builder.append("<br/>");
					builder.append("<p>Venlig hilsen </p>");
					builder.append("<p>OS2autoproces</p>");

					try {
						mailSenderService.sendMessage(senderEmailAddress, Collections.singletonList(process.getOwner().getEmail()), subject, builder.toString());
						processDao.setNoChangesNotificationDate(process.getId());
					}
					catch (Exception ex) {
						log.error("Failed to send not changed in a year notification for process: " + process.getId(), ex);
					}
				}
			}
		}
		SecurityUtil.logoutSystem();
	}

	@Transactional
	public void count() {
		// create new rows
		List<Process> allProcesses = processDao.findAll().stream().filter(p -> !p.isSepMep() && !p.isKlaProcess()).collect(Collectors.toList());
		List<ProcessCountHistory> newHistoryRows = new ArrayList<>();
		LocalDate today = LocalDate.now();
		String currentQ = findQ(today);

		// create for all processes
		ProcessCountHistory processCountHistory = new ProcessCountHistory();
		processCountHistory.setCountedDate(today);
		processCountHistory.setCountedQuarter(currentQ);
		processCountHistory.setProcessCount(allProcesses.size());
		newHistoryRows.add(processCountHistory);

		// create pr municipality
		for (Municipality municipality : municipalityService.findAll()) {
			ProcessCountHistory processCountHistoryMunicipality = new ProcessCountHistory();
			processCountHistoryMunicipality.setCountedDate(today);
			processCountHistoryMunicipality.setCountedQuarter(currentQ);
			processCountHistoryMunicipality.setProcessCount(allProcesses.stream().filter(p -> p.getCvr().equals(municipality.getCvr())).count());
			processCountHistoryMunicipality.setCvr(municipality.getCvr());
			newHistoryRows.add(processCountHistoryMunicipality);
		}

		processCountHistoryDao.saveAll(newHistoryRows);

		// delete rows older than 3 years
		processCountHistoryDao.deleteByCountedDateBefore(today.minusYears(3));
	}

	private String findQ(LocalDate today) {
		String q;

		if (today.getMonthValue() == 1 || today.getMonthValue() == 2 || today.getMonthValue() == 3 ) {
			q = "Q1";
		} else if (today.getMonthValue() == 4 || today.getMonthValue() == 5 || today.getMonthValue() == 6 ) {
			q = "Q2";
		} else if (today.getMonthValue() == 7 || today.getMonthValue() == 8 || today.getMonthValue() == 9 ) {
			q = "Q3";
		} else {
			q = "Q4";
		}

		return q + " " + today.getYear();
	}
}
