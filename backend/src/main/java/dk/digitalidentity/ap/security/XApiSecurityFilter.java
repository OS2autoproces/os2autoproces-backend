package dk.digitalidentity.ap.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import dk.digitalidentity.ap.dao.MunicipalityDao;
import dk.digitalidentity.ap.dao.model.Municipality;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XApiSecurityFilter implements Filter {
	private MunicipalityDao municipalityDao;

	public XApiSecurityFilter(MunicipalityDao municipalityDao) {
		this.municipalityDao = municipalityDao;
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		String authHeader = request.getHeader("ApiKey");		
		if (authHeader != null) {
			Municipality municipality = municipalityDao.getByApiKey(authHeader);
			if (municipality == null) {
				unauthorized(response, "Invalid ApiKey header", authHeader);
				return;
			}

			ArrayList<GrantedAuthority> authorities = new ArrayList<>();
			authorities.add(new SimpleGrantedAuthority(SecurityUtil.ROLE_SUPERUSER));

			TokenClient token = new TokenClient(municipality.getName(), municipality.getApiKey(), authorities);
			token.setCvr(municipality.getCvr());

			SecurityContextHolder.getContext().setAuthentication(token);
			filterChain.doFilter(servletRequest, servletResponse);
		}
		else {
			unauthorized(response, "Missing ApiKey header", authHeader);
		}
	}

	private static void unauthorized(HttpServletResponse response, String message, String authHeader) throws IOException {
		log.warn(message + " (authHeader = " + authHeader + ")");
		response.sendError(401, message);
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