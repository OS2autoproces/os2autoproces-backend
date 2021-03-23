package dk.digitalidentity.ap.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.service.IdentityProviderService;
import dk.digitalidentity.saml.extension.SamlIdentityProviderProvider;
import dk.digitalidentity.saml.model.IdentityProvider;

@Component
public class IdentityProviderProvider implements SamlIdentityProviderProvider {

	@Autowired
	private IdentityProviderService identityProviderService;

	@Override
	public List<IdentityProvider> getIdentityProviders() {
		List<IdentityProvider> identityProviders = new ArrayList<>();
		
		for (dk.digitalidentity.ap.dao.model.IdentityProvider identityProvider : identityProviderService.getAll()) {
			IdentityProvider idp = IdentityProvider.builder()
												   .entityId(identityProvider.getEntityId())
												   .metadata(identityProvider.getMetadata())
												   .cvr(identityProvider.getCvr())
												   .build();
			
			identityProviders.add(idp);
		}
		
		return identityProviders;
	}

	@Override
	public IdentityProvider getByEntityId(String entityId) {
		dk.digitalidentity.ap.dao.model.IdentityProvider identityProvider = identityProviderService.getByEntityId(entityId);
		if (identityProvider != null) {
			return IdentityProvider.builder()
								   .entityId(identityProvider.getEntityId())
								   .metadata(identityProvider.getMetadata())
								   .cvr(identityProvider.getCvr())
								   .build();
		}

		return null;
	}
}
