package dk.digitalidentity.ap.dao;

import dk.digitalidentity.ap.dao.model.Logo;
import dk.digitalidentity.ap.dao.model.QLogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface LogoDao extends JpaRepository<Logo, Long>, QuerydslPredicateExecutor<Logo>, QuerydslBinderCustomizer<QLogo> {

	@Override
	default void customize(QuerydslBindings bindings, QLogo logo) {

	}
}
