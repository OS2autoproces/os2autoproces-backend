package dk.digitalidentity.ap.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import dk.digitalidentity.ap.dao.model.Municipality;
import dk.digitalidentity.ap.service.MunicipalityService;

@Controller
public class ErrorPageController implements ErrorController {
	private ErrorAttributes errorAttributes = new DefaultErrorAttributes();

	@Autowired
	private MunicipalityService municipalityService;

	@RequestMapping(value = "/saml/error", produces = "text/html")
	public String errorPage(Model model, HttpServletRequest request) {
		Map<String, Object> body = getErrorAttributes(new ServletWebRequest(request));
		model.addAllAttributes(body);

		// deal with 404 first
		Object status = body.get("status");
        if (status != null && status instanceof Integer && (Integer) status == 404) {
        	return "404";
        }
        
		Object authException = request.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

		// handle the forward case
		if (authException == null && request.getSession() != null) {
			authException = request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}

		if (authException != null && authException instanceof Throwable) {
			StringBuilder builder = new StringBuilder();
			Throwable t = (Throwable) authException;

			logThrowable(builder, t, false);
			model.addAttribute("exception", builder.toString());

			if (authException instanceof UsernameNotFoundException) {
				String message = ((UsernameNotFoundException) authException).getMessage();

				int idx = message.indexOf("/");
				if (idx > 0) {
					String cvr = message.substring(0, idx);

					Municipality municipality = municipalityService.getByCvr(cvr);
					if (municipality != null) {
						model.addAttribute("cause", "USER");
					} else {
						model.addAttribute("cause", "ORG");
					}
				}
			}
		}

		if (!body.containsKey("cause")) {
			model.addAttribute("cause", "UNKNOWN");
		}

		return "samlerror";
	}

	@RequestMapping(value = "/error", produces = "application/json")
	public ResponseEntity<Map<String, Object>> errorJSON(HttpServletRequest request) {
		Map<String, Object> body = getErrorAttributes(new ServletWebRequest(request));

		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		try {
			status = HttpStatus.valueOf((int) body.get("status"));
		}
		catch (Exception ex) {
			;
		}

		return new ResponseEntity<>(body, status);
	}

	private Map<String, Object> getErrorAttributes(WebRequest request) {
		return errorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());
	}
	
	private void logThrowable(StringBuilder builder, Throwable t, boolean append) {
		StackTraceElement[] stackTraceElements = t.getStackTrace();

		builder.append((append ? "Caused by: " : "") + t.getClass().getName() + ": " + t.getMessage() + "\n");
		for (int i = 0; i < 5 && i < stackTraceElements.length; i++) {
			builder.append("  ... " + stackTraceElements[i].toString() + "\n");
		}

		if (t.getCause() != null) {
			logThrowable(builder, t.getCause(), true);
		}
	}
}
