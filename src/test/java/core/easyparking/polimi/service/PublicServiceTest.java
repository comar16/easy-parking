package core.easyparking.polimi.service;

import core.easyparking.polimi.entity.Account;
import core.easyparking.polimi.entity.Admin;
import core.easyparking.polimi.entity.Driver;
import core.easyparking.polimi.repository.AccountRepository;
import core.easyparking.polimi.repository.AdminRepository;
import core.easyparking.polimi.repository.UserRepository;
import core.easyparking.polimi.utils.object.request.RegisterRequest;
import core.easyparking.polimi.utils.object.staticvalues.Role;
import core.easyparking.polimi.utils.object.staticvalues.Status;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@Transactional
@SpringJUnitConfig
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = NONE)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ComponentScan({"core.easyparking.polimi.service", "core.easyparking.polimi.configuration.mail"})
public class PublicServiceTest {
	@Autowired
	public PublicService publicService;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AdminRepository adminRepository;


	private final String userMail = "userPublicService@mail.com";
	private final String adminMail = "adminPublicService@mail.com";
	private final String defaultPassword = "TestPassword123";

	@BeforeEach
	public void beforeEach() {
		accountRepository.deleteAll();

		Account userAccount = accountRepository.save(new Account(userMail, DigestUtils.sha3_256Hex(defaultPassword), Role.User));
		userRepository.save(new Driver(userAccount.getAccountId(), "NameUser", "SurnameUser", Status.Pending));
		Account adminAccount = accountRepository.save(new Account(adminMail, DigestUtils.sha3_256Hex(defaultPassword), Role.Admin));
		adminRepository.save(new Admin(adminAccount.getAccountId(), "NameAdmin", "SurnameAdmin", Status.Pending));

	}

	@Test
	void register() {
		String customMail = "provaprova.prova@mail.com";
		RegisterRequest registerRequest = new RegisterRequest(customMail, defaultPassword, "Mario", "Rossi");

		publicService.register(registerRequest);

		assertEquals(3, accountRepository.findAll().size());
		HashSet<String> accountEmails = accountRepository.findAll().stream().map(Account::getUsername).collect(Collectors.toCollection(HashSet::new));

		assertEquals(Set.of(customMail, userMail, adminMail), accountEmails);
	}

	@Test
	void loginUser() {
		String token = publicService.loginUser(userMail, defaultPassword);
		assertNotNull(token);
	}

	@Test
	void loginAdmin() {
		String token = publicService.loginAdmin(adminMail, defaultPassword);
		assertNotNull(token);
	}

}
