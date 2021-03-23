package dk.digitalidentity.ap.dao.model.processor;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.dao.model.ItSystem;

@Component
public class ItSystemProcessor implements ResourceProcessor<Resource<ItSystem>> {

	@Override
	public Resource<ItSystem> process(Resource<ItSystem> resource) {

		// strip links
		return new Resource<>(resource.getContent());
	}
}
