package dk.digitalidentity.ap.api.asdr;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import dk.digitalidentity.ap.api.ApiTestHelper;
import dk.digitalidentity.ap.dao.ProcessDao;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.enums.Visibility;
import dk.digitalidentity.ap.security.SecurityUtil;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@ActiveProfiles({ "test" })
public class AAProcessApiTest extends ApiTestHelper {

	// This class is intentionally named [AA]ProcessApiTest, to ensure the tests
	// are executed first - it seems that other tests mess with the state enough
	// that these tests fails, so to avoid this issue, these tests now always run first.
	//
	// Same reason for the strange packagename for these tests ;)

	@Autowired
	private ProcessDao processDao;

	@BeforeEach
	public void setUp() throws Exception {
		// make sure we have a good solid bunch of processes to work with
		super.before(2, 2, 100);
	}

	// The API has been implemented by Spring Data REST, so all the ordinary operations
	// are tested through the tests made by the vendor of the framework
	//
	// Here we test only the customization added to the API
	
	// purpose of test
	// ***************
	// To verify that a user has read access to specific processes
	// according to the business rules
	@Test
	@Transactional(readOnly = true)
	public void testReadAccess() throws Exception {
		List<Process> allProcesses = processDao.findAll();
		
		List<Process> allPersonalNotReportedByUserAndNotAssociatedWithUserFromSameCvr = allProcesses.stream()
				.filter(p -> p.getCvr().equals("29189978"))
				.filter(p -> p.getVisibility().equals(Visibility.PERSONAL))
				.filter(p -> !p.getReporter().getUuid().equals(user.getUuid()))
				.filter(p -> !p.getUsers().stream().map(u -> u.getUuid()).collect(Collectors.toList()).contains(user.getUuid()))
				.collect(Collectors.toList());
		
		List<Process> allMunicipalityPublicFromSameCvr = allProcesses.stream()
				.filter(p -> p.getCvr().equals("29189978"))
				.filter(p -> p.getVisibility().equals(Visibility.MUNICIPALITY))
				.collect(Collectors.toList());

		List<Process> allPublicFromOtherCvr = allProcesses.stream()
				.filter(p -> p.getVisibility().equals(Visibility.PUBLIC))
				.filter(p -> !p.getCvr().equals("29189978"))
				.collect(Collectors.toList());
		
		List<Process> allNonPublicFromOtherCvr = allProcesses.stream()
				.filter(p -> !p.getVisibility().equals(Visibility.PUBLIC))
				.filter(p -> !p.getCvr().equals("29189978"))
				.collect(Collectors.toList());
		
		List<Process> allReportedByUser = allProcesses.stream()
				.filter(p -> p.getVisibility().equals(Visibility.PERSONAL))
				.filter(p -> p.getReporter() != null && p.getReporter().getUuid().equals(user.getUuid()))
				.collect(Collectors.toList());
		
		List<Process> allAssociatedWithUser = allProcesses.stream()
				.filter(p -> p.getVisibility().equals(Visibility.PERSONAL))
				.filter(p -> p.getUsers().stream().map(u -> u.getUuid()).collect(Collectors.toList()).contains(user.getUuid()))
				.collect(Collectors.toList());

		// ensure our user has no special roles, so all validation happens against associations
		ArrayList<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(SecurityUtil.ROLE_USER));
		fakeLogin(user.getCvr(), user, authorities);
		
		// assert that the user cannot read non-associated processes from same municipality
		this.mockMvc.perform(get("/api/processes/{id}", allPersonalNotReportedByUserAndNotAssociatedWithUserFromSameCvr.get(0).getId()))
					.andExpect(status().is(404));
		
		// assert that the user cannot read non-public from other municipality
		this.mockMvc.perform(get("/api/processes/{id}", allNonPublicFromOtherCvr.get(0).getId()))
					.andExpect(status().is(404));
		
		// assert that the user can read municipality-visible from same municipality
		this.mockMvc.perform(get("/api/processes/{id}", allMunicipalityPublicFromSameCvr.get(0).getId()))
					.andExpect(status().is(200));
		
		// assert that the user can read public-visible from other municipality
		this.mockMvc.perform(get("/api/processes/{id}", allPublicFromOtherCvr.get(0).getId()))
					.andExpect(status().is(200));

		// assert that the user can read all reported by self
		this.mockMvc.perform(get("/api/processes/{id}", allReportedByUser.get(0).getId()))
					.andExpect(status().is(200));

		// assert that the user can read all associated to self
		this.mockMvc.perform(get("/api/processes/{id}", allAssociatedWithUser.get(0).getId()))
					.andExpect(status().is(200));
		
		// now try with the super user, which should get more access
		authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(SecurityUtil.ROLE_SUPERUSER));
		fakeLogin(user.getCvr(), user, authorities);

		// user should now have access to these due to the role
		this.mockMvc.perform(get("/api/processes/{id}", allPersonalNotReportedByUserAndNotAssociatedWithUserFromSameCvr.get(0).getId()))
					.andExpect(status().is(200));
	}
	
	// purpose of test
	// ***************
	// To verify that a user has write access, restricted by role
	@Test
	@Transactional(readOnly = true)
	public void testWriteAccessDueToRole() throws Exception {
		List<Process> allProcesses = processDao.findAll();

		List<Process> allPersonalNotReportedByUserAndNotAssociatedWithUserFromSameCvr = allProcesses.stream()
				.filter(p -> p.getCvr().equals("29189978"))
				.filter(p -> p.getVisibility().equals(Visibility.PERSONAL))
				.filter(p -> !p.getReporter().getUuid().equals(user.getUuid()))
				.filter(p -> !p.getUsers().stream().map(u -> u.getUuid()).collect(Collectors.toList()).contains(user.getUuid()))
				.collect(Collectors.toList());

		List<Process> allFromOtherCvr = allProcesses.stream()
				.filter(p -> p.getVisibility().equals(Visibility.PUBLIC))
				.filter(p -> !p.getCvr().equals("29189978"))
				.collect(Collectors.toList());

		// ensure our user has no special roles, so all validation happens against associations
		ArrayList<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(SecurityUtil.ROLE_USER));
		fakeLogin(user.getCvr(), user, authorities);
		
		// assert that the user cannot modify non-associated processes from same municipality
		this.mockMvc.perform(patch("/api/processes/{id}", allPersonalNotReportedByUserAndNotAssociatedWithUserFromSameCvr.get(0).getId())
					.content("{\"title\":\"" + UUID.randomUUID().toString() + "\",\"contact\": \"https://localhost:9090/api/users/1\"}")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().is(404)); // no read access, so write fails with 404 instead of 403

		// assert that the user cannot modify non-associated processes from other municipality
		try {
			// the content is required as reading other municipalities processes blanks out several fields which are required
			this.mockMvc.perform(patch("/api/processes/{id}", allFromOtherCvr.get(0).getId())
						.content("{\"title\":\"veryRandomString\",\"users\":[],\"contact\": \"https://localhost:9090/api/users/1\",\"owner\": \"https://localhost:9090/api/users/1\", \"phase\": \"IDEA\"}")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().is(403)); // we get an exception instead :(

			Assertions.assertFalse(true, "Should never arrive here!");
		}
		catch (Exception ex) {
			// because the tests runs in the same context as the backend being tested, the exception
			// thrown by the backend propagates all the way up here, so we validate against the exception
			// instead of the 403 status code which we WOULD have gotten.
			
			Throwable t = ex;
			while (t.getCause() != null) {
				t = t.getCause();
			}
			
			Assertions.assertTrue(t instanceof AccessDeniedException);
		}

		// now try with a super user
		authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(SecurityUtil.ROLE_SUPERUSER));
		fakeLogin(user.getCvr(), user, authorities);

		// assert that the user can now modify non-associated processes from same municipality
		this.mockMvc.perform(patch("/api/processes/{id}", allPersonalNotReportedByUserAndNotAssociatedWithUserFromSameCvr.get(0).getId())
					.content("{\"title\":\"" + UUID.randomUUID().toString() + "\",\"contact\": \"https://localhost:9090/api/users/1\"}")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().is(200));
	}
	
	// purpose of test
	// ***************
	// To verify that a user has write access because of a user-association
	@Test
	@Transactional(readOnly = true)
	public void testWriteAccessDueToUserAssociation() throws Exception {
		List<Process> allProcesses = processDao.findAll();

		List<Process> allReportedByUser = allProcesses.stream()
				.filter(p -> p.getVisibility().equals(Visibility.PERSONAL))
				.filter(p -> p.getReporter() != null && p.getReporter().getUuid().equals(user.getUuid()))
				.collect(Collectors.toList());
		
		List<Process> allAssociatedWithUser = allProcesses.stream()
				.filter(p -> p.getVisibility().equals(Visibility.PERSONAL))
				.filter(p -> p.getUsers().stream().map(u -> u.getUuid()).collect(Collectors.toList()).contains(user.getUuid()))
				.collect(Collectors.toList());

		// ensure our user has no special roles, so all validation happens against associations
		ArrayList<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(SecurityUtil.ROLE_USER));
		fakeLogin(user.getCvr(), user, authorities);

		// assert that the user can modify processes reported by user
		this.mockMvc.perform(patch("/api/processes/{id}", allReportedByUser.get(0).getId())
					.content("{\"title\":\"" + UUID.randomUUID().toString() + "\", \"phase\": \"IDEA\"}")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().is(200));

		// assert that the user can modify processes associated with the user
		this.mockMvc.perform(patch("/api/processes/{id}", allAssociatedWithUser.get(0).getId())
					.content("{\"title\":\"" + UUID.randomUUID().toString() + "\", \"phase\": \"IDEA\"}")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().is(200));
	}
	
	// purpose of test
	// ***************
	// To verify that freetext search will return the expected results
	@Test
	@Transactional(readOnly = false)
	public void testFreeTextSearch() throws Exception {
		
		// verify that nothing matches search
		
		MvcResult result = this.mockMvc.perform(get("/api/processes?freetext={text}", "magicstring")
								.accept(MediaType.APPLICATION_JSON))
								.andExpect(status().is(200))
								.andReturn();
		
		String response = result.getResponse().getContentAsString();
		
        JSONObject object = new JSONObject(response);
        JSONObject embedded = (JSONObject) object.get("_embedded");
        String res = embedded.get("processes").toString();

        Process[] processes = mapper.readValue(res, Process[].class);
        
        Assertions.assertTrue(processes.length == 0);
		
		// update two processes to match
		
		List<Process> allProcesses = processDao.findAll();
		
		List<Process> allReportedByUser = allProcesses.stream()
				.filter(p -> p.getVisibility().equals(Visibility.PERSONAL))
				.filter(p -> p.getReporter() != null && p.getReporter().getUuid().equals(user.getUuid()))
				.collect(Collectors.toList());

		this.mockMvc.perform(patch("/api/processes/{id}", allReportedByUser.get(0).getId())
					.content("{\"title\":\"magicstring\", \"phase\": \"IDEA\"}")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().is(200));
		
		this.mockMvc.perform(patch("/api/processes/{id}", allReportedByUser.get(1).getId())
					.content("{\"shortDescription\":\"magicstring\", \"phase\": \"IDEA\"}")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().is(200));
		
		// verify that there now are two matches
		result = this.mockMvc.perform(get("/api/processes?freetext={text}", "magicstring")
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().is(200))
					.andReturn();

		response = result.getResponse().getContentAsString();

		object = new JSONObject(response);
		embedded = (JSONObject) object.get("_embedded");
		res = embedded.get("processes").toString();
		
		processes = mapper.readValue(res, Process[].class);
		
		Assertions.assertTrue(processes.length == 2);
	}
}
