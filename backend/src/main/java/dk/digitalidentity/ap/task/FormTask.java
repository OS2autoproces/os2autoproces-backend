package dk.digitalidentity.ap.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.service.FormService;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableScheduling
@EnableAsync
@Slf4j
public class FormTask {

	@Value("${scheduled.enabled:false}")
	private boolean runScheduled;

	@Autowired
	private FormService formService;
		
	@Async
	public void init() {
		if (runScheduled && formService.count() == 0) {
			parse();
		}
	}

	// Run every Saturday at 21:00
	@Scheduled(cron = "0 0 21 * * SAT")
	public synchronized void parse() {
		if (!runScheduled) {
			return;
		}

		log.info("Running Scheduled Task: " + this.getClass().getName());

		formService.updateFormData();
	}
}
