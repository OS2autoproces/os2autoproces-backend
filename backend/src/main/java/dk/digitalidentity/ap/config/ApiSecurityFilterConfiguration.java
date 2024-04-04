package dk.digitalidentity.ap.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dk.digitalidentity.ap.dao.MunicipalityDao;
import dk.digitalidentity.ap.security.ApiSecurityFilter;

@Configuration
public class ApiSecurityFilterConfiguration {

	@Autowired
	private MunicipalityDao municipalityDao;

	@Bean(name="ApiSecurityFilter")
	public FilterRegistrationBean<ApiSecurityFilter> apiSecurityFilter() {
		ApiSecurityFilter filter = new ApiSecurityFilter(municipalityDao);

		FilterRegistrationBean<ApiSecurityFilter> filterRegistrationBean = new FilterRegistrationBean<ApiSecurityFilter>(filter);
		filterRegistrationBean.addUrlPatterns("/api/*");
		filterRegistrationBean.setOrder(1); // 1 is before SAML Module which runs at 2

		return filterRegistrationBean;
	}
}
