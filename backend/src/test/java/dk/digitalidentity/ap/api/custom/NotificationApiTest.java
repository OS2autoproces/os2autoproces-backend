package dk.digitalidentity.ap.api.custom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import dk.digitalidentity.ap.api.ApiTestHelper;
import dk.digitalidentity.ap.dao.NotificationDao;
import dk.digitalidentity.ap.dao.ProcessDao;
import dk.digitalidentity.ap.dao.model.Notification;
import dk.digitalidentity.ap.dao.model.Process;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@ActiveProfiles({ "test" })
@Transactional
public class NotificationApiTest extends ApiTestHelper {

	@Autowired
	private NotificationDao notificationDao;

	@Autowired
	private ProcessDao processDao;
	
	@BeforeEach
	public void setUp() throws Exception {
		super.before();
	}
	
	// purpose of test
	// ***************
	// To ensure that the subscribing/unsubscribing to notifications works as intended
	@Test
	public void subscribeAndVerify() throws Exception {
		MvcResult result = this.mockMvc.perform(get("/api/processes?size=1"))
					.andExpect(status().is(200))
					.andReturn();
		String response = result.getResponse().getContentAsString();
		
		int startIdx = response.indexOf(":", response.indexOf("\"id\""));
		int stopIdx = response.indexOf(",", startIdx);
		String id = response.substring(startIdx + 1, stopIdx).trim();
		
		Optional<Process> process = processDao.findById(Long.parseLong(id));

		Notification notification = notificationDao.getByUserIdAndProcess(user.getId(), process.get());
		Assertions.assertNull(notification);

		this.mockMvc.perform(put("/api/notifications/{id}", id))
					.andExpect(status().is(200));

		notification = notificationDao.getByUserIdAndProcess(user.getId(), process.get());
		Assertions.assertNotNull(notification);

		this.mockMvc.perform(delete("/api/notifications/{id}", id))
					.andExpect(status().is(200));

		notification = notificationDao.getByUserIdAndProcess(user.getId(), process.get());
		Assertions.assertNull(notification);
	}
}
