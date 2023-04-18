package dk.digitalidentity.ap.dao.model.processor;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.dao.model.CmsEntry;

@Component
public class CmsEntityProcessor implements RepresentationModelProcessor<EntityModel<CmsEntry>> {

	@Override
	public EntityModel<CmsEntry> process(EntityModel<CmsEntry> model) {

		// strip links
		return EntityModel.of(model.getContent());
	}

}
