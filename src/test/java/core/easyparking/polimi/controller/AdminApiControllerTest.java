package core.easyparking.polimi.controller;

import core.easyparking.polimi.entity.*;
import core.easyparking.polimi.repository.*;
import core.easyparking.polimi.service.AdminService;
import core.easyparking.polimi.service.PublicService;
import core.easyparking.polimi.utils.object.responce.GetAdminResponce;
import core.easyparking.polimi.utils.object.responce.GetParkingAreaColorResponce;
import core.easyparking.polimi.utils.object.responce.GetParkingAreaTypeDimensionResponce;
import core.easyparking.polimi.utils.object.staticvalues.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@SpringJUnitConfig
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = NONE)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminApiControllerTest {

	@Autowired
	private PublicService publicService;
	@Autowired
	private TestRestTemplate restTemplate;
	@Autowired
	private AdminRepository adminRepository;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private ParkingAreaColorRepository parkingAreaColorRepository;
	@Autowired
	private ParkingAreaRepository parkingAreaRepository;
	@Autowired
	private ParkingAreaTypeDimensionRepository parkingAreaTypeDimensionRepository;
	@Autowired
	private UserRepository userRepository;

	@MockBean private AdminService adminService;

	private final HttpHeaders headers = new HttpHeaders();
	public Admin admin;
	ParkingAreaColor pac = new ParkingAreaColor(Color.Blue,1.60,5.00,15.99, 45.70);
	ParkingAreaTypeDimension patd = new ParkingAreaTypeDimension(Type.Spina, 2.30, 4.90);
	ParkingArea pa = new ParkingArea(pac.getPacId(), patd.getPatdId(), Functionality.Car, 20.87, 34.56, ParkingAreaStatus.Free);

  @BeforeEach
	public void beforeEach() {

		userRepository.deleteAll();
		adminRepository.deleteAll();
		accountRepository.deleteAll();
		String adminMail = "admintestcontroller@mail.com";
		String defaultPassword = "TestPassword123";

		Account account = accountRepository.save(new Account(adminMail, DigestUtils.sha3_256Hex(defaultPassword), Role.Admin));
	  admin = adminRepository.save(new Admin(account.getAccountId(), "adminControllerName", "adminControllerSurname", Status.Pending));
		admin.setAccount(account);
		adminRepository.save(admin);
		pac = parkingAreaColorRepository.save(pac);
		patd = parkingAreaTypeDimensionRepository.save(patd);
		pa.setPatdId(patd.getPatdId());
		pa.setPacId(pac.getPacId());
		pa = parkingAreaRepository.save(pa);

		headers.clear();
		String token = publicService.loginAdmin(adminMail, defaultPassword);
		headers.add("Authorization", "Bearer " + token);
	}

	@Test
	public void getProfile() {
		Admin admin = adminRepository.findAll().get(0);
		given(adminService.getProfile()).willReturn(new GetAdminResponce(admin));
		ResponseEntity<Admin> response200 =
				restTemplate.exchange(
						"/api/easyparking/admin/profile", HttpMethod.GET, new HttpEntity<>(null, headers), Admin.class);
		assertEquals(HttpStatus.OK, response200.getStatusCode());
		assertNotNull(response200.getBody());
	}

	@Test
	public void getAllUsers() {
		ParameterizedTypeReference<List<Driver>> type = new ParameterizedTypeReference<>() {};
		ResponseEntity<List<Driver>> response200 =
				restTemplate.exchange(
						"/api/easyparking/admin/list-users", HttpMethod.GET, new HttpEntity<>(null, headers), type);
		assertEquals(HttpStatus.OK, response200.getStatusCode());
		assertNotNull(response200.getBody());
	}

	@Test
	public void getAllAdmin() {
		ParameterizedTypeReference<List<Admin>> type = new ParameterizedTypeReference<>() {};
		ResponseEntity<List<Admin>> response200 =
				restTemplate.exchange(
						"/api/easyparking/admin/list-admins", HttpMethod.GET, new HttpEntity<>(null, headers), type);
		assertEquals(HttpStatus.OK, response200.getStatusCode());
		assertNotNull(response200.getBody());
	}

	@Test
	public void getAllParkingAreaColor() {
		ParameterizedTypeReference<List<ParkingAreaColor>> type = new ParameterizedTypeReference<>() {};
		ResponseEntity<List<ParkingAreaColor>> response200 =
				restTemplate.exchange(
						"/api/easyparking/admin/list-admins", HttpMethod.GET, new HttpEntity<>(null, headers), type);
		assertEquals(HttpStatus.OK, response200.getStatusCode());
		assertNotNull(response200.getBody());
	}

	@Test
	public void createParkingAreaColor() {
		GetParkingAreaColorResponce pac1 = new GetParkingAreaColorResponce(Color.Blue,1.60,5.00,15.99, 45.70);
		given(adminService.createParkingAreaColor(pac1)).willReturn(pac1);

		ResponseEntity<ParkingAreaColor> response =
				restTemplate.exchange(
						"/api/easyparking/admin/parking-area-color", HttpMethod.POST, new HttpEntity<>(pac, headers),
						ParkingAreaColor.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());

	}

	@Test
	public void updateParkingAreaColor() {
		GetParkingAreaColorResponce pac1 = new GetParkingAreaColorResponce(Color.Blue,1.60,5.00,15.99, 45.70);
		given(adminService.updateParkingAreaColor(pac.getPacId(), pac1)).willReturn(pac1);
		ResponseEntity<ParkingAreaColor> response =
				restTemplate.exchange(
						"/api/easyparking/admin/parking-area-color/{pacId}", HttpMethod.PUT, new HttpEntity<>(pac, headers),
						ParkingAreaColor.class, pac.getPacId());
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void updateHourlyPricePAC() {
		ParkingAreaColor pac1 = new ParkingAreaColor(Color.Blue,1.60,5.00,15.99, 45.70);
		given(adminService.updateHourlyPricePAC(any(), any())).willReturn(new GetParkingAreaColorResponce(pac1));

		ResponseEntity<ParkingAreaColor> response =
				restTemplate.exchange(
						"/api/easyparking/admin/pac/hourly-price/{pacId}/?hourlyPrice={hourlyPrice}", HttpMethod.PUT, new HttpEntity<>(null, headers),
						ParkingAreaColor.class, pac.getPacId(), pac.getHourlyPrice());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
	}

	@Test
	public void updateDailyPricePAC() {
		ParkingAreaColor pac1 = new ParkingAreaColor(Color.Blue,1.60,5.00,15.99, 45.70);
		given(adminService.updateDailyPricePAC(any(), any())).willReturn(new GetParkingAreaColorResponce(pac1));
		ResponseEntity<ParkingAreaColor> response =
				restTemplate.exchange(
						"/api/easyparking/admin/pac/daily-price/{pacId}/?dailyPrice={dailyPrice}", HttpMethod.PUT, new HttpEntity<>(null, headers),
						ParkingAreaColor.class, pac.getPacId(), pac.getDailyPrice());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
	}

	@Test
	public void updateWeeklyPricePAC() {
		ParkingAreaColor pac1 = new ParkingAreaColor(Color.Blue,1.60,5.00,15.99, 45.70);
		given(adminService.updateWeeklyPricePAC(any(), any())).willReturn(new GetParkingAreaColorResponce(pac1));
		ResponseEntity<ParkingAreaColor> response =
				restTemplate.exchange(
						"/api/easyparking/admin/pac/weekly-price/{pacId}/?weeklyPrice={weeklyPrice}", HttpMethod.PUT, new HttpEntity<>(null, headers),
						ParkingAreaColor.class, pac.getPacId(), pac.getWeeklyPrice());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
	}

	@Test
	public void updateMonthlyPricePAC() {
		ParkingAreaColor pac1 = new ParkingAreaColor(Color.Blue,1.60,5.00,15.99, 45.70);
		given(adminService.updateMonthlyPricePAC(any(), any())).willReturn(new GetParkingAreaColorResponce(pac1));
		ResponseEntity<ParkingAreaColor> response =
				restTemplate.exchange(
						"/api/easyparking/admin/pac/monthly-price/{pacId}/?monthlyPrice={monthlyPrice}", HttpMethod.PUT, new HttpEntity<>(null, headers),
						ParkingAreaColor.class, pac.getPacId(), pac.getMonthlyPrice());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
	}

	@Test
	public void setFreePAC() {
		given(adminService.setFreePAC(any())).willReturn(new GetParkingAreaColorResponce());
		ResponseEntity<ParkingAreaColor> response =
				restTemplate.exchange(
						"/api/easyparking/admin/pac/zero-price/{pacId}", HttpMethod.PUT, new HttpEntity<>(null, headers),
						ParkingAreaColor.class, pac.getPacId());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
	}

	@Test
	public void getAllParkingAreaTypeDimension() {
		ParameterizedTypeReference<List<ParkingAreaTypeDimension>> type = new ParameterizedTypeReference<>() {};
		ResponseEntity<List<ParkingAreaTypeDimension>> response200 =
				restTemplate.exchange(
						"/api/easyparking/admin/parking-area-type-dimension", HttpMethod.GET, new HttpEntity<>(null, headers), type);
		assertEquals(HttpStatus.OK, response200.getStatusCode());
		assertNotNull(response200.getBody());
	}

	@Test
	public void createParkingAreaTypeDimensions() {
		GetParkingAreaTypeDimensionResponce patd1 = new GetParkingAreaTypeDimensionResponce(Type.Spina, 2.30, 4.90);
		given(adminService.createParkingAreaTypeDimensions(any())).willReturn(patd1);
		ResponseEntity<ParkingAreaTypeDimension> response =
				restTemplate.exchange(
						"/api/easyparking/admin/parking-area-type-dimension", HttpMethod.POST, new HttpEntity<>(patd, headers),
						ParkingAreaTypeDimension.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
	}

	@Test
	public void updateParkingAreaTypeDimension() {
		GetParkingAreaTypeDimensionResponce patd1 = new GetParkingAreaTypeDimensionResponce(Type.Spina, 2.30, 4.90);
		given(adminService.updateParkingAreaTypeDimension(any(),any())).willReturn(patd1);
		ResponseEntity<ParkingAreaTypeDimension> response =
				restTemplate.exchange(
						"/api/easyparking/admin/parking-area-type-dimension/{patdId}", HttpMethod.PUT, new HttpEntity<>(patd, headers),
						ParkingAreaTypeDimension.class, patd.getPatdId());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
	}

	@Test
	public void getAllParkingArea() {
		ParameterizedTypeReference<List<ParkingArea>> type = new ParameterizedTypeReference<>() {};
		ResponseEntity<List<ParkingArea>> response200 =
				restTemplate.exchange(
						"/api/easyparking/admin/parking-area", HttpMethod.GET, new HttpEntity<>(null, headers), type);
		assertEquals(HttpStatus.OK, response200.getStatusCode());
		assertNotNull(response200.getBody());
	}

	@Test
	public void createParkingArea() {
		given(adminService.createParkingArea(any())).willReturn(pa);
		ResponseEntity<ParkingArea> response =
				restTemplate.exchange(
						"/api/easyparking/admin/parking-area", HttpMethod.POST, new HttpEntity<>(pa, headers),
						ParkingArea.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
	}

	@Test
	public void updateParkingArea() {
		given(adminService.updateParkingArea(any(), any())).willReturn(pa);
		ResponseEntity<ParkingArea> response =
				restTemplate.exchange(
						"/api/easyparking/admin/parking-area/{paId}", HttpMethod.PUT, new HttpEntity<>(pa, headers),
						ParkingArea.class, pa.getPaId());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
	}

	@Test
	public void setPADamaged() {
		given(adminService.setPADamaged(any())).willReturn(pa);
		ResponseEntity<ParkingArea> response =
				restTemplate.exchange(
						"/api/easyparking/admin/parking-area/status/{paId}", HttpMethod.PUT, new HttpEntity<>(null, headers),
						ParkingArea.class, pa.getPaId());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
	}

	@Test
	public void deleteParkingArea() {
		ResponseEntity<String> response =
				restTemplate.exchange(
						"/api/easyparking/admin/parking-area/{paId}", HttpMethod.DELETE, new HttpEntity<>(null, headers),
						String.class, pa.getPaId());
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void deleteParkingAreaTypeDimension() {
		ResponseEntity<String> response =
				restTemplate.exchange(
						"/api/easyparking/admin/parking-area-type-dimension/{patdId}", HttpMethod.DELETE, new HttpEntity<>(null, headers),
						String.class, patd.getPatdId());
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void deleteParkingAreaColor() {
		ResponseEntity<String> response =
				restTemplate.exchange(
						"/api/easyparking/admin/parking-area-color/{pacId}", HttpMethod.DELETE, new HttpEntity<>(null, headers),
						String.class, pac.getPacId());
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

}
