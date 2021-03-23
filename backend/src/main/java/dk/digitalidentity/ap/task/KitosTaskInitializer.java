package dk.digitalidentity.ap.task;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KitosTaskInitializer {

	@Autowired
	private KitosTask kitosTask;
	
	@PostConstruct
	public void init() {
		kitosTask.init();
	}
}
