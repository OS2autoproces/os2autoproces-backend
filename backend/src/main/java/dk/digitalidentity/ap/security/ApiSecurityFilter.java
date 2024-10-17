package dk.digitalidentity.ap.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import dk.digitalidentity.ap.dao.ProcessSeenByDao;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import dk.digitalidentity.ap.dao.MunicipalityDao;
import dk.digitalidentity.ap.dao.model.Municipality;
import dk.digitalidentity.samlmodule.model.SamlGrantedAuthority;
import dk.digitalidentity.samlmodule.model.TokenUser;

public class ApiSecurityFilter implements Filter {
	private MunicipalityDao municipalityDao;
	private ProcessSeenByDao processSeenByDao;

	public ApiSecurityFilter(MunicipalityDao municipalityDao, ProcessSeenByDao processSeenByDao) {
		this.municipalityDao = municipalityDao;
		this.processSeenByDao = processSeenByDao;
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		String authHeader = request.getHeader("Authorization");
		if (StringUtils.hasLength(authHeader)) {
			
			// strip Bearer prefix
			if (authHeader.startsWith("Bearer ")) {
				authHeader = authHeader.substring("Bearer ".length());
			}
		}

		if (StringUtils.hasLength(authHeader)) {
			Municipality municipality = municipalityDao.getByApiKey(authHeader);
			if (municipality != null) {
				ArrayList<SamlGrantedAuthority> authorities = new ArrayList<>();
				authorities.add(new SamlGrantedAuthority(SecurityUtil.ROLE_SUPERUSER));

				TokenUser tokenUser = TokenUser.builder().cvr(municipality.getCvr()).authorities(authorities).attributes(new HashMap<>()).username(municipality.getName()).build();
				
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(tokenUser.getUsername(), "N/A", tokenUser.getAuthorities());
				authToken.setDetails(tokenUser);

				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}

		if (!SecurityUtil.isLoggedInAsSystemAccount()) {
			// handle seen by unique users if endpoint is the one used for viewing details for a process
			HttpServletRequest httpRequest = request;
			String path = httpRequest.getRequestURI();
			String projectionParam = httpRequest.getParameter("projection");

			if (path.matches("/backend/api/processes/\\d+") && "extended".equals(projectionParam)) {
				AuthenticatedUser authenticatedUser = SecurityUtil.getUser();
				if (authenticatedUser != null) {
					Pattern pattern = Pattern.compile("/backend/api/processes/(\\d+)");
					Matcher matcher = pattern.matcher(path);

					if (matcher.find()) {
						String id = matcher.group(1);
						Long projectId = Long.parseLong(id);
						processSeenByDao.insertIfNotExists(authenticatedUser.getId(), projectId);
					}
				}
			}
		}

		filterChain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void destroy() {
		;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		;
	}
}