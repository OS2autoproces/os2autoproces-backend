package dk.digitalidentity.ap.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import dk.digitalidentity.ap.dao.model.Kle;
import dk.digitalidentity.ap.dao.model.QKle;

@CrossOrigin(exposedHeaders = "x-csrf-token")
public interface KleDao extends JpaRepository<Kle, String>, QuerydslPredicateExecutor<Kle>, QuerydslBinderCustomizer<QKle> {

	@RestResource(exported = false)
	void delete(Kle entity);

	@RestResource(exported = false)
	void deleteById(String id);

	@RestResource(exported = false)
	void deleteAll();

	@RestResource(exported = false)
	<S extends Kle> List<S> saveAll(Iterable<S> entities);

	@RestResource(exported = false)
	<S extends Kle> S save(S entity);

	@RestResource(exported = false)
	<S extends Kle> S saveAndFlush(S entity);

	// internal use only
	@RestResource(exported = false)
	Kle getByCode(String code);
	
	// exported to API
	List<Kle> findAll();

	@Override
	default void customize(QuerydslBindings bindings, QKle kle) {

	}
}
