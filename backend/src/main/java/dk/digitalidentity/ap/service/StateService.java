package dk.digitalidentity.ap.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.ap.dao.StateDao;
import dk.digitalidentity.ap.dao.model.State;

@Service
public class StateService {

	@Autowired
	private StateDao stateDao;
	
	public State getByKey(String key) {
		return stateDao.getByKey(key);
	}
	
	public Date getCurrentTimestamp() {
		return stateDao.getCurrentTimestamp();
	}
	
	public State save(State state) {
		return stateDao.save(state);
	}
}
