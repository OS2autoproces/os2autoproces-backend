package dk.digitalidentity.ap.api.projections;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.projection.ProcessExtendedProjection;
import dk.digitalidentity.ap.util.ProjectionAnalyzer;

@RunWith(SpringRunner.class)
public class ProjectionTest {

	// purpose of test
	// ***************
	// To ensure that modifications to the Process datamodel is reflected in the Extended projection
	@Test
	public void testProcess() {
		assertTrue(ProjectionAnalyzer.verifyProjectionExtendsEntity(Process.class, ProcessExtendedProjection.class));
		
		// To verify that the extension does not contain fields that have been removed from the
		// Process datamodel, comment out the extensions and then run this line - it should pass as they are now identical
		// assertTrue(ProjectionAnalyzer.verifyEntityEqualsExtension(Process.class, ProcessExtendedProjection.class));
	}
}
