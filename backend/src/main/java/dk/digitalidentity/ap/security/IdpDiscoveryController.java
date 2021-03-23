package dk.digitalidentity.ap.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IdpDiscoveryController {

	@Value("${frontend.discovery.url}")
	public String discoveryUrl;
	
	@GetMapping("/discovery")
	public String discovery() {
		return "redirect:" + discoveryUrl;
	}
}
