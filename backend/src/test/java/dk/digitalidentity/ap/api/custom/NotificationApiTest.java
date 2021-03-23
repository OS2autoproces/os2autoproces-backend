package dk.digitalidentity.ap.api.custom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import dk.digitalidentity.ap.api.ApiTestHelper;
import dk.digitalidentity.ap.dao.NotificationDao;
import dk.digitalidentity.ap.dao.ProcessDao;
import dk.digitalidentity.ap.dao.model.Notification;
import dk.digitalidentity.ap.dao.model.Process;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@ActiveProfiles({ "test" })
@Transactional
public class NotificationApiTest extends ApiTestHelper {

	@Autowired
	private NotificationDao notificationDao;

	@Autowired
	private ProcessDao processDao;
	
	@Before
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
		
		Process process = processDao.findOne(Long.parseLong(id));

		Notification notification = notificationDao.getByUserAndProcess(user, process);
		Assert.assertNull(notification);

		this.mockMvc.perform(put("/api/notifications/{id}", id))
					.andExpect(status().is(200));

		notification = notificationDao.getByUserAndProcess(user, process);
		Assert.assertNotNull(notification);

		this.mockMvc.perform(delete("/api/notifications/{id}", id))
					.andExpect(status().is(200));

		notification = notificationDao.getByUserAndProcess(user, process);
		Assert.assertNull(notification);
	}
}
