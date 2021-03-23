package dk.digitalidentity.ap.service.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessHistoryPair {
	private long id;
	private ProcessHistory prev;
	private ProcessHistory latest;
	
	public boolean hasChanges() {
		return (!prev.equals(latest));
	}
}
