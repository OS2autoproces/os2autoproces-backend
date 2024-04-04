package dk.digitalidentity.ap.interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.enums.ProcessType;
import dk.digitalidentity.ap.security.SecurityUtil;

@Aspect
@Component
public class ProcessReadInterceptor {

	@Around(value = "execution(* dk.digitalidentity.ap.dao.ProcessDao.get*(..))")
	public Object aroundGet(ProceedingJoinPoint jp) throws Throwable {
        Object target = jp.proceed();
        
        target = processTarget(target);
        
        return target;
	}

	@Around(value = "execution(* dk.digitalidentity.ap.dao.ProcessDao.find*(..))")
	public Object aroundFind(ProceedingJoinPoint jp) throws Throwable {
        Object target = jp.proceed();
        
        target = processTarget(target);
        
        return target;
	}

	@SuppressWarnings("unchecked")
	private Object processTarget(Object target) throws Exception {
		if (target == null) {
			return target;
		}

		if (target instanceof Process) {
			Process process = (Process) target;

			return filter(process);
		}
		else if (target instanceof PageImpl<?>) {
			Page<Object> page = (Page<Object>) target;
			List<Object> targetList = page.getContent();

			if (!targetList.isEmpty()) {
				// Get first element of list to determine type
				if (targetList.get(0) instanceof Process) {
					List<Process> newProcesses = new ArrayList<>();
					List<Process> processes = targetList.stream().map(o -> (Process) o).collect(Collectors.toList());

					for (Process process : processes) {
						newProcesses.add(filter(process));
					}

					return new PageImpl<>(newProcesses, page.getPageable(), page.getTotalElements());
				}
			}
		}
		else if (target instanceof List) {
			List<Object> targetList = (List<Object>) target;

			if (!targetList.isEmpty()) {
				// Get first element of list to determine type
				if (targetList.get(0) instanceof Process) {
					List<Process> newProcesses = new ArrayList<>();
					List<Process> processes = targetList.stream().map(o -> (Process) o).collect(Collectors.toList());

					for (Process process : processes) {
						newProcesses.add(filter(process));
					}
				}
			}
		}
		
		// no filtering
		return target;
	}

	private Process filter(Process process) {
		if (process == null) {
			return process;
		}

		if (SecurityUtil.isLoggedInAsSystemAccount()) {
			return process;
		}
		
		// when linking to foreign processes (owned by other municipalities),
		// those processes are read, and then linked to - if we make modifications,
		// those are flushed when the transaction completes, and we do not want that
		// on filtered processes....
		boolean cloned = false;

		if (!SecurityUtil.canEdit(process)) {
			if (!cloned) {
				process = process.cloneMe();
				cloned = true;
			}

			process.setInternalNotes(null);
		}

		// no more filtering on same cvr access (or global parent processes)
		if (process.getCvr().equals(SecurityUtil.getCvr()) || process.getType().equals(ProcessType.GLOBAL_PARENT)) {
			return process;
		}

		// from here on out we should only work on a clone
		if (!cloned) {
			process = process.cloneMe();
			cloned = true;
		}

		// blank out fields that are internal only
		process.setOrgUnits(new ArrayList<>());
		process.setOwner(null);
		process.setUsers(new ArrayList<>());
		process.setReporter(null);
		process.setEsdhReference(null);
		
		return process;
	}
}
