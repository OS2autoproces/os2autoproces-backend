package dk.digitalidentity.ap.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.ap.dao.ItSystemDao;
import dk.digitalidentity.ap.dao.model.ItSystem;

@Service
public class ItSystemService {

	@Autowired
	private ItSystemDao itSystemDao;

	public ItSystem save(ItSystem entity) {
		return itSystemDao.save(entity);
	}

	public List<ItSystem> findAll() {
		return itSystemDao.findAll();
	}
	
	public long count() {
		return itSystemDao.count();
	}
}
