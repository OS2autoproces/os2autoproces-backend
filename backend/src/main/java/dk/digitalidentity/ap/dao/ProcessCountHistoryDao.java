package dk.digitalidentity.ap.dao;

import dk.digitalidentity.ap.dao.model.ProcessCountHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ProcessCountHistoryDao extends JpaRepository<ProcessCountHistory, Long> {
	void deleteByCountedDateBefore(LocalDate before);

	ProcessCountHistory findByCvrAndCountedQuarter(String cvr, String countedQuarter);

}
