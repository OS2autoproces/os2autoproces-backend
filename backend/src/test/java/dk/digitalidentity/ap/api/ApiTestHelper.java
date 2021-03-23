package dk.digitalidentity.ap.api;

import java.util.ArrayList;
import java.util.HashMap;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import dk.digitalidentity.ap.dao.model.User;
import dk.digitalidentity.ap.security.SecurityUtil;
import dk.digitalidentity.ap.service.UserService;
import dk.digitalidentity.saml.model.TokenUser;

public abstract class ApiTestHelper {
	protected ObjectMapper mapper;
	protected MockMvc mockMvc;
	protected User user;

	@Autowired
	protected UserService userService;

	@Autowired
	private BootstrapApi bootstrapper;
	
	@Autowired
	private WebApplicationContext context;

	@Autowired
	private Flyway flyway;
	
	public void before() {
		before(10, 10, 20);
	}

	public void before(int userCount, int ouCount, int processCount) {
		flyway.clean();
		flyway.migrate();

		bootstrapper.bootstrap(userCount, ouCount, processCount);
		
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
		this.mapper = new ObjectMapper();
		this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		this.user = userService.getByUuidAndCvr("39223c72-bfd5-4fd1-aee1-6c9a740b8f7f", "29189978");

		fakeLogin(user.getCvr(), user);
	}
	
	protected void fakeLogin(String cvr, User user) {
		ArrayList<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(SecurityUtil.ROLE_ADMINISTRATOR));
		authorities.add(new SimpleGrantedAuthority(SecurityUtil.ROLE_FRONTPAGE_EDITOR));
		authorities.add(new SimpleGrantedAuthority(SecurityUtil.ROLE_SUPERUSER));

		fakeLogin(cvr, user, authorities);
	}

	protected void fakeLogin(String cvr, User user, ArrayList<GrantedAuthority> authorities) {
		if (user == null) {
			SecurityContextHolder.getContext().setAuthentication(null);
			return;
		}

		TokenUser token = TokenUser.builder().authorities(authorities).cvr(cvr).username("dummy").attributes(new HashMap<>()).build();
		token.getAttributes().put("user", user);
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("dummy", null, authorities);
		auth.setDetails(token);

		SecurityContextHolder.getContext().setAuthentication(auth);
	}
}
