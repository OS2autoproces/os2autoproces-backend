package dk.digitalidentity.ap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import dk.digitalidentity.ap.security.CommentUsernameGenerator;

@Configuration
@EnableJpaAuditing(auditorAwareRef="auditorProvider")
public class AuditorAwareConfig {

	@Bean
	public AuditorAware<String> auditorProvider() {
		return new CommentUsernameGenerator();
	}
}