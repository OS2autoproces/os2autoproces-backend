package dk.digitalidentity.ap.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.querydsl.core.types.dsl.StringPath;

import dk.digitalidentity.ap.dao.model.QSearchWord;
import dk.digitalidentity.ap.dao.model.SearchWord;

@CrossOrigin(exposedHeaders = "x-csrf-token")
public interface SearchWordDao extends JpaRepository<SearchWord, Long>, QueryDslPredicateExecutor<SearchWord>, QuerydslBinderCustomizer<QSearchWord> {

	@RestResource(exported = false)
	void delete(Iterable<? extends SearchWord> entities);

	@RestResource(exported = false)
	void delete(SearchWord entity);

	@RestResource(exported = false)
	void delete(Long id);

	@RestResource(exported = false)
	void deleteAll();

	@RestResource(exported = false)
	<S extends SearchWord> List<S> save(Iterable<S> entities);

	@RestResource(exported = false)
	<S extends SearchWord> S save(S entity);

	@RestResource(exported = false)
	<S extends SearchWord> S saveAndFlush(S entity);

	@Override
	default void customize(QuerydslBindings bindings, QSearchWord searchWord) {

		// enable case-insensitive searching
		bindings.bind(String.class).first((StringPath path, String value) -> path.containsIgnoreCase(value));
		
		// infix search on searchWord
		bindings.bind(searchWord.searchWord).first((path, value) -> path.contains(value));
	}
}
