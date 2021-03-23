package dk.digitalidentity.ap.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.BasicErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import dk.digitalidentity.ap.dao.model.Municipality;
import dk.digitalidentity.ap.service.MunicipalityService;

@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class ErrorPageController extends BasicErrorController {
	
	@Value(value = "${error.showtrace:false}")
	private boolean showStackTrace;

	@Autowired
	private MunicipalityService municipalityService;

	@Autowired
	public ErrorPageController(ErrorAttributes errorAttributes, ErrorProperties errorProperties) {
		super(errorAttributes, errorProperties);
	}

	@RequestMapping(produces = "text/html")
	@Override
	public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> body = getErrorAttributes(request, showStackTrace);

		// deal with 404 first
		Object status = body.get("status");
        if (status != null && status instanceof Integer && (Integer) status == 404) {
        	return new ModelAndView("404", body);
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
			body.put("exception", builder.toString());

			if (authException instanceof UsernameNotFoundException) {
				String message = ((UsernameNotFoundException) authException).getMessage();

				int idx = message.indexOf("/");
				if (idx > 0) {
					String cvr = message.substring(0, idx);

					Municipality municipality = municipalityService.getByCvr(cvr);
					if (municipality != null) {
						body.put("cause", "USER");
					} else {
						body.put("cause", "ORG");
					}
				}
			}
		}

		if (!body.containsKey("cause")) {
			body.put("cause", "UNKNOWN");
		}

		return new ModelAndView("samlerror", body);
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

	@Override
	public String getErrorPath() {
		return "/_dummyErrorPath";
	}
}