package dk.digitalidentity.ap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import dk.digitalidentity.ap.interceptor.ProcessSaveInterceptor;

@Configuration
@EnableAspectJAutoProxy
public class InterceptorConfiguration {

	@Bean
	public ProcessSaveInterceptor processSaveInterceptor() {
		return new ProcessSaveInterceptor();
	}
}