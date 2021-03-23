package dk.digitalidentity.ap.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ROLE_FRONTPAGE_EDITOR')")
public @interface RequireFrontPageEditorRole {

}
