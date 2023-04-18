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
	public FilterRegistrationBean<XApiSecurityFilter> xapiSecurityFilter() {
		XApiSecurityFilter filter = new XApiSecurityFilter(municipalityDao);

		FilterRegistrationBean<XApiSecurityFilter> filterRegistrationBean = new FilterRegistrationBean<XApiSecurityFilter>(filter);
		filterRegistrationBean.addUrlPatterns("/xapi/*");
		filterRegistrationBean.setOrder(100);
		
		return filterRegistrationBean;
	}
}
