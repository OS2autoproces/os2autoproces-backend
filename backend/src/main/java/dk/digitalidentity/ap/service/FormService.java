package dk.digitalidentity.ap.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.ap.dao.FormDao;
import dk.digitalidentity.ap.dao.model.Form;

@Service
public class FormService {

	@Autowired
	private FormDao formDao;

	public Form save(Form form) {
		return formDao.save(form);
	}

	public Form getByCode(String code) {
		return formDao.getByCode(code);
	}

	public List<Form> findAll() {
		return formDao.findAll();
	}

	public long count() {
		return formDao.count();
	}
}
