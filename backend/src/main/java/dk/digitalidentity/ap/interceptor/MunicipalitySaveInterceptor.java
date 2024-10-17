package dk.digitalidentity.ap.interceptor;

import dk.digitalidentity.ap.dao.model.Municipality;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.security.SecurityUtil;
import dk.digitalidentity.ap.service.MunicipalityService;
import dk.digitalidentity.ap.service.ProcessService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

@Aspect
public class MunicipalitySaveInterceptor {
	private static final String errorMessage = "You do not have permission to modify this municipality!";

	@Autowired
	private MunicipalityService municipalityService;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private ProcessService processService;

	@Around("execution(* dk.digitalidentity.ap.dao.MunicipalityDao.save*(..))")
	public Object aroundSaveMunicipality(ProceedingJoinPoint joinPoint) throws Throwable {
		if (joinPoint.getArgs().length > 0) {
			Object target = joinPoint.getArgs()[0];

			// it should always be a municipality, but let's be safe ;)
			if (target != null && target instanceof Municipality) {
				Municipality newMunicipality = (Municipality) target;
				entityManager.detach(newMunicipality);

				// do a security check on modify, and check if autoOtherContactEmail has change and update processes
				if (newMunicipality.getId() > 0) {
					Municipality oldMunicipality = municipalityService.findOne(newMunicipality.getId());

					// check for read/write access to the object
					if (oldMunicipality == null || !SecurityUtil.canEditMunicipalityInfo(oldMunicipality)) {
						throw new AccessDeniedException(errorMessage);
					}

					// if autoOtherContactEmail has changed, update processes
					if (StringUtils.hasLength(newMunicipality.getAutoOtherContactEmail()) && !Objects.equals(newMunicipality.getAutoOtherContactEmail(), oldMunicipality.getAutoOtherContactEmail())) {
						List<Process> processes = processService.findByCvr(oldMunicipality.getCvr());
						for (Process process : processes) {
							process.setOtherContactEmail(newMunicipality.getAutoOtherContactEmail());
						}
						processService.saveAll(processes);
					}
				}
			}
		}

		return joinPoint.proceed();
	}
}