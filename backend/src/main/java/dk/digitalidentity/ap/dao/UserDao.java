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

import dk.digitalidentity.ap.dao.model.QUser;
import dk.digitalidentity.ap.dao.model.User;

@SecurityRequirement(name = "Authorization")
@CrossOrigin(exposedHeaders = "x-csrf-token")
public interface UserDao extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User>, QuerydslBinderCustomizer<QUser> {

	@RestResource(exported = false)
	void delete(User entity);

	@RestResource(exported = false)
	void deleteById(Long id);

	@RestResource(exported = false)
	void deleteAll();

	@RestResource(exported = false)
	<S extends User> List<S> saveAll(Iterable<S> entities);

	@RestResource(exported = false)
	<S extends User> S save(S entity);

	@RestResource(exported = false)
	<S extends User> S saveAndFlush(S entity);
	
	// methods for internal use
	@RestResource(exported = false)
	User getByUuidAndCvrAndActiveTrue(String uuid, String cvr);
	
	@RestResource(exported = false)
	List<User> getByUuidAndCvr(String uuid, String cvr);
	
	@RestResource(exported = false)
	List<User> getByCvr(String cvr);

	@Override
	default void customize(QuerydslBindings bindings, QUser user) {

		// enable case-insensitive searching
		bindings.bind(String.class).first((StringPath path, String value) -> path.containsIgnoreCase(value));
		
		// infix search on name
		bindings.bind(user.name).first((path, value) -> path.contains(value));
	}
}
