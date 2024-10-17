package dk.digitalidentity.ap;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.autoconfigure.actuate.CloudWatchMetricAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.cache.ElastiCacheAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.jdbc.AmazonRdsDatabaseAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.messaging.MessagingAutoConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication(scanBasePackages = "dk.digitalidentity")
@EnableAutoConfiguration(exclude = {
		CloudWatchMetricAutoConfiguration.class,
        ElastiCacheAutoConfiguration.class,
        ContextStackAutoConfiguration.class,
        AmazonRdsDatabaseAutoConfiguration.class,
        MessagingAutoConfiguration.class
})
@OpenAPIDefinition(info = @Info(title = "OS2autoproces", version = "1.0"))
@SecurityScheme(name = "Authorization", paramName = "Authorization", scheme = "basic", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
@EnableMethodSecurity
public class AutoprocesApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutoprocesApplication.class, args);
	}
}