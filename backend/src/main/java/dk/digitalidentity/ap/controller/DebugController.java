package dk.digitalidentity.ap.controller;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.opensaml.saml2.core.Assertion;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.w3c.dom.Element;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
public class DebugController {

	@RequestMapping(value = { "/debug" })
	public String index(Model model, HttpServletRequest request) {
		List<String> headers = new ArrayList<>();
		Enumeration<String> headerNames = request.getHeaderNames();

		try {
			if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				Object credentials = authentication.getCredentials();

				if (credentials != null && credentials instanceof SAMLCredential) {
					SAMLCredential saml = (SAMLCredential) credentials;

					Assertion assertion = saml.getAuthenticationAssertion();
					Element element = assertion.getDOM();

					Source source = new DOMSource(element);
					TransformerFactory transFactory = TransformerFactory.newInstance();
					Transformer transformer = transFactory.newTransformer();
					StringWriter buffer = new StringWriter();

					transformer.setOutputProperty(OutputKeys.METHOD, "xml");
					transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
					transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
					transformer.setOutputProperty(OutputKeys.INDENT, "yes");
					transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
					transformer.transform(source, new StreamResult(buffer));

					model.addAttribute("token", buffer.toString());
				}
			}
		} catch (Exception ex) {
			log.warn("Failed to parse SAML token", ex);
		}

		while (headerNames.hasMoreElements()) {
			String header = headerNames.nextElement();

			StringBuilder builder = new StringBuilder();
			Enumeration<String> headers2 = request.getHeaders(header);
			while (headers2.hasMoreElements()) {
				String headerValue = headers2.nextElement();

				if (builder.length() > 0) {
					builder.append(",");
				}
				builder.append(headerValue);
			}

			headers.add(header + " = " + builder.toString());
		}

		model.addAttribute("headers", headers);

		return "debug";
	}
}