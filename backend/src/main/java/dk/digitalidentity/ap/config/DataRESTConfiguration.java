package dk.digitalidentity.ap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import dk.digitalidentity.ap.dao.impl.AutoProcessRepositoryFactoryBean;
import dk.digitalidentity.ap.dao.model.Form;
import dk.digitalidentity.ap.dao.model.ItSystem;
import dk.digitalidentity.ap.dao.model.Kle;
import dk.digitalidentity.ap.dao.model.OrgUnit;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.Technology;
import dk.digitalidentity.ap.dao.model.User;
import dk.digitalidentity.ap.dao.model.validator.ProcessValidator;

@Configuration
@EnableJpaRepositories(basePackages = "dk.digitalidentity.ap.dao", repositoryFactoryBeanClass = AutoProcessRepositoryFactoryBean.class)
public class DataRESTConfiguration implements RepositoryRestConfigurer {

	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
		config.exposeIdsFor(Process.class, Technology.class, ItSystem.class, Kle.class, Form.class, User.class, OrgUnit.class);
	}
	
	@Bean
	public Validator validator() {
		return new LocalValidatorFactoryBean();
	}

	@Bean
	public SpelAwareProxyProjectionFactory projectionFactory() {
		return new SpelAwareProxyProjectionFactory();
	}

	@Override
	public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener validatingListener) {
		Validator validator = validator();

		// bean validation always before save and create
		validatingListener.addValidator("beforeCreate", validator);
		validatingListener.addValidator("beforeSave", validator);
		validatingListener.addValidator("BeforeLinkSave", validator);
		validatingListener.addValidator("BeforeDelete", validator);

		// special validation rules for Process
		ProcessValidator processValidator = new ProcessValidator();
		validatingListener.addValidator("beforeCreate", processValidator);
		validatingListener.addValidator("beforeSave", processValidator);
		validatingListener.addValidator("BeforeLinkSave", processValidator);
	}
}