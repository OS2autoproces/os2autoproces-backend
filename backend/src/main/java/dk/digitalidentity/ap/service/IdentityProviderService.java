package dk.digitalidentity.ap.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.ap.dao.IdentityProviderDao;
import dk.digitalidentity.ap.dao.model.IdentityProvider;

@Service
public class IdentityProviderService {

	@Autowired
	private IdentityProviderDao identityProviderDao;

	public List<IdentityProvider> getAll() {
		return identityProviderDao.findAll();
	}

	public IdentityProvider getByEntityId(String entityId) {
		return identityProviderDao.getByEntityId(entityId);
	}
}
