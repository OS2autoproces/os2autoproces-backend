package dk.digitalidentity.ap.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import dk.digitalidentity.ap.dao.model.IdentityProvider;

@RepositoryRestResource(exported = false)
public interface IdentityProviderDao extends JpaRepository<IdentityProvider, Long> {
	List<IdentityProvider> findAll();
	IdentityProvider getByEntityId(String entityId);
	IdentityProvider getByCvr(String cvr);
}
