package core.easyparking.polimi.service.jwt;

import core.easyparking.polimi.configuration.mail.CustomMailSender;
import core.easyparking.polimi.entity.Account;
import core.easyparking.polimi.repository.AccountRepository;
import core.easyparking.polimi.utils.object.staticvalues.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JWTAuthenticationService {
	private final JWTService jwtService;
	private final CustomMailSender customMailSender;
	private final AccountRepository accountRepository;

	public String login(Role role, String username, String password) throws BadCredentialsException {
		if (accountRepository.findByRoleAndUsernameAndPassword(role, username, password).isPresent()) {
			return accountRepository
					.findByRoleAndUsernameAndPassword(role, username, password)
					.map(user -> jwtService.create(role, username, password))
					.orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
		} else if (accountRepository.findByRoleAndUsernameAndResetCode(role, username, password).isPresent()) {
			Account account = accountRepository.findByRoleAndUsernameAndResetCode(role, username, password)
					.orElseThrow(() -> new BadCredentialsException("Invalid username or password."));
			if (LocalDateTime.now().isBefore(account.getDateReset())) {
				account.setPassword(password);
				accountRepository.save(account);
				return jwtService.create(role, username, password);
			} else throw new BadCredentialsException("Change password expired. Get a new email with password");
		}
		throw new BadCredentialsException("Invalid username o password");
	}

	/**
	 * Allows admin/user to reset their password
	 * @param role: identifies 'Admin' or 'User' role
	 * @param username: the username to identify admin/user that wants proceeded with reset
	 */
	public void reset(Role role, String username) throws BadCredentialsException {
		accountRepository
				.findByRoleAndUsername(role, username)
				.ifPresentOrElse(
						account -> {
							String newPassword = RandomString.make(10);
							switch (role) {
								case User: {
									if (!customMailSender.sendResetUser(username, Map.of("password", newPassword))) {
										throw new RuntimeException("Something went wrong with email");
									}else{
										account.setDateReset(LocalDateTime.now().plusHours(26));
										account.setResetCode(DigestUtils.sha3_256Hex(newPassword));
										accountRepository.save(account);
									}
									break;
								}
								case Admin: {
									if (!customMailSender.sendResetAdmin(username, Map.of("password", newPassword))) {
										throw new RuntimeException("Something went wrong with email");
									}else{
										account.setDateReset(LocalDateTime.now().plusHours(26));
										account.setResetCode(DigestUtils.sha3_256Hex(newPassword));
										accountRepository.save(account);
									}
									break;
								}
								default:
									throw new RuntimeException("Something went wrong with reset");
							}
						},
						() -> {
							throw new BadCredentialsException("Invalid email");
						});
	}

	public Account authenticateByToken(String token) {
		try {
			Map<String, Object> data = jwtService.verify(token);
			Role role = Role.valueOf(String.valueOf(data.get("role")));
			String username = String.valueOf(data.get("username"));
			String password = String.valueOf(data.get("password"));
			return accountRepository.findByRoleAndUsernameAndPassword(role, username, password)
					.orElseThrow(() -> new UsernameNotFoundException("Authentication fail"));
		} catch (Exception e) {
			throw new BadCredentialsException("Invalid token");
		}
	}
}
