package dk.digitalidentity.ap.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dk.digitalidentity.ap.dao.MunicipalityDao;
import dk.digitalidentity.ap.security.XApiSecurityFilter;

@Configuration
public class XApiSecurityFilterConfiguration {

	@Autowired
	private MunicipalityDao municipalityDao;

	@Bean(name="XApiSecurityFilter")
	public FilterRegistrationBean xapiSecurityFilter() {
		XApiSecurityFilter filter = new XApiSecurityFilter(municipalityDao);

		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(filter);
		filterRegistrationBean.addUrlPatterns("/xapi/*");
		filterRegistrationBean.setOrder(100);
		
		return filterRegistrationBean;
	}
}
