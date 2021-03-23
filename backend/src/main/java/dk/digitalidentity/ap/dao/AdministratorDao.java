package dk.digitalidentity.ap.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import dk.digitalidentity.ap.dao.model.Administrator;

@RepositoryRestResource(exported = false)
public interface AdministratorDao extends JpaRepository<Administrator, Long> {

	List<Administrator> getByUserUuid(String userUuid);
}
