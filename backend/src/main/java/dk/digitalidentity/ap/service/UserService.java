package dk.digitalidentity.ap.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.ap.dao.UserDao;
import dk.digitalidentity.ap.dao.model.User;

@Service
public class UserService {

	@Autowired
	private UserDao userDao;

	public List<User> save(List<User> list) {
		return userDao.save(list);
	}
	
	public User save(User user) {
		return userDao.save(user);
	}
	
	public User getByUuidAndCvr(String uuid, String cvr) {
		return userDao.getByUuidAndCvrAndActiveTrue(uuid, cvr);
	}
	
	public List<User> getByUuidAndCvrIncludingInactive(String uuid, String cvr) {
		return userDao.getByUuidAndCvr(uuid, cvr);
	}

	public List<User> getByCvrIncludingInactive(String cvr) {
		return userDao.getByCvr(cvr);
	}
}
