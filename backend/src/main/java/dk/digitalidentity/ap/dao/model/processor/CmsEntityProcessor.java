package dk.digitalidentity.ap.dao.model.processor;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.dao.model.CmsEntry;

@Component
public class CmsEntityProcessor implements ResourceProcessor<Resource<CmsEntry>> {

	@Override
	public Resource<CmsEntry> process(Resource<CmsEntry> resource) {
		// strip links
		return new Resource<>(resource.getContent());
	}
}
