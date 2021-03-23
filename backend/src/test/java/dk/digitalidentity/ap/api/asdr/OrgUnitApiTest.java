package dk.digitalidentity.ap.api.asdr;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dk.digitalidentity.ap.api.ApiTestHelper;
import dk.digitalidentity.ap.dao.OrgUnitDao;
import dk.digitalidentity.ap.dao.model.OrgUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@ActiveProfiles({ "test" })
@Transactional
public class OrgUnitApiTest extends ApiTestHelper {

	@Autowired
	private OrgUnitDao orgUnitDao;

	@Before
	public void setUp() throws Exception {
		super.before();
	}
		
	// purpose of test
	// ***************
	// Verify that a user can only read OrgUnits from the same CVR
	// as the user is from.
	@Test
	public void testAccessToOUOnlyFromSameCvr() throws Exception {
		List<OrgUnit> orgUnits = orgUnitDao.findAll();
		OrgUnit orgUnitDifferentCvr = null;
		OrgUnit orgUnitSameCvr = null;
		
		// find OUs from same and different CVRs
		for (OrgUnit ou : orgUnits) {
			if (!ou.getCvr().equals(user.getCvr())) {
				orgUnitDifferentCvr = ou;
			}
			else {
				orgUnitSameCvr = ou;
			}
		}
		
		// assert that the user cannot read from other CVR
		this.mockMvc.perform(get("/api/orgUnits/{id}", orgUnitDifferentCvr.getId()))
				.andExpect(status().is(404));
		
		// assert that the user can read from same CVR
		this.mockMvc.perform(get("/api/orgUnits/{id}", orgUnitSameCvr.getId()))
				.andExpect(status().is(200));
	}
}
