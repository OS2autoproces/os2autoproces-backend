package dk.digitalidentity.ap.dao.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@Setter
@Entity(name = "process_count_history")
public class ProcessCountHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String cvr;

	@Column
	private long processCount;

	@Column
	private String countedQuarter;

	@Column
	private LocalDate countedDate;
}
