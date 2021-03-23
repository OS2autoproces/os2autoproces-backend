package dk.digitalidentity.ap.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.ap.dao.AdministratorDao;
import dk.digitalidentity.ap.dao.model.Administrator;

@Service
public class AdministratorService {

	@Autowired
	private AdministratorDao dao;

	public List<Administrator> getByUserUuid(String userUuid) {
		return dao.getByUserUuid(userUuid);
	}
}
