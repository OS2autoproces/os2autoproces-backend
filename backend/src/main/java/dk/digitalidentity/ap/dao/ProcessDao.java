package dk.digitalidentity.ap.dao;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.QProcess;

@CrossOrigin(exposedHeaders = "x-csrf-token")
public interface ProcessDao extends JpaRepository<Process, Long>, QueryDslPredicateExecutor<Process>, QuerydslBinderCustomizer<QProcess> {
	
	@RestResource(exported = false)
	List<Process> findByLastChangedBetween(Date begin, Date end);

	@RestResource(exported = false)
	@Query(value = "SELECT * FROM process" +
	               "  WHERE phase = 'OPERATION'" +
	               "  AND LENGTH(legal_clause) > 0" +
	               "  AND (legal_clause_last_verified < SUBDATE(CURRENT_TIMESTAMP, INTERVAL 6 MONTH) OR legal_clause_last_verified IS NULL)",
	               nativeQuery = true)
	List<Process> getByLegalClausePendingVerification();

	@RestResource(exported = false)
	@Query(value = "SELECT search_words FROM process WHERE search_words IS NOT NULL", nativeQuery = true)
	List<String> getAllSearchWords();

	@RestResource(exported = false)
	@Modifying
	@Transactional
	@Query(value = "UPDATE process SET visibility = :visibility WHERE id = :processId", nativeQuery = true)
	void setProcessVisibility(long processId, String visibility);
	
	@Override
	default void customize(QuerydslBindings bindings, QProcess root) {
		bindings.bind(root.lastChanged).all((path, value) -> {
			Iterator<? extends Date> it = value.iterator();

			return path.after(it.next());
		});

		bindings.bind(root.created).all((path, value) -> {
			Iterator<? extends Date> it = value.iterator();

			return path.after(it.next());
		});
	}
}