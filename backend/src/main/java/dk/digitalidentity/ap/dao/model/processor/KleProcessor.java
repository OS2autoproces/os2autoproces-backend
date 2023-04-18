package dk.digitalidentity.ap.dao.model.processor;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.dao.model.Kle;

@Component
public class KleProcessor implements RepresentationModelProcessor<EntityModel<Kle>> {

	@Override
	public EntityModel<Kle> process(EntityModel<Kle> model) {

		// strip links
		return EntityModel.of(model.getContent());
	}
}
