package dk.digitalidentity.ap.api.custom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.annotation.JsonIgnore;

import dk.digitalidentity.ap.api.ApiTestHelper;
import dk.digitalidentity.ap.dao.ProcessDao;
import dk.digitalidentity.ap.dao.model.Process;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@ActiveProfiles({ "test" })
@Transactional
public class CloneApiTest extends ApiTestHelper {

	@Autowired
	private ProcessDao processDao;

	@Before
	public void setUp() throws Exception {
		super.before();
	}
	
	// purpose of test
	// ***************
	// To ensure that modifications to the Process datamodel is reflected in the CloneAPI,
	// so we explicitly take into considerations which fields to copy into the clone
	@Test
	public void cloneProcessAndInspect() throws Exception {
		MvcResult result = this.mockMvc.perform(get("/api/processes?size=1"))
							.andExpect(status().is(200))
							.andReturn();
		String response = result.getResponse().getContentAsString();
		
		int startIdx = response.indexOf(":", response.indexOf("\"id\""));
		int stopIdx = response.indexOf(",", startIdx);
		String id = response.substring(startIdx + 1, stopIdx).trim();

		Process original = processDao.findOne(Long.parseLong(id));

		result = this.mockMvc.perform(post("/api/processes/{id}/copy", id))
					.andExpect(status().is(200))
					.andReturn();
		response = result.getResponse().getContentAsString();

		Process clone = mapper.readValue(response, Process.class);

		// see if we copied everything!
		for (PropertyDescriptor descriptor : Introspector.getBeanInfo(Process.class, Object.class).getPropertyDescriptors()) {
			Method method = descriptor.getReadMethod();

			// these fields are not cloned
			if (method.getName().equals("getContact") ||
				method.getName().equals("getOwner") ||
				method.getName().equals("getReporter") ||
				method.getName().equals("getCvr") ||
				method.getName().equals("getId") ||
				method.getName().equals("getChildren") ||
				method.getName().equals("getItSystems") ||
				method.getName().equals("getItSystemsDescription") ||
				method.getName().equals("getDecommissioned") ||
				method.getName().equals("getOrgUnits") ||
				method.getName().equals("getParents") ||
				method.getName().equals("getUsers") ||
				method.getName().equals("getVisibility") ||
				method.getName().equals("getLegalClauseLastVerified") ||
				method.getName().equals("getLinks") ||
				method.getName().equals("getStatusText") ||
				method.getName().equals("getCreated") ||
				method.getName().equals("getLastChanged")) {

				continue;
			}
			
			if (method != null && AnnotationUtils.findAnnotation(method, JsonIgnore.class) == null) {
				Object originalValue = method.invoke(original);
				Object cloneValue = method.invoke(clone);

				if (originalValue == null || cloneValue == null) {
					// one is null, the other is not
					if (originalValue != cloneValue) {
						Assert.assertFalse("Clone differs on method (one is null): " + method.getName(), true);
					}
				}
				else {
					if (originalValue instanceof Collection) {
						if (((Collection<?>) originalValue).size() != ((Collection<?>) cloneValue).size()) {
							Assert.assertFalse("Clone differs on method (list size): " + method.getName(), true);
						}
					}
					else if (!originalValue.equals(cloneValue)) {
						Assert.assertFalse("Clone differs on method (both are not-null): " + method.getName(), true);
					}
				}
			}
		}
	}
}
