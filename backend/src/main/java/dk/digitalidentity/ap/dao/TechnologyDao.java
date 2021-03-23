package dk.digitalidentity.ap.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.web.bind.annotation.CrossOrigin;

import dk.digitalidentity.ap.dao.model.QTechnology;
import dk.digitalidentity.ap.dao.model.Technology;
import dk.digitalidentity.ap.security.RequireAdminRole;

@CrossOrigin(exposedHeaders = "x-csrf-token")
public interface TechnologyDao extends JpaRepository<Technology, Long>, QueryDslPredicateExecutor<Technology>, QuerydslBinderCustomizer<QTechnology> {

	@RequireAdminRole
	void delete(Iterable<? extends Technology> entities);

	@RequireAdminRole
	void delete(Technology entity);

	@RequireAdminRole
	void delete(Long id);

	@RequireAdminRole
	void deleteAll();

	@RequireAdminRole
	<S extends Technology> List<S> save(Iterable<S> entities);

	@RequireAdminRole
	<S extends Technology> S save(S entity);

	@RequireAdminRole
	<S extends Technology> S saveAndFlush(S entity);

	@Override
	default void customize(QuerydslBindings bindings, QTechnology techology) {

		// allow for in-fix searching
		bindings.bind(techology.name).first((path, value) -> path.containsIgnoreCase(value));
	}
}