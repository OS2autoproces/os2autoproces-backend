package dk.digitalidentity.ap.dao;

import java.util.List;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.web.bind.annotation.CrossOrigin;

import dk.digitalidentity.ap.dao.model.QTechnology;
import dk.digitalidentity.ap.dao.model.Technology;
import dk.digitalidentity.ap.security.RequireAdminRole;

@SecurityRequirement(name = "Authorization")
@CrossOrigin(exposedHeaders = "x-csrf-token")
public interface TechnologyDao extends JpaRepository<Technology, Long>, QuerydslPredicateExecutor<Technology>, QuerydslBinderCustomizer<QTechnology> {

	@RequireAdminRole
	void delete(Technology entity);

	@RequireAdminRole
	void deleteById(Long id);

	@RequireAdminRole
	void deleteAll();

	@RequireAdminRole
	<S extends Technology> List<S> saveAll(Iterable<S> entities);

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