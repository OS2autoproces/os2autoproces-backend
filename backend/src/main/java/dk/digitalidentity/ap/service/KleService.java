package dk.digitalidentity.ap.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.ap.dao.KleDao;
import dk.digitalidentity.ap.dao.model.Kle;

@Service
public class KleService {

	@Autowired
	private KleDao kleDao;
	
	public long count() {
		return kleDao.count();
	}

	public List<Kle> findAll() {
		return kleDao.findAll();
	}

	public Kle getByCode(String code) {
		return kleDao.getByCode(code);
	}

	public Kle save(Kle kle) {
		return kleDao.save(kle);
	}
}
