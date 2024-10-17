package dk.digitalidentity.ap.service;

import dk.digitalidentity.ap.dao.LogoDao;
import dk.digitalidentity.ap.dao.model.Logo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogoService {

	@Autowired
	private LogoDao logoDao;

	public Logo save(Logo logo) {
		return logoDao.save(logo);
	}

	public void delete(Logo logo) {
		logoDao.delete(logo);
	}
}
