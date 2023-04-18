package dk.digitalidentity.ap.dao.model.processor;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.dao.model.User;

@Component
public class UserProcessor implements RepresentationModelProcessor<EntityModel<User>> {

	@Override
	public EntityModel<User> process(EntityModel<User> model) {

		// strip links
		return EntityModel.of(model.getContent());
	}
}
