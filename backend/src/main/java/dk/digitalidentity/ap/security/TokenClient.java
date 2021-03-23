package dk.digitalidentity.ap.security;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
public class TokenClient extends UsernamePasswordAuthenticationToken {

	@Getter
	@Setter
	private String cvr;

	public TokenClient(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
		super(principal, credentials, authorities);
	}
}
