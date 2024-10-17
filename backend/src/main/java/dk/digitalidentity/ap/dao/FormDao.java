package dk.digitalidentity.ap.dao;

import java.util.List;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import dk.digitalidentity.ap.dao.model.Form;
import dk.digitalidentity.ap.dao.model.QForm;

@SecurityRequirement(name = "Authorization")
@CrossOrigin(exposedHeaders = "x-csrf-token")
public interface FormDao extends JpaRepository<Form, String>, QuerydslPredicateExecutor<Form>, QuerydslBinderCustomizer<QForm> {

	@RestResource(exported = false)
	void delete(Form entity);

	@RestResource(exported = false)
	void deleteById(String id);

	@RestResource(exported = false)
	void deleteAll();

	@RestResource(exported = false)
	<S extends Form> List<S> saveAll(Iterable<S> entities);

	@RestResource(exported = false)
	<S extends Form> S save(S entity);

	@RestResource(exported = false)
	<S extends Form> S saveAndFlush(S entity);

	// internal use only
	@RestResource(exported = false)
	Form getByCode(String code);

	// exported to API
	List<Form> findAll();

	@Override
	default void customize(QuerydslBindings bindings, QForm form) {

	}
}
