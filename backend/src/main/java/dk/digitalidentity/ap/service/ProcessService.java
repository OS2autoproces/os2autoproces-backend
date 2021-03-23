package dk.digitalidentity.ap.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.ap.dao.ProcessDao;
import dk.digitalidentity.ap.dao.model.Form;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.enums.Visibility;
import dk.digitalidentity.ap.security.SecurityUtil;

@Service
public class ProcessService {
	
	@Autowired
	private ProcessDao processDao;

	public Process findOne(long id) {
		return processDao.findOne(id);
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
					process.getChildren().size(); // flex relationship, to avoid dead session
					process.setLegalClause(form.getLegalClause());

					processDao.save(process);
				}
			}
		}
		finally {
			SecurityUtil.logoutSystem();
		}
	}
}
