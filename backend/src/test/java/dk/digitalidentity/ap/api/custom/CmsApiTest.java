package dk.digitalidentity.ap.api.custom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.internal.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dk.digitalidentity.ap.api.ApiTestHelper;
import dk.digitalidentity.ap.dao.CmsEntryDao;
import dk.digitalidentity.ap.dao.model.CmsEntry;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@ActiveProfiles({ "test" })
@Transactional
public class CmsApiTest extends ApiTestHelper {

	@Autowired
	private CmsEntryDao cmsDao;
	
	@Before
	public void setUp() throws Exception {
		super.before();
	}

	// purpose of test
	// ***************
	// Verify that both creating and deleting entries works as intended
	@Test
	public void testCreateAndDeleteCmsEntries() throws Exception {
		final String content = "Hello World!";
		final String content2 = "Hello World2!";
		final String label = "testEntry";
		
		CmsEntry entry = cmsDao.getByLabel(label);
		Assert.isNull(entry);
		
		this.mockMvc.perform(post("/api/cms/{label}", label)
				.content(content))
				.andExpect(status().is(200));

		entry = cmsDao.getByLabel(label);
		Assert.notNull(entry);
		Assert.isTrue(entry.getContent().equals(content));

		this.mockMvc.perform(post("/api/cms/{label}", label)
				.content(content2))
				.andExpect(status().is(200));

		entry = cmsDao.getByLabel(label);
		Assert.notNull(entry);
		Assert.isTrue(entry.getContent().equals(content2));

		this.mockMvc.perform(delete("/api/cms/{label}", label))
				.andExpect(status().is(200));

		entry = cmsDao.getByLabel(label);
		Assert.isNull(entry);
	}
}
