package dk.digitalidentity.ap.dao.model.processor;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.dao.model.OrgUnit;

@Component
public class OrgUnitProcessor implements ResourceProcessor<Resource<OrgUnit>> {

	@Override
	public Resource<OrgUnit> process(Resource<OrgUnit> resource) {
		// strip links
		return new Resource<>(resource.getContent());
	}
}
