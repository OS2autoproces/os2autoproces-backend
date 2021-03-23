package dk.digitalidentity.ap.task;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KleTaskInitializer {

	@Autowired
	private KleTask kleParser;
	
	@PostConstruct
	public void init() {
		kleParser.init();
	}
}
