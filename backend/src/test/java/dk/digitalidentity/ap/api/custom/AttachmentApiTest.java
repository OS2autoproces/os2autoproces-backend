package dk.digitalidentity.ap.api.custom;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dk.digitalidentity.ap.api.ApiTestHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@ActiveProfiles({ "test" })
@Transactional
public class AttachmentApiTest extends ApiTestHelper {

	@Before
	public void setUp() throws Exception {
		super.before();
	}
	
	// purpose of test
	// ***************
	//
	@Test
	public void test() throws Exception {
		// TODO: implement tests
	}
}
