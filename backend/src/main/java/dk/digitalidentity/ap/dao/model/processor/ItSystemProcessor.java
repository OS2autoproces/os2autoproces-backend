package dk.digitalidentity.ap.dao.model.processor;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.dao.model.ItSystem;

@Component
public class ItSystemProcessor implements RepresentationModelProcessor<EntityModel<ItSystem>> {

	@Override
	public EntityModel<ItSystem> process(EntityModel<ItSystem> model) {

		// strip links
		return EntityModel.of(model.getContent());
	}
}
