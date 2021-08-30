package core.easyparking.polimi.security;

import core.easyparking.polimi.service.jwt.JWTAuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TokenAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
	private final JWTAuthenticationService jwtAuthenticationService;

	@Override
	protected void additionalAuthenticationChecks(UserDetails details, UsernamePasswordAuthenticationToken credentials) { }

	@Override
	public UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) {
		Object token = authentication.getCredentials();
		return Optional
				.ofNullable(token)
				.flatMap(t ->
						Optional.of(jwtAuthenticationService.authenticateByToken(String.valueOf(t)))
								.map(u -> User.builder()
									.username(u.getUsername())
									.password(u.getPassword())
									.authorities(u.getRole().name())
									.build()))
				.orElseThrow(() -> new BadCredentialsException("Invalid authentication token=" + token));
	}
}
