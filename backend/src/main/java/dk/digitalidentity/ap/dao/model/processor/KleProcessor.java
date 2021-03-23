package dk.digitalidentity.ap.dao.model.processor;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.dao.model.Kle;

@Component
public class KleProcessor implements ResourceProcessor<Resource<Kle>> {

	@Override
	public Resource<Kle> process(Resource<Kle> resource) {

		// strip links
		return new Resource<>(resource.getContent());
	}
}
