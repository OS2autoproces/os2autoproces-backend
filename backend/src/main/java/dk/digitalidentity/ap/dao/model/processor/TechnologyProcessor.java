package dk.digitalidentity.ap.dao.model.processor;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.dao.model.Technology;

@Component
public class TechnologyProcessor implements ResourceProcessor<Resource<Technology>> {

	@Override
	public Resource<Technology> process(Resource<Technology> resource) {

		// strip links
		return new Resource<>(resource.getContent());
	}
}
