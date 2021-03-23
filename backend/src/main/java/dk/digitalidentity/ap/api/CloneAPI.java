package dk.digitalidentity.ap.api;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.enums.Domain;
import dk.digitalidentity.ap.dao.model.enums.ProcessType;
import dk.digitalidentity.ap.dao.model.enums.Visibility;
import dk.digitalidentity.ap.security.SecurityUtil;
import dk.digitalidentity.ap.service.ProcessService;

@RestController
@CrossOrigin(exposedHeaders = "x-csrf-token")
@RequestMapping("/api/processes")
public class CloneAPI {

	@Autowired
	private ProcessService processService;

	@PostMapping("/{processId}/copy")
	public ResponseEntity<?> getClone(@PathVariable("processId") long processId, HttpServletRequest request) {
		Process process = processService.findOne(processId);
		if (process == null) {
			return ResponseEntity.notFound().build();
		}
		
		if (!process.getType().equals(ProcessType.CHILD)) {
			return ResponseEntity.badRequest().body("Cannot clone parent processes!");
		}

		// let's avoid bad frontend calls ;)
		if (request != null && request.getSession() != null) {
			Long lastCopiedProcess = (Long) request.getSession().getAttribute("lastCopiedProcess");
			if (lastCopiedProcess != null && processId == lastCopiedProcess) {
				return ResponseEntity.badRequest().body("Replay Protection: process was the last copied process!");
			}
			
			request.getSession().setAttribute("lastCopiedProcess", processId);
		}

		Process clone = new Process();
		
		clone.setDomains(process.getDomains());
		clone.setEsdhReference(process.getEsdhReference());
		clone.setEvaluatedLevelOfRoi(process.getEvaluatedLevelOfRoi());
		clone.setKle(process.getKle());
		clone.setKlId(process.getKlId());
		clone.setLegalClause(process.getLegalClause());
		clone.setLegalClauseLastVerified(process.getLegalClauseLastVerified());
		clone.setLevelOfChange(process.getLevelOfChange());
		clone.setLevelOfDigitalInformation(process.getLevelOfDigitalInformation());
		clone.setLevelOfProfessionalAssessment(process.getLevelOfProfessionalAssessment());
		clone.setLevelOfSpeed(process.getLevelOfSpeed());
		clone.setLevelOfQuality(process.getLevelOfQuality());
		clone.setLevelOfRoutineWorkReduction(process.getLevelOfRoutineWorkReduction());
		clone.setLevelOfStructuredInformation(process.getLevelOfStructuredInformation());
		clone.setLevelOfUniformity(process.getLevelOfUniformity());
		clone.setLocalId(process.getLocalId());
		clone.setLongDescription(process.getLongDescription());
		clone.setOrganizationalImplementationNotes(process.getOrganizationalImplementationNotes());
		clone.setPhase(process.getPhase());
		clone.setProcessChallenges(process.getProcessChallenges());
		clone.setRating(process.getRating());
		clone.setRatingComment(process.getRatingComment());
		clone.setShortDescription(process.getShortDescription());
		clone.setSolutionRequests(process.getSolutionRequests());
		clone.setStatus(process.getStatus());
		clone.setTargetsCitizens(process.isTargetsCitizens());
		clone.setTargetsCompanies(process.isTargetsCompanies());
		clone.setTechnicalImplementationNotes(process.getTechnicalImplementationNotes());
		clone.setTechnologies(new ArrayList<>());
		if (process.getTechnologies() != null && process.getTechnologies().size() > 0) {
			clone.getTechnologies().addAll(process.getTechnologies());
		}
		clone.setTimeSpendComment(process.getTimeSpendComment());
		clone.setTimeSpendComputedTotal(process.getTimeSpendComputedTotal());
		clone.setTimeSpendPercentageDigital(process.getTimeSpendPercentageDigital());
		clone.setTimeSpendEmployeesDoingProcess(process.getTimeSpendEmployeesDoingProcess());
		clone.setTimeSpendOccurancesPerEmployee(process.getTimeSpendOccurancesPerEmployee());
		clone.setTimeSpendPerOccurance(process.getTimeSpendPerOccurance());
		clone.setTitle(process.getTitle());
		clone.setVendor(process.getVendor());
		clone.setSearchWords(process.getSearchWords());

		// domains have to be copied, as they are a collectionset, and hibernate does not like sharing ;)
		if (process.getDomains() != null) {
			clone.setDomains(new ArrayList<>());
			for (Domain domain : process.getDomains()) {
				clone.getDomains().add(domain);
			}
		}
		
		// values that are not copied
		clone.setVisibility(Visibility.PERSONAL);
		clone.setOwner(SecurityUtil.getUser());
		clone.setReporter(SecurityUtil.getUser());
		clone.setContact(SecurityUtil.getUser());
		clone.setCvr(SecurityUtil.getCvr());
		clone.setLinks(new ArrayList<>());
		clone.setItSystems(new ArrayList<>());
		clone.setStatusText(null);

		clone = processService.save(clone);

		return ResponseEntity.ok(clone);
	}
}