package dk.digitalidentity.ap.interceptor;

import javax.persistence.EntityManager;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import dk.digitalidentity.ap.dao.model.Form;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.User;
import dk.digitalidentity.ap.dao.model.enums.ProcessType;
import dk.digitalidentity.ap.dao.model.enums.Visibility;
import dk.digitalidentity.ap.security.AuthenticatedUser;
import dk.digitalidentity.ap.security.SecurityUtil;
import dk.digitalidentity.ap.service.FormService;
import dk.digitalidentity.ap.service.MunicipalityService;
import dk.digitalidentity.ap.service.ProcessService;
import dk.digitalidentity.ap.service.UserService;

@Aspect
@Component
public class ProcessSaveInterceptor {
	private static final String errorMessage = "You do not have permission to modify this process!";

	@Autowired
	private ProcessService processService;

	@Autowired
	private UserService userService;

	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private FormService formService;

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
				}
				else {
					AuthenticatedUser authenticatedUser = SecurityUtil.getUser();
					User user = userService.getByUuidAndCvr(authenticatedUser.getUuid(), authenticatedUser.getCvr());

					// on new process we assigned current users cvr
					newProcess.setCvr(SecurityUtil.getCvr());
					newProcess.setReporter(user);
					newProcess.setMunicipalityName(municipalityService.getByCvr(SecurityUtil.getCvr()).getName());

					// default value if not supplied
					if (newProcess.getOwner() == null) {
						newProcess.setOwner(user);
					}
					
					// default value if not supplied
					if (newProcess.getContact() == null) {
						newProcess.setContact(user);
					}

					if (newProcess.getType() == null) {
						newProcess.setType(ProcessType.CHILD);
					}
				}
				
				// make sure to update the klaProcess boolean
				if (newProcess.getKla() != null && newProcess.getKla().length() > 0) {
					newProcess.setKlaProcess(true);
				}
				else {
					newProcess.setKlaProcess(false);
				}
				
				// compute time spend
				double timeSpend = newProcess.getTimeSpendPerOccurance();
				int numberOfOccurances = newProcess.getTimeSpendOccurancesPerEmployee();
				int percentage = newProcess.getTimeSpendPercentageDigital();
				double computedValueInHours = (timeSpend * numberOfOccurances * percentage / 60) / 100;
				newProcess.setTimeSpendComputedTotal((int) computedValueInHours);
				
				// if FORM is set, overwrite legal_clause with the official version
				if (!StringUtils.isEmpty(newProcess.getForm())) {
					Form form = formService.getByCode(newProcess.getForm());

					if (form != null && !StringUtils.isEmpty(form.getLegalClause())) {
						newProcess.setLegalClause(form.getLegalClause());
					}
				}

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
					
					// for easy sorting
					newProcess.setChildrenCount(newProcess.getChildren().size());
				}
			}
		}

		return joinPoint.proceed();
	}
}