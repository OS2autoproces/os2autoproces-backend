package dk.digitalidentity.ap.dao;

import java.util.List;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.querydsl.core.types.dsl.StringPath;

import dk.digitalidentity.ap.dao.model.OrgUnit;
import dk.digitalidentity.ap.dao.model.QOrgUnit;

@SecurityRequirement(name = "Authorization")
@CrossOrigin(exposedHeaders = "x-csrf-token")
public interface OrgUnitDao extends JpaRepository<OrgUnit, Long>, QuerydslPredicateExecutor<OrgUnit>, QuerydslBinderCustomizer<QOrgUnit> {
	
	@RestResource(exported = false)
	void delete(OrgUnit entity);

	@RestResource(exported = false)
	void deleteById(Long id);

	@RestResource(exported = false)
	void deleteAll();

	@RestResource(exported = false)
	<S extends OrgUnit> List<S> saveAll(Iterable<S> entities);

	@RestResource(exported = false)
	<S extends OrgUnit> S save(S entity);

	@RestResource(exported = false)
	<S extends OrgUnit> S saveAndFlush(S entity);
	
	// methods for internal use

	@RestResource(exported = false)
	OrgUnit getByUuidAndCvrAndActiveTrue(String uuid, String cvr);

	@RestResource(exported = false)
	List<OrgUnit> getByCvr(String cvr);

	@Override
	default void customize(QuerydslBindings bindings, QOrgUnit orgUnit) {

		// enable case-insensitive searching
		bindings.bind(String.class).first((StringPath path, String value) -> path.containsIgnoreCase(value));
		
		// infix search on name
		bindings.bind(orgUnit.name).first((path, value) -> path.contains(value));
	}
}
