package dk.digitalidentity.ap.api.custom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dk.digitalidentity.ap.api.ApiTestHelper;
import dk.digitalidentity.ap.api.model.OrgUnitDTO;
import dk.digitalidentity.ap.api.model.OrganisationDTO;
import dk.digitalidentity.ap.api.model.UserDTO;
import dk.digitalidentity.ap.dao.MunicipalityDao;
import dk.digitalidentity.ap.dao.OrgUnitDao;
import dk.digitalidentity.ap.dao.UserDao;
import dk.digitalidentity.ap.dao.model.Municipality;
import dk.digitalidentity.ap.dao.model.OrgUnit;
import dk.digitalidentity.ap.dao.model.User;
import dk.digitalidentity.ap.security.TokenClient;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@ActiveProfiles({ "test" })
@Transactional
public class OrganisationApiTest extends ApiTestHelper {
	private static final String user1Uuid = UUID.randomUUID().toString();
	private static final String user2Uuid = UUID.randomUUID().toString();
	private static final String user3Uuid = UUID.randomUUID().toString();
	private static final String user4Uuid = UUID.randomUUID().toString();
	private static final String orgUnit1Uuid = UUID.randomUUID().toString();
	private static final String orgUnit2Uuid = UUID.randomUUID().toString();
	private static final String orgUnit3Uuid = UUID.randomUUID().toString();
	private static final String orgUnit4Uuid = UUID.randomUUID().toString();
	
	@Autowired
	private MunicipalityDao municipalityDao;
	
	@Autowired
	private OrgUnitDao orgUnitDao;

	@Autowired
	private UserDao userDao;

	@Before
	public void setUp() throws Exception {
		super.before();
	}
	
	// purpose of test
	// ***************
	// To verify that a fresh municipality can be loaded, and that additional posts
	// will update the data according to the business rules
	@Test
	public void testLoadAndOverwriteOrgData() throws Exception {
		// create a fresh municipality for testing
		Municipality municipality = new Municipality();
		municipality.setApiKey("apiKey");
		municipality.setCvr("11223344");
		municipality.setName("Test Municipality");
		municipalityDao.save(municipality);
				
		// verify database is empty
		List<User> users = userDao.getByCvr("11223344");
		Assert.assertTrue(users.size() == 0);

		List<OrgUnit> orgUnits = orgUnitDao.getByCvr("11223344");
		Assert.assertTrue(orgUnits.size() == 0);
		
		fakeClientLogin(municipality);
		String payload = getPayload(false);

		this.mockMvc.perform(post("/xapi/organisation")
				.contentType(MediaType.APPLICATION_JSON)
				.content(payload))
				.andExpect(status().is(200));

		// verify content is created
		users = userDao.getByCvr("11223344");
		Assert.assertTrue(users.size() == 3);

		orgUnits = orgUnitDao.getByCvr("11223344");
		Assert.assertTrue(orgUnits.size() == 3);

		// data check
		User user2 = userDao.getByUuidAndCvrAndActiveTrue(user2Uuid, "11223344");
		Assert.assertTrue(user2.getPositions().size() == 2);
		Assert.assertTrue(user2.getName().equals("user 2"));

		fakeClientLogin(municipality);
		payload = getPayload(true);

		this.mockMvc.perform(post("/xapi/organisation")
				.contentType(MediaType.APPLICATION_JSON)
				.content(payload))
				.andExpect(status().is(200));
		
		// verify content is created
		users = userDao.getByCvr("11223344");
		Assert.assertTrue(users.size() == 4);

		orgUnits = orgUnitDao.getByCvr("11223344");
		Assert.assertTrue(orgUnits.size() == 4);

		// data check
		User user3 = userDao.getByUuidAndCvrAndActiveTrue(user3Uuid, "11223344");
		Assert.assertNull(user3);

		OrgUnit orgUnit3 = orgUnitDao.getByUuidAndCvrAndActiveTrue(orgUnit3Uuid, "11223344");
		Assert.assertNull(orgUnit3);

		User user4 = userDao.getByUuidAndCvrAndActiveTrue(user4Uuid, "11223344");
		Assert.assertTrue(user4.isActive());
		Assert.assertTrue(user4.getName().equals("user 4"));
	}
	
	private String getPayload(boolean withUpdates) throws Exception {
		OrganisationDTO dto = new OrganisationDTO();
		List<UserDTO> users = new ArrayList<>();
		List<OrgUnitDTO> orgUnits = new ArrayList<>();
		
		OrgUnitDTO orgUnit1 = new OrgUnitDTO();
		orgUnit1.setName("ou 1");
		orgUnit1.setUuid(orgUnit1Uuid);
		orgUnits.add(orgUnit1);
		
		OrgUnitDTO orgUnit2 = new OrgUnitDTO();
		orgUnit2.setName("ou 2");
		orgUnit2.setUuid(orgUnit2Uuid);
		orgUnits.add(orgUnit2);
		
		UserDTO user1 = new UserDTO();
		user1.setEmail("user1@email.com");
		user1.setName("user 1");
		user1.setUuid(user1Uuid);
		List<String> positions = new ArrayList<>();
		positions.add(orgUnit1.getUuid());
		user1.setPositions(positions);
		users.add(user1);
		
		UserDTO user2 = new UserDTO();
		user2.setEmail("user2@email.com");
		user2.setName("user 2");
		user2.setUuid(user2Uuid);
		positions = new ArrayList<>();
		positions.add(orgUnit1.getUuid());
		positions.add(orgUnit2.getUuid());
		user2.setPositions(positions);
		users.add(user2);
		
		if (!withUpdates) {
			OrgUnitDTO orgUnit3 = new OrgUnitDTO();
			orgUnit3.setName("ou 3");
			orgUnit3.setUuid(orgUnit3Uuid);
			orgUnits.add(orgUnit3);
			
			UserDTO user3 = new UserDTO();
			user3.setEmail("user3@email.com");
			user3.setName("user 3");
			user3.setUuid(user3Uuid);
			positions = new ArrayList<>();
			positions.add(orgUnit3.getUuid());
			user3.setPositions(positions);
			users.add(user3);
		}
		else {
			OrgUnitDTO orgUnit4 = new OrgUnitDTO();
			orgUnit4.setName("ou 4");
			orgUnit4.setUuid(orgUnit4Uuid);
			orgUnits.add(orgUnit4);
			
			UserDTO user4 = new UserDTO();
			user4.setEmail("user4@email.com");
			user4.setName("user 4");
			user4.setUuid(user4Uuid);
			positions = new ArrayList<>();
			positions.add(orgUnit4.getUuid());
			user4.setPositions(positions);
			users.add(user4);
		}
		
		dto.setOrgUnits(orgUnits);
		dto.setUsers(users);
		
		return mapper.writeValueAsString(dto);
	}
	
	private void fakeClientLogin(Municipality municipality) {
		ArrayList<GrantedAuthority> authorities = new ArrayList<>();

		TokenClient token = new TokenClient(municipality.getName(), municipality.getApiKey(), authorities);
		token.setCvr(municipality.getCvr());

		SecurityContextHolder.getContext().setAuthentication(token);
	}
}
