package dk.digitalidentity.ap.dao;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import dk.digitalidentity.ap.dao.model.QState;
import dk.digitalidentity.ap.dao.model.State;

@RepositoryRestResource(exported = false)
public interface StateDao extends JpaRepository<State, Long>, QueryDslPredicateExecutor<State>, QuerydslBinderCustomizer<QState> {

	@Query(value = "SELECT CURRENT_TIMESTAMP", nativeQuery = true)
	Date getCurrentTimestamp();

	State getByKey(String key);

	@Override
	default void customize(QuerydslBindings bindings, QState state) {

	}
}
