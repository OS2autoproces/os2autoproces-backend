package dk.digitalidentity.ap.task;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FormTaskInitializer {

	@Autowired
	private FormTask formParser;
	
	@PostConstruct
	public void init() {
		formParser.init();
	}
}
