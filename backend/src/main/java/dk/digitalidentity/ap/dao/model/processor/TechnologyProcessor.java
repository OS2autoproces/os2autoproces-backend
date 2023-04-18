package dk.digitalidentity.ap.dao.model.processor;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.dao.model.Technology;

@Component
public class TechnologyProcessor implements RepresentationModelProcessor<EntityModel<Technology>> {

	@Override
	public EntityModel<Technology> process(EntityModel<Technology> model) {

		// strip links
		return EntityModel.of(model.getContent());
	}
}
