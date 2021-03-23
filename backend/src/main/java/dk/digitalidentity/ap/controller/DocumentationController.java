package dk.digitalidentity.ap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DocumentationController {

	@GetMapping("/doc")
	public String getDocumentationPage() {
		return "index";
	}
}
