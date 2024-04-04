package dk.digitalidentity.ap.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import dk.digitalidentity.ap.service.model.ProcessHistory;
import dk.digitalidentity.ap.service.model.ProcessHistoryPair;

@Service
public class ProcessModificationsService {
	private static final String SELECT_SQL = "SELECT rev, revtype, kl_id, phase, status, status_text, legal_clause, kle, form, kla, title, short_description, long_description, process_challenges, solution_requests, technical_implementation_notes, organizational_implementation_notes, rating, rating_comment, automation_description FROM process_aud WHERE id = ? ORDER BY rev DESC LIMIT ?";
	private static final String COUNT_SQL = "SELECT count(*) FROM process_aud WHERE id = ? AND last_changed > ?";

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public ProcessHistoryPair getHistory(long id, Date lastChanged) {
		int count = jdbcTemplate.queryForObject(COUNT_SQL, new Object[] { id, lastChanged }, Integer.class);
		
		List<ProcessHistory> processes = jdbcTemplate.query(SELECT_SQL, new Object[] { id, count + 1 }, (RowMapper<ProcessHistory>) (rs, rowNum) -> {
			return new ProcessHistory(rs);
		});

		if (processes == null || processes.size() < 2 || processes.get(0).getRevtype() == 2 || processes.get(1).getRevtype() == 2) {
			return null;
		}

		processes.sort((p1, p2) -> Long.compare(p1.getRev(), p2.getRev()));

		ProcessHistoryPair pair = new ProcessHistoryPair();
		pair.setId(id);
		pair.setLatest(processes.get(processes.size() - 1));
		pair.setPrev(processes.get(0));
		
		return pair;
	}
}
