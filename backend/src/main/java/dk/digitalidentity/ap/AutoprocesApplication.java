package dk.digitalidentity.ap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SpringBootApplication(scanBasePackages = "dk.digitalidentity")
@OpenAPIDefinition(info = @Info(title = "OS2autoproces", version = "1.0"))
@SecurityScheme(name = "Authorization", paramName = "Authorization", scheme = "basic", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
@EnableMethodSecurity
public class AutoprocesApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutoprocesApplication.class, args);
	}
}