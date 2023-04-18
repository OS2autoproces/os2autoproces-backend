package dk.digitalidentity.ap.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import dk.digitalidentity.ap.dao.model.Municipality;
import dk.digitalidentity.ap.dao.model.QMunicipality;

@RepositoryRestResource(exported = false)
public interface MunicipalityDao extends JpaRepository<Municipality, Long>, QuerydslPredicateExecutor<Municipality>, QuerydslBinderCustomizer<QMunicipality> {

	List<Municipality> findAll();
	Municipality getByCvr(String cvr);
	Municipality getByApiKey(String apiKey);

	@Override
	default void customize(QuerydslBindings bindings, QMunicipality municipality) {

	}
}
