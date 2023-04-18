package dk.digitalidentity.ap.api.custom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import dk.digitalidentity.ap.api.ApiTestHelper;
import dk.digitalidentity.ap.api.model.SystemUserDTO;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@ActiveProfiles({ "test" })
@Transactional
public class PublicApiTest extends ApiTestHelper {

	@BeforeEach
	public void setUp() throws Exception {
		super.before();
	}
	
	// purpose of test
	// ***************
	// To verify that the WhoAmI method returns the expected data
	@Test
	public void testWhoAmI() throws Exception {
		// call WhoAmI
		MvcResult result = this.mockMvc.perform(get("/public/whoami"))
				.andExpect(status().is(200))
				.andReturn();
		String response = result.getResponse().getContentAsString();

		SystemUserDTO who = mapper.readValue(response, SystemUserDTO.class);
		Assertions.assertTrue(who.getCvr().equals(user.getCvr()));
		Assertions.assertTrue(who.getName().equals(user.getName()));
		Assertions.assertTrue(who.getUuid().equals(user.getUuid()));
		Assertions.assertTrue(who.getRoles().size() == 3);

		// logout
		fakeLogin(null, null);
		
		// call WhoAmI
		result = this.mockMvc.perform(get("/public/whoami"))
				.andExpect(status().is(200))
				.andReturn();
		response = result.getResponse().getContentAsString();

		who = mapper.readValue(response, SystemUserDTO.class);
		Assertions.assertTrue(who.getCvr() == null);
		Assertions.assertTrue(who.getName() == null);
	}
}
