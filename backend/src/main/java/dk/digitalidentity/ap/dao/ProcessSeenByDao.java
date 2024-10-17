package dk.digitalidentity.ap.dao;

import dk.digitalidentity.ap.dao.model.ProcessSeenBy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface ProcessSeenByDao extends JpaRepository<ProcessSeenBy, Long> {

	long countByProcessId(@Param("processId") long processId);

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO process_seen_by (user_id, process_id) " +
			"SELECT :userId, :processId FROM DUAL " +
			"WHERE NOT EXISTS (SELECT 1 FROM process_seen_by " +
			"WHERE user_id = :userId AND process_id = :processId)", nativeQuery = true)
	int insertIfNotExists(@Param("userId") long userId, @Param("processId") long processId);

	@Query(value = "SELECT ranked.sort_order, ranked.process_id AS processId, ranked.title AS title, ranked.count " +
			"FROM (SELECT psb.process_id, p.title AS title, COUNT(psb.process_id) AS count, " +
			"ROW_NUMBER() OVER(ORDER BY COUNT(psb.process_id) DESC) AS sort_order " +
			"FROM process_seen_by psb " +
			"JOIN process p ON psb.process_id = p.id " +
			"GROUP BY psb.process_id, p.title) AS ranked " +
			"ORDER BY ranked.count DESC " +
			"LIMIT 10", nativeQuery = true)
	List<Object[]> findTop10ProcessIdsWithTitleCountAndRank();
}
