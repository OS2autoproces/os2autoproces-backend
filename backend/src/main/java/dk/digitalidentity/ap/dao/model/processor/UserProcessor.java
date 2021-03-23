package dk.digitalidentity.ap.dao.model.processor;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.dao.model.User;

@Component
public class UserProcessor implements ResourceProcessor<Resource<User>> {

	@Override
	public Resource<User> process(Resource<User> resource) {
		// strip links
		return new Resource<>(resource.getContent());
	}
}
