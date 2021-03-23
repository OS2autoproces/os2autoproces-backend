package dk.digitalidentity.ap.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import dk.digitalidentity.ap.dao.model.ItSystem;
import dk.digitalidentity.ap.dao.model.QItSystem;

@CrossOrigin(exposedHeaders = "x-csrf-token")
public interface ItSystemDao extends JpaRepository<ItSystem, Long>, QueryDslPredicateExecutor<ItSystem>, QuerydslBinderCustomizer<QItSystem> {

	@RestResource(exported = false)
	void delete(Iterable<? extends ItSystem> entities);

	@RestResource(exported = false)
	void delete(ItSystem entity);

	@RestResource(exported = false)
	void delete(Long id);

	@RestResource(exported = false)
	void deleteAll();

	@RestResource(exported = false)
	<S extends ItSystem> List<S> save(Iterable<S> entities);

	@RestResource(exported = false)
	<S extends ItSystem> S save(S entity);

	@RestResource(exported = false)
	<S extends ItSystem> S saveAndFlush(S entity);
	
	// exported to API
	List<ItSystem> findAll();

	@Override
	default void customize(QuerydslBindings bindings, QItSystem itSystem) {

		// allow for in-fix searching on name
		bindings.bind(itSystem.name).first((path, value) -> path.containsIgnoreCase(value));
	}
}
