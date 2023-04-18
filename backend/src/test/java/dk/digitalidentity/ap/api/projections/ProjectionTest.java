package dk.digitalidentity.ap.api.projections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.projection.ProcessExtendedProjection;
import dk.digitalidentity.ap.util.ProjectionAnalyzer;

@ExtendWith(SpringExtension.class)
public class ProjectionTest {

	// purpose of test
	// ***************
	// To ensure that modifications to the Process datamodel is reflected in the Extended projection
	@Test
	public void testProcess() {
		Assertions.assertTrue(ProjectionAnalyzer.verifyProjectionExtendsEntity(Process.class, ProcessExtendedProjection.class));
		
		// To verify that the extension does not contain fields that have been removed from the
		// Process datamodel, comment out the extensions and then run this line - it should pass as they are now identical
		// assertTrue(ProjectionAnalyzer.verifyEntityEqualsExtension(Process.class, ProcessExtendedProjection.class));
	}
}
