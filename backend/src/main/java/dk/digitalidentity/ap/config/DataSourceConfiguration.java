package dk.digitalidentity.ap.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfiguration {

	@Value("${dataSource.url}")
	private String url;

	@Value("${dataSource.username}")
	private String username;

	@Value("${dataSource.password}")
	private String password;

	@Value("${dataSource.driverClassName:com.mysql.jdbc.Driver}")
	private String driverClassName;

	@Value("${dataSource.maxActive:40}")
	private int maxActive;

	@Value("${dataSource.maxIdle:5}")
	private int maxIdle;

	@Value("${dataSource.minIdle:2}")
	private int minIdle;

	@Value("${dataSource.initialSize:2}")
	private int initialSize;

	@Value("${dataSource.testWhileIdle:true}")
	private boolean testWhileIdle;

	@Value("${dataSource.validationQuery:SELECT 1}")
	private String validationQuery;

	@Value("${dataSource.validationInterval:30000}")
	private int validationInterval;

	@Value("${dataSource.testOnBorrow:true}")
	private boolean testOnBorrow;

	@Value("${dataSource.maxAge:360000}")
	private int maxAge;

	@Bean
	public DataSource dataSource() {
		org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();

		dataSource.setDriverClassName(driverClassName);
		dataSource.setPassword(password);
		dataSource.setUsername(username);
		dataSource.setUrl(url);
		dataSource.setMaxActive(maxActive);
		dataSource.setMinIdle(minIdle);
		dataSource.setMaxIdle(maxIdle);
		dataSource.setInitialSize(initialSize);
		dataSource.setTestWhileIdle(testWhileIdle);
		dataSource.setValidationQuery(validationQuery);
		dataSource.setValidationInterval(validationInterval);
		dataSource.setTestOnBorrow(testOnBorrow);
		dataSource.setMaxAge(maxAge);

		return dataSource;
	}
}
