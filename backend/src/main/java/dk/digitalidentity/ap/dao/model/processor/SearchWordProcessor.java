package dk.digitalidentity.ap.dao.model.processor;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.dao.model.SearchWord;

@Component
public class SearchWordProcessor implements ResourceProcessor<Resource<SearchWord>> {

	@Override
	public Resource<SearchWord> process(Resource<SearchWord> resource) {

		// strip links
		return new Resource<>(resource.getContent());
	}
}
