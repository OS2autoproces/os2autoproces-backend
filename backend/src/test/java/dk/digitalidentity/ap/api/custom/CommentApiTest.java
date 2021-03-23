package dk.digitalidentity.ap.api.custom;

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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dk.digitalidentity.ap.api.ApiTestHelper;
import dk.digitalidentity.ap.dao.CommentDao;
import dk.digitalidentity.ap.dao.ProcessDao;
import dk.digitalidentity.ap.dao.model.Comment;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.enums.Visibility;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@ActiveProfiles({ "test" })
@Transactional
public class CommentApiTest extends ApiTestHelper {

	@Autowired
	private CommentDao commentDao;

	@Autowired
	private ProcessDao processDao;
	
	@Before
	public void setUp() throws Exception {
		super.before();
	}
	
	// purpose of test
	// ***************
	// verify that it is possible to add comments to a process that the user
	// has read access to, but no to a process that the user does NOT have
	// read access to
	@Test
	public void testAddingComments() throws Exception {
		List<Process> processes = processDao.findAll();
		Process processWithAccess = null;
		Process processWithNoAccess = null;

		for (Process process : processes) {
			if (process.getCvr().equals(user.getCvr())) {
				processWithAccess = process;
			}
			else if (!process.getVisibility().equals(Visibility.PUBLIC)) {
				processWithNoAccess = process;
			}
		}

		List<Comment> comments = commentDao.getByProcess(processWithAccess);
		Assert.assertTrue(comments.size() == 0);

		this.mockMvc.perform(put("/api/comments/{processId}", processWithAccess.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"message\":\"Hello World\"}"))
				.andExpect(status().is(200));

		comments = commentDao.getByProcess(processWithAccess);
		Assert.assertTrue(comments.size() == 1);

		comments = commentDao.getByProcess(processWithNoAccess);
		Assert.assertTrue(comments.size() == 0);
		
		this.mockMvc.perform(put("/api/comments/{processId}", processWithNoAccess.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"message\":\"Hello World\"}"))
				.andExpect(status().is(404));
		
		comments = commentDao.getByProcess(processWithNoAccess);
		Assert.assertTrue(comments.size() == 0);
	}
}
