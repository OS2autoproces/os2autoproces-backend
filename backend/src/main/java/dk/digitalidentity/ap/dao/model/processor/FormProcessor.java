package dk.digitalidentity.ap.dao.model.processor;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.dao.model.Form;

@Component
public class FormProcessor implements ResourceProcessor<Resource<Form>> {

	@Override
	public Resource<Form> process(Resource<Form> resource) {

		// strip links
		return new Resource<>(resource.getContent());
	}
}
