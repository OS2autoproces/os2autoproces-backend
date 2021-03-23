package dk.digitalidentity.ap.interceptor;

import javax.persistence.EntityManager;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.enums.ProcessType;
import dk.digitalidentity.ap.dao.model.enums.Visibility;
import dk.digitalidentity.ap.security.SecurityUtil;
import dk.digitalidentity.ap.service.MunicipalityService;
import dk.digitalidentity.ap.service.ProcessService;
import lombok.extern.log4j.Log4j;

@Aspect
@Component
@Log4j
public class ProcessSaveInterceptor {
	private static final String errorMessage = "You do not have permission to modify this process!";

	@Autowired
	private ProcessService processService;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private MunicipalityService municipalityService;

	@Around("execution(* dk.digitalidentity.ap.dao.ProcessDao.delete*(..))")
	public void aroundDeleteProcess(ProceedingJoinPoint joinPoint) throws Throwable {
		if (joinPoint.getArgs().length > 0) {
			Object target = joinPoint.getArgs()[0];

			Process process = null;
			if (target != null && target instanceof Long) {
				process = processService.findOne((Long) target);
			}
			else if (target != null && target instanceof Process) {
				process = (Process) target;
			}

			if (process == null || !SecurityUtil.canEdit(process)) {
				throw new AccessDeniedException(errorMessage);
			}
		}
		
		joinPoint.proceed();
	}

	@Around("execution(* dk.digitalidentity.ap.dao.ProcessDao.save*(..))")
	public Object aroundSaveProcess(ProceedingJoinPoint joinPoint) throws Throwable {
		if (joinPoint.getArgs().length > 0) {
			Object target = joinPoint.getArgs()[0];

			// it should always be a process, but let's be safe ;)
			if (target != null && target instanceof Process) {
				Process newProcess = (Process) target;
				entityManager.detach(newProcess);

				// do a security check on modify, and set default values on create
				if (newProcess.getId() != null && newProcess.getId() > 0) {
					Process oldProcess = processService.findOne(newProcess.getId());

					// check for read/write access to the object
					if (oldProcess == null || !SecurityUtil.canEdit(oldProcess)) {
						throw new AccessDeniedException(errorMessage);
					}
					
					if (newProcess.getReporter() == null || newProcess.getReporter().getId() != oldProcess.getReporter().getId()) {
						log.warn("Attempting to change reporter on: " + oldProcess.getId() + ". Preventing the attempt!");
						newProcess.setReporter(oldProcess.getReporter());
					}
				}
				else {
					// on new process we assigned current users cvr
					newProcess.setCvr(SecurityUtil.getCvr());
					newProcess.setReporter(SecurityUtil.getUser());
					newProcess.setMunicipalityName(municipalityService.getByCvr(SecurityUtil.getCvr()).getName());
					
					if (newProcess.getType() == null) {
						newProcess.setType(ProcessType.CHILD);
					}
				}

				// default value if not supplied
				if (newProcess.getOwner() == null) {
					newProcess.setOwner(SecurityUtil.getUser());
				}
				
				// default value if not supplied
				if (newProcess.getContact() == null) {
					newProcess.setContact(SecurityUtil.getUser());
				}
				
				// make sure to update the klaProcess boolean
				if (newProcess.getKla() != null && newProcess.getKla().length() > 0) {
					newProcess.setKlaProcess(true);
				}
				else {
					newProcess.setKlaProcess(false);
				}
				
				// compute time spend
				int timeSpend = newProcess.getTimeSpendPerOccurance();
				int numberOfEmployees = newProcess.getTimeSpendEmployeesDoingProcess();
				int numberOfOccurances = newProcess.getTimeSpendOccurancesPerEmployee();
				int percentage = newProcess.getTimeSpendPercentageDigital();
				int computedValueInHours = (timeSpend * numberOfEmployees * numberOfOccurances * percentage / 60) / 100;
				newProcess.setTimeSpendComputedTotal(computedValueInHours);
				
				// update children to minimum visibility
				if (newProcess.getChildren() != null && newProcess.getChildren().size() > 0) {
					for (Process child : newProcess.getChildren()) {
						
						// if a process cannot be read by the user, it is null'ed out
						if (child == null) {
							continue;
						}

						switch (newProcess.getVisibility()) {
							case PERSONAL:
								// should not really happen, as a parent process is never personal
								break;
							case MUNICIPALITY:
								if (child.getVisibility().equals(Visibility.PERSONAL)) {
									processService.setProcessVisibility(child, Visibility.MUNICIPALITY);
								}
								break;
							case PUBLIC:
								if (!child.getVisibility().equals(Visibility.PUBLIC)) {
									processService.setProcessVisibility(child, Visibility.PUBLIC);
								}
								break;
						}
 					}
				}
			}
		}

		return joinPoint.proceed();
	}
}