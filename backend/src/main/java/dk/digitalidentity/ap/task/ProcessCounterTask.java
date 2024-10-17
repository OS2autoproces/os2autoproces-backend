package dk.digitalidentity.ap.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dk.digitalidentity.ap.service.ProcessService;

@Component
@EnableScheduling
public class ProcessCounterTask {

	@Value("${scheduled.enabled:false}")
	private boolean runScheduled;

	@Autowired
	private ProcessService processService;

	// take a snapshot of the process count last day every three months at 21.55
	@Scheduled(cron = "0 55 21 L MAR,JUN,SEP,DEC ?")
	public void sendNotifications() {
		if (!runScheduled) {
			return;
		}

		processService.count();
	}

}
