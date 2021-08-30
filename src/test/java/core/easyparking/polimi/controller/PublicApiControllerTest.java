package core.easyparking.polimi.controller;

import core.easyparking.polimi.service.PublicService;
import core.easyparking.polimi.utils.object.request.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PublicApiControllerTest {

	@Autowired private TestRestTemplate restTemplate;

	@MockBean private PublicService publicService;

	private final String userMail = "userPublicController@mail.com";
	private final String adminMail = "adminPublicController@mail.com";
	private final String defaultPassword = "TestPassword123";

	@Test
	void register() {
		String token = "test";
		given(publicService.register(any())).willReturn(token);
		RegisterRequest registerRequest = new RegisterRequest();

		ResponseEntity<String> response =
				restTemplate.exchange(
						"/api/easyparking/register/user", HttpMethod.POST, new HttpEntity<>(registerRequest), String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(token, response.getBody());
	}

	@Test
	void loginUser() {
		String token = "test";
		given(publicService.loginUser(any(), any())).willReturn(token);

		ResponseEntity<String> response =
				restTemplate.exchange(
						"/api/easyparking/login/user?username={username}&password={password}", HttpMethod.POST, null,
						String.class, userMail, defaultPassword);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(token, response.getBody());
	}

	@Test
	void loginAdmin() {
		String token = "test";
		given(publicService.loginAdmin(any(), any())).willReturn(token);

		ResponseEntity<String> response =
				restTemplate.exchange(
						"/api/easyparking/login/admin?username={username}&password={password}", HttpMethod.POST, null,
						String.class, adminMail, defaultPassword);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(token, response.getBody());
	}


	@Test
	void resetUser() {
		ResponseEntity<String> response =
				restTemplate.exchange(
						"/api/easyparking/reset/user?username={username}", HttpMethod.POST, null,
						String.class, userMail);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
/*
	@Test
	void resetAdmin() {
		ResponseEntity<String> response =
				restTemplate.exchange(
						"/api/easyparking/reset/admin?username={username}", HttpMethod.POST, null,
						String.class, adminMail);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	*/
}
