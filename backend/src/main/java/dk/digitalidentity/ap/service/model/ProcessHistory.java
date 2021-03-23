package dk.digitalidentity.ap.service.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import dk.digitalidentity.ap.dao.model.enums.Phase;
import dk.digitalidentity.ap.dao.model.enums.Status;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(exclude = { "rev", "revtype" })
public class ProcessHistory {
	private long rev;
	private long revtype;
	
	private String title;
	private String shortDescription;
	private String longDescription;
	private Phase phase;
	private Status status;
	private String statusText;
	private String klId;
	private String kle;
	private String form;
	private String kla;
	private String legalClause;
	private String processChallenges;
	private String solutionRequests;
	private String technicalImplementationNotes;
	private String organizationalImplementationNotes;

	private int rating;
	private String ratingComment;
	
	public ProcessHistory(ResultSet rs) throws SQLException {
		rev = rs.getLong("rev");
		revtype = rs.getLong("revtype");
		klId = rs.getString("kl_id");
		
		String phaseStr = rs.getString("phase");
		if (phaseStr != null) {
			phase = Phase.valueOf(phaseStr);
		}
		
		String statusStr = rs.getString("status");
		if (statusStr != null) {
			status = Status.valueOf(statusStr);
		}
		
		statusText = rs.getString("status_text");
		legalClause = rs.getString("legal_clause");
		kle = rs.getString("kle");
		form = rs.getString("form");
		kla = rs.getString("kla");
		title = rs.getString("title");
		shortDescription = rs.getString("short_description");
		longDescription = rs.getString("long_description");
		processChallenges = rs.getString("process_challenges");
		solutionRequests = rs.getString("solution_requests");
		technicalImplementationNotes = rs.getString("technical_implementation_notes");
		organizationalImplementationNotes = rs.getString("organizational_implementation_notes");
		rating = rs.getInt("rating");
		ratingComment = rs.getString("rating_comment");
	}
}
