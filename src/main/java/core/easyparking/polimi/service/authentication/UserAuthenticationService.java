package core.easyparking.polimi.service.authentication;

import core.easyparking.polimi.entity.Account;
import core.easyparking.polimi.entity.Driver;
import core.easyparking.polimi.repository.AccountRepository;
import core.easyparking.polimi.repository.UserRepository;
import core.easyparking.polimi.service.jwt.JWTAuthenticationService;
import core.easyparking.polimi.service.jwt.JWTService;
import core.easyparking.polimi.utils.object.staticvalues.Role;
import core.easyparking.polimi.utils.object.staticvalues.Status;
import core.easyparking.polimi.utils.object.request.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import static core.easyparking.polimi.utils.object.request.RegisterRequest.validateRegisterRequestJsonFields;


@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserAuthenticationService {
	private final JWTService jwtService;
	private final AccountRepository accountRepository;
	private final UserRepository userRepository;
	private final JWTAuthenticationService authenticationService;

	/**
	 * Allows user to login the 'easy parking' system
	 * @param username: the username to identify user that wants log
	 * @param password: the password to identify user that wants log
	 * @return a String: jwt authorization to can log
	 */
	public String login(String username, String password) throws BadCredentialsException {
		if (accountRepository.findByRoleAndUsernameAndPassword(Role.User, username, password).isPresent()) {
			return accountRepository
					.findByRoleAndUsernameAndPassword(Role.User, username, password)
					.map(user -> jwtService.create(Role.User, username, password))
					.orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
		} else if (accountRepository.findByRoleAndUsernameAndResetCode(Role.User, username, password).isPresent()) {
			Account account = accountRepository.findByRoleAndUsernameAndResetCode(Role.User, username, password)
					.orElseThrow(() -> new BadCredentialsException("Invalid username or password."));
			if (LocalDateTime.now().isBefore(account.getDateReset().minusHours(2))) {
				account.setPassword(password);
				accountRepository.save(account);
				return jwtService.create(Role.User, username, password);
			} else throw new BadCredentialsException("Change password expired. Get a new email with password");
		}
		throw new BadCredentialsException("Invalid username o password");
	}

	public void reset(String username) {
		authenticationService.reset(Role.User, username);
	}

	/**
	 * Allows user to register into 'easy parking' system
	 * @param registerRequest: json data retrieved from body to complete request
	 * @return a String: jwt authorization to can log
	 */
	public String register(RegisterRequest registerRequest) {

		// Validate json body
		if (!validateRegisterRequestJsonFields(registerRequest))
			throw new IllegalArgumentException("Invalid json body");
		// Existing account validate
		accountRepository.findByUsername(registerRequest.getUsername()).ifPresent((a) -> {
			throw new IllegalArgumentException("Email already used");});
		// Save all
		Role role = Role.User;
		String shaPassword = DigestUtils.sha3_256Hex(registerRequest.getPassword());
		Account account = accountRepository.save(
				new Account(registerRequest.getUsername(), shaPassword, role));
		userRepository.save(
				new Driver(account.getAccountId(), registerRequest.getName(), registerRequest.getSurname(), Status.Pending));
		return authenticationService.login(role, registerRequest.getUsername(), shaPassword);
	}

}
