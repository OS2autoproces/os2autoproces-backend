package dk.digitalidentity.ap.api.custom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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
import dk.digitalidentity.ap.dao.model.Bookmark;
import dk.digitalidentity.ap.service.BookmarkService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@ActiveProfiles({ "test" })
@Transactional
public class BookmarkApiTest extends ApiTestHelper {
	
	@Autowired
	private BookmarkService bookmarkService;

	@Before
	public void setUp() throws Exception {
		super.before();
	}
	
	// purpose of test
	// ***************
	// To ensure that the add/remove bookmark functionality functions as intended (sunshine scenarios)
	@Test
	public void bookmarkAndVerify() throws Exception {
		MvcResult result = this.mockMvc.perform(get("/api/processes?size=2"))
					.andExpect(status().is(200))
					.andReturn();
		String response = result.getResponse().getContentAsString();
		
		int startIdx = response.indexOf(":", response.indexOf("\"id\""));
		int stopIdx = response.indexOf(",", startIdx);
		String id1 = response.substring(startIdx + 1, stopIdx).trim();
		
		startIdx = response.indexOf(":", response.indexOf("\"id\"", stopIdx));
		stopIdx = response.indexOf(",", startIdx);
		String id2 = response.substring(startIdx + 1, stopIdx).trim();

		this.mockMvc.perform(put("/api/bookmarks/{id}", id1))
					.andExpect(status().is(200));
		
		this.mockMvc.perform(put("/api/bookmarks/{id}", id2))
					.andExpect(status().is(200));

		List<Bookmark> bookmarks = bookmarkService.getByUser(user);
		Assert.assertTrue(bookmarks.size() == 2);
		
		int matches = 0;
		for (Bookmark bookmark : bookmarks) {
			if (bookmark.getProcess().getId() == Long.parseLong(id1)) {
				matches++;
			}
			else if (bookmark.getProcess().getId() == Long.parseLong(id2)) {
				matches++;
			}
		}	
		Assert.assertTrue(matches == 2);
		
		this.mockMvc.perform(delete("/api/bookmarks/{id}", id1))
			.andExpect(status().is(200))
			.andReturn();

		bookmarks = bookmarkService.getByUser(user);
		Assert.assertTrue(bookmarks.size() == 1);
		Assert.assertTrue(bookmarks.get(0).getProcess().getId() == Long.parseLong(id2));
	}
}
