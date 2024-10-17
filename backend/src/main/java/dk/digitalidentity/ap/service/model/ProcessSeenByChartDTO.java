package dk.digitalidentity.ap.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProcessSeenByChartDTO {
	private Long processId;
	private String title;
	private Long count;
	private int sortOrder;
}
