package dk.digitalidentity.ap.dao.model.processor;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.dao.model.SearchWord;

@Component
public class SearchWordProcessor implements RepresentationModelProcessor<EntityModel<SearchWord>> {

	@Override
	public EntityModel<SearchWord> process(EntityModel<SearchWord> model) {

		// strip links
		return EntityModel.of(model.getContent());
	}
}
