package dk.digitalidentity.ap.interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.enums.ProcessType;
import dk.digitalidentity.ap.security.SecurityUtil;

@Aspect
@Component
public class ProcessReadInterceptor {

	@Autowired
	private EntityManager entityManager;

	@AfterReturning(pointcut = "execution(* dk.digitalidentity.ap.dao.ProcessDao.get*(..))", returning = "target")
	public void afterGet(Object target) throws Throwable {
		processTarget(target);
	}

	@AfterReturning(pointcut = "execution(* dk.digitalidentity.ap.dao.ProcessDao.find*(..))", returning = "target")
	public void afterFind(Object target) throws Throwable {
		processTarget(target);
	}

	@SuppressWarnings("unchecked")
	private void processTarget(Object target) throws Exception {
		if (target == null) {
			return;
		}

		if (target instanceof Process) {
			Process process = (Process) target;
			filter(process);
		}
		else if (target instanceof PageImpl<?>) {
			Page<Object> page = (Page<Object>) target;
			List<Object> targetList = page.getContent();

			if (!targetList.isEmpty()) {
				// Get first element of list to determine type
				if (targetList.get(0) instanceof Process) {
					List<Process> processes = targetList.stream().map(o -> (Process) o).collect(Collectors.toList());

					for (Process process : processes) {
						filter(process);
					}
				}
			}
		}
		else if (target instanceof List) {
			List<Object> targetList = (List<Object>) target;

			if (!targetList.isEmpty()) {
				// Get first element of list to determine type
				if (targetList.get(0) instanceof Process) {
					List<Process> processes = targetList.stream().map(o -> (Process) o).collect(Collectors.toList());

					for (Process process : processes) {
						filter(process);
					}
				}
			}
		}
	}

	private void filter(Process process) {
		if (process == null) {
			return;
		}

		if (SecurityUtil.isLoggedInAsSystemAccount()) {
			return;
		}
		
		// when linking to foreign processes (owned by other municipalities),
		// those processes are read, and then linked to - if we make modifications,
		// those are flushed when the transaction completes, and we do not want that
		// on filtered processes....
		boolean detached = false;

		if (!SecurityUtil.canEdit(process)) {
			detach(process);
			detached = true;
			process.setInternalNotes(null);
		}

		// parent processes might contain children from other municipalities
		if (process.getChildren() != null && process.getChildren().size() > 0) {

			// filtering will likely always happen here, so we detach parents always
			if (!detached) {
				detach(process);
				detached = true;
			}

			for (Process child : process.getChildren()) {
				filter(child);
			}
		}

		// no more filtering on same cvr access (or global parent processes)
		if (process.getCvr().equals(SecurityUtil.getCvr()) || process.getType().equals(ProcessType.GLOBAL_PARENT)) {
			return;
		}
		
		if (!detached) {
			detach(process);
		}

		// blank out fields that are internal only
		process.setOrgUnits(new ArrayList<>());
		process.setOwner(null);
		process.setUsers(new ArrayList<>());
		process.setReporter(null);
		process.setEsdhReference(null);
	}
	
	private void detach(Process process) {
		// visit all collections and relationships before detaching
		process.getOrgUnits().size();
		process.getReporter();
		process.getUsers().size();
		process.getTechnologies().size();
		process.getOwner();
		process.getChildren().size();
		process.getParents().size();
		process.getLinks().size();
		process.getDomains().size();
		process.getItSystems().size();

		entityManager.detach(process);
	}
}
