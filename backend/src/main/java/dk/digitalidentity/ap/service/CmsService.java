package dk.digitalidentity.ap.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.ap.dao.CmsEntryDao;
import dk.digitalidentity.ap.dao.model.CmsEntry;

@Service
public class CmsService {

	@Autowired
	private CmsEntryDao cmsEntryDao;
	
	public CmsEntry getByLabel(String label) {
		return cmsEntryDao.getByLabel(label);
	}
	
	public List<CmsEntry> findAll() {
		return cmsEntryDao.findAll();
	}

	public void save(CmsEntry cmsEntry) {
		cmsEntryDao.save(cmsEntry);		
	}

	public void delete(CmsEntry cmsEntry) {
		cmsEntryDao.delete(cmsEntry);
	}
}
