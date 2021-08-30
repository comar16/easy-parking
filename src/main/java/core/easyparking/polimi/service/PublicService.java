package core.easyparking.polimi.service;

import core.easyparking.polimi.utils.object.request.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import core.easyparking.polimi.service.authentication.AdminAuthenticationService;
import core.easyparking.polimi.service.authentication.UserAuthenticationService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PublicService {
	private final UserAuthenticationService userAuthenticationService;
	private final AdminAuthenticationService adminAuthenticationService;

	public String register(RegisterRequest data) {
		return userAuthenticationService.register(data);
	}

	public String loginUser(String username, String password) {
		return userAuthenticationService.login(username, DigestUtils.sha3_256Hex(password));
	}

	public String loginAdmin(String username, String password) {
		return adminAuthenticationService.login(username, DigestUtils.sha3_256Hex(password));
	}

	public void resetUser(String username) {
		userAuthenticationService.reset(username);
	}

	public void resetAdmin(String username) {
		adminAuthenticationService.reset(username);
	}
}
