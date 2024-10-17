package dk.digitalidentity.ap.service;

import java.util.List;

import dk.digitalidentity.ap.dao.model.Process;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.ap.dao.MunicipalityDao;
import dk.digitalidentity.ap.dao.model.Municipality;

@Service
public class MunicipalityService {

	@Autowired
	private MunicipalityDao municipalityDao;

	public Municipality getByCvr(String cvr) {
		return municipalityDao.getByCvr(cvr);
	}

	public List<Municipality> findAll() {
		return municipalityDao.findAll();
	}

	public void save(Municipality municipality) {
		municipalityDao.save(municipality);
	}
	public Municipality findOne(long id) {
		return municipalityDao.findById(id).orElse(null);
	}
}
