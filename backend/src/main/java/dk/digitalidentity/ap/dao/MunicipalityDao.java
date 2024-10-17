package dk.digitalidentity.ap.dao;

import dk.digitalidentity.ap.dao.model.Municipality;
import dk.digitalidentity.ap.dao.model.QMunicipality;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@SecurityRequirement(name = "Authorization")
@RestResource
@CrossOrigin(exposedHeaders = "x-csrf-token")
public interface MunicipalityDao extends JpaRepository<Municipality, Long>, QuerydslPredicateExecutor<Municipality>, QuerydslBinderCustomizer<QMunicipality> {

	List<Municipality> findAll();

	@RestResource(path = "byCvr", rel = "byCvr")
	Municipality getByCvr(@Param("cvr") String cvr);

	@RestResource(exported = false)
	Municipality getByApiKey(String apiKey);

	@Override
	default void customize(QuerydslBindings bindings, QMunicipality municipality) {

	}
}
