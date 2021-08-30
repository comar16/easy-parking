package core.easyparking.polimi.service.authentication;

import core.easyparking.polimi.entity.Account;
import core.easyparking.polimi.service.jwt.JWTAuthenticationService;
import core.easyparking.polimi.utils.object.staticvalues.Role;
import lombok.RequiredArgsConstructor;
import core.easyparking.polimi.repository.AccountRepository;
import core.easyparking.polimi.service.jwt.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AdminAuthenticationService {
	private final JWTService jwtService;
	private final AccountRepository accountRepository;
	private final JWTAuthenticationService authenticationService;

	/**
	 * Allows admin to login the 'easy parking' system
	 * @param username: the username to identify admin that wants log
	 * @param password: the password to identify admin that wants log
	 * @return a String: jwt authorization to can log
	 */
	public String login(String username, String password) throws BadCredentialsException {
		if (accountRepository.findByRoleAndUsernameAndPassword(Role.Admin, username, password).isPresent()) {
			return accountRepository
					.findByRoleAndUsernameAndPassword(Role.Admin, username, password)
					.map(user -> jwtService.create(Role.Admin, username, password))
					.orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
		} else if (accountRepository.findByRoleAndUsernameAndResetCode(Role.Admin, username, password).isPresent()) {
			Account account = accountRepository.findByRoleAndUsernameAndResetCode(Role.Admin, username, password)
					.orElseThrow(() -> new BadCredentialsException("Invalid username or password."));
			if (LocalDateTime.now().isBefore(account.getDateReset().minusHours(2))) {
				account.setPassword(password);
				accountRepository.save(account);
				return jwtService.create(Role.Admin, username, password);
			} else throw new BadCredentialsException("Change password expired. Get a new email with password");
		}
		throw new BadCredentialsException("Invalid username o password");
	}

	public void reset(String username) {
		authenticationService.reset(Role.Admin, username);
	}

}
