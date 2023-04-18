package dk.digitalidentity.ap.dao.model.processor;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.dao.model.Form;

@Component
public class FormProcessor implements RepresentationModelProcessor<EntityModel<Form>> {

	@Override
	public EntityModel<Form> process(EntityModel<Form> model) {

		// strip links
		return EntityModel.of(model.getContent());
	}
}
