package dk.digitalidentity.ap.task;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class STSOrganisationTaskInitializer {

	@Autowired
	private STSOrganisationTask task;
	
	@PostConstruct
	public void init() {
		task.init();
	}
}
