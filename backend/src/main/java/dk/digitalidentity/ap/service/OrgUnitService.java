package dk.digitalidentity.ap.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.ap.dao.OrgUnitDao;
import dk.digitalidentity.ap.dao.model.OrgUnit;

@Service
public class OrgUnitService {

	@Autowired
	private OrgUnitDao orgUnitDao;

	public OrgUnit save(OrgUnit orgUnit) {
		return orgUnitDao.save(orgUnit);
	}

	public OrgUnit getByUuidAndCvr(String uuid, String cvr) {
		return orgUnitDao.getByUuidAndCvrAndActiveTrue(uuid, cvr);
	}

	public List<OrgUnit> getByCvrIncludingInactive(String cvr) {
		return orgUnitDao.getByCvr(cvr);
	}
}
