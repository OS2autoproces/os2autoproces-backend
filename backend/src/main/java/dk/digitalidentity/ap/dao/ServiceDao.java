package dk.digitalidentity.ap.dao;

import dk.digitalidentity.ap.dao.model.QService;
import dk.digitalidentity.ap.dao.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface ServiceDao extends JpaRepository<Service, Long>, QuerydslPredicateExecutor<Service>, QuerydslBinderCustomizer<QService> {
	List<Service> findByName(String name);

	public interface ServiceUsage {
		String getName();
		long getCount();
	}

	@Query(nativeQuery = true, value = "select s.name as name, count(*) as count from service s join process_services ps on ps.service_id = s.id join process p on p.id = ps.process_id group by s.name order by count desc limit 10")
	List<ServiceUsage> getUsage();

	@Query(nativeQuery = true, value = "select s.name as name, count(*) as count from service s join process_services ps on ps.service_id = s.id join process p on p.id = ps.process_id where p.cvr = ?1 group by s.name order by count desc limit 10")
	List<ServiceUsage> getUsageByCvr(String cvr);

	@Override
	default void customize(QuerydslBindings bindings, QService service) {

	}
}
