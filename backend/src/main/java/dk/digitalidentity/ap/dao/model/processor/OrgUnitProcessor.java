package dk.digitalidentity.ap.dao.model.processor;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.dao.model.OrgUnit;

@Component
public class OrgUnitProcessor implements RepresentationModelProcessor<EntityModel<OrgUnit>> {

	@Override
	public EntityModel<OrgUnit> process(EntityModel<OrgUnit> model) {

		// strip links
		return EntityModel.of(model.getContent());
	}
}
