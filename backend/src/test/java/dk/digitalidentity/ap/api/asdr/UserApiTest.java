package dk.digitalidentity.ap.api.asdr;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import dk.digitalidentity.ap.api.ApiTestHelper;
import dk.digitalidentity.ap.dao.UserDao;
import dk.digitalidentity.ap.dao.model.User;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@ActiveProfiles({ "test" })
@Transactional
public class UserApiTest extends ApiTestHelper {

	@Autowired
	private UserDao userDao;

	@BeforeEach
	public void setUp() throws Exception {
		super.before();
	}
		
	// purpose of test
	// ***************
	// Verify that a user can only read Users from the same CVR
	// as the user is from.
	@Test
	public void testAccessToUsersOnlyFromSameCvr() throws Exception {
		List<User> users = userDao.findAll();
		User userDifferentCvr = null;
		User userSameCvr = null;
		
		// find Users from same and different CVRs
		for (User u : users) {
			if (!u.getCvr().equals(user.getCvr())) {
				userDifferentCvr = u;
			}
			else {
				userSameCvr = u;
			}
		}
		
		// assert that the user cannot read from other CVR
		this.mockMvc.perform(get("/api/users/{id}", userDifferentCvr.getId()))
				.andExpect(status().is(404));
		
		// assert that the user can read from same CVR
		this.mockMvc.perform(get("/api/users/{id}", userSameCvr.getId()))
				.andExpect(status().is(200));
	}
}
