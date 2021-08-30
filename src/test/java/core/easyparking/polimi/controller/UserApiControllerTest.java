package core.easyparking.polimi.controller;

import core.easyparking.polimi.entity.*;
import core.easyparking.polimi.repository.*;
import core.easyparking.polimi.service.PublicService;
import core.easyparking.polimi.service.UserService;
import core.easyparking.polimi.utils.object.request.GetParkingAreaRequest;
import core.easyparking.polimi.utils.object.responce.AddVehicleResponce;
import core.easyparking.polimi.utils.object.responce.GetFineListResponce;
import core.easyparking.polimi.utils.object.responce.GetUserResponce;
import core.easyparking.polimi.utils.object.staticvalues.Functionality;
import core.easyparking.polimi.utils.object.staticvalues.Role;
import core.easyparking.polimi.utils.object.staticvalues.Status;
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
public class UserApiControllerTest {

	@Autowired
	private PublicService publicService;
	@Autowired
	private TestRestTemplate restTemplate;
	@Autowired
	private AdminRepository adminRepository;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private ModelVehicleRepository modelVehicleRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private VehicleRepository vehicleRepository;
	@MockBean
	private UserService userService;
	private final HttpHeaders headers = new HttpHeaders();
	public Admin admin;
	public Driver user;
	Vehicle vehicle;
	ModelVehicle mv1;

  @BeforeEach
	public void beforeEach() {

		userRepository.deleteAll();
		adminRepository.deleteAll();
		accountRepository.deleteAll();
		String adminMail = "admintestcontroller@mail.com";
		String userMail = "usertestcontroller@mail.com";
		String defaultPassword = "TestPassword123";

		Account userAccount = accountRepository.save(new Account(userMail, DigestUtils.sha3_256Hex(defaultPassword), Role.User));
		user = userRepository.save(new Driver(userAccount.getAccountId(), "userControllerName", "userControllerSurname", Status.Pending));
		user.setAccount(userAccount);
		mv1 = modelVehicleRepository.save(new ModelVehicle("FIAT", "panda", 1999, 1000, Functionality.Car, 1.78, 1.56));
		vehicle = vehicleRepository.save(new Vehicle(mv1.getMvId(), user.getUserId(), "AB123ET"));

		Account account = accountRepository.save(new Account(adminMail, DigestUtils.sha3_256Hex(defaultPassword), Role.Admin));
	  admin = adminRepository.save(new Admin(account.getAccountId(), "adminControllerName", "adminControllerSurname", Status.Pending));
		admin.setAccount(account);
		adminRepository.save(admin);

		headers.clear();
		String token = publicService.loginUser(userMail, defaultPassword);
		headers.add("Authorization", "Bearer " + token);
	}

	@Test
	public void getProfile() {
		Driver user = userRepository.findAll().get(0);
		given(userService.getProfile()).willReturn(new GetUserResponce(user));
		ResponseEntity<Driver> response200 =
				restTemplate.exchange(
						"/api/easyparking/user/profile", HttpMethod.GET, new HttpEntity<>(null, headers), Driver.class);
		assertEquals(HttpStatus.OK, response200.getStatusCode());
		assertNotNull(response200.getBody());
	}

	@Test
	public void addVehicle() {
		AddVehicleResponce vehicleTest = new AddVehicleResponce(vehicle);
		given(userService.addVehicle(any(), any())).willReturn(vehicleTest);

		ResponseEntity<Vehicle> response200 =
				restTemplate.exchange(
						"/api/easyparking/user/add-vehicle?mvId={mvId}&licensePlate={licensePlate}", HttpMethod.POST, new HttpEntity<>(null, headers), Vehicle.class,
						1L, "AS234RT");
		assertEquals(HttpStatus.OK, response200.getStatusCode());
		assertNotNull(response200.getBody());
	}

	@Test
	public void updateMV() {
		AddVehicleResponce vehicleTest = new AddVehicleResponce(vehicle);
		given(userService.updateMV(any(), any())).willReturn(vehicleTest);

		ResponseEntity<Vehicle> response200 =
				restTemplate.exchange(
						"/api/easyparking/user/vehicles-mv/{vehicleId}/?mvId={mvId}", HttpMethod.PUT, new HttpEntity<>(null, headers),
						Vehicle.class, vehicle.getVehicleId(), vehicle.getMvId());
		assertEquals(HttpStatus.OK, response200.getStatusCode());
		assertNotNull(response200.getBody());
	}

	@Test
	public void getVehicles() {
		ParameterizedTypeReference<List<Vehicle>> type = new ParameterizedTypeReference<>() {};
		ResponseEntity<List<Vehicle>> response200 =
				restTemplate.exchange(
						"/api/easyparking/user/get-vehicles", HttpMethod.GET, new HttpEntity<>(null, headers), type);
		assertEquals(HttpStatus.OK, response200.getStatusCode());
		assertNotNull(response200.getBody());
	}

	@Test
	public void getPAFilteredByZ() {
		GetParkingAreaRequest getParkingArea = new GetParkingAreaRequest(Functionality.Car, 0.0, 20.0, 21.0, 35.8, 1L, 1L, 1L);

		ParameterizedTypeReference<List<ParkingArea>> type = new ParameterizedTypeReference<>() {};
		ResponseEntity<List<ParkingArea>> response200 =
				restTemplate.exchange(
						"/api/easyparking/user/parking-area/z", HttpMethod.POST, new HttpEntity<>(getParkingArea, headers), type);
		assertEquals(HttpStatus.OK, response200.getStatusCode());
		assertNotNull(response200.getBody());
	}

	@Test
	public void getPAFilteredByZF() {
		GetParkingAreaRequest getParkingArea = new GetParkingAreaRequest(Functionality.Car, 0.0, 20.0, 21.0, 35.8, 1L, 1L, 1L);

		ParameterizedTypeReference<List<ParkingArea>> type = new ParameterizedTypeReference<>() {};
		ResponseEntity<List<ParkingArea>> response200 =
				restTemplate.exchange(
						"/api/easyparking/user/parking-area/z-f", HttpMethod.POST, new HttpEntity<>(getParkingArea, headers), type);
		assertEquals(HttpStatus.OK, response200.getStatusCode());
		assertNotNull(response200.getBody());
	}

	@Test
	public void getPAFilteredByZFC() {
		GetParkingAreaRequest getParkingArea = new GetParkingAreaRequest(Functionality.Car,0.0, 20.0, 21.0, 35.8, 1L, 1L, 1L);

		ParameterizedTypeReference<List<ParkingArea>> type = new ParameterizedTypeReference<>() {};
		ResponseEntity<List<ParkingArea>> response200 =
				restTemplate.exchange(
						"/api/easyparking/user/parking-area/z-f-c", HttpMethod.POST, new HttpEntity<>(getParkingArea, headers), type);
		assertEquals(HttpStatus.OK, response200.getStatusCode());
		assertNotNull(response200.getBody());
	}

	@Test
	public void getPAFilteredByZFCT() {
		GetParkingAreaRequest getParkingArea = new GetParkingAreaRequest(Functionality.Car, 0.0, 20.0, 21.0, 35.8, 1L, 1L, 1L);

		ParameterizedTypeReference<List<ParkingArea>> type = new ParameterizedTypeReference<>() {};
		ResponseEntity<List<ParkingArea>> response200 =
				restTemplate.exchange(
						"/api/easyparking/user/parking-area/z-f-c-t", HttpMethod.POST, new HttpEntity<>(getParkingArea, headers), type);
		assertEquals(HttpStatus.OK, response200.getStatusCode());
		assertNotNull(response200.getBody());
	}

	@Test
	public void getPAFilteredByZFCTV() {

		vehicle = vehicleRepository.findByLicensePlate("AB123ET")
				.orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
		GetParkingAreaRequest getParkingArea = new GetParkingAreaRequest(Functionality.Car, 0.0, 20.0, 21.0, 35.8, vehicle.getVehicleId(), 1L, 1L);

		ParameterizedTypeReference<List<ParkingArea>> type = new ParameterizedTypeReference<>() {};
		ResponseEntity<List<ParkingArea>> response200 =
				restTemplate.exchange(
						"/api/easyparking/user/parking-area/z-f-c-t-v", HttpMethod.POST, new HttpEntity<>(getParkingArea, headers), type);
		assertEquals(HttpStatus.OK, response200.getStatusCode());
		assertNotNull(response200.getBody());
	}

	@Test
	public void getModelsVehicle() {

		ParameterizedTypeReference<List<ModelVehicle>> type = new ParameterizedTypeReference<>() {};
		ResponseEntity<List<ModelVehicle>> response200 =
				restTemplate.exchange(
						"/api/easyparking/user/get-vehiclemodels", HttpMethod.GET, new HttpEntity<>(null, headers), type);
		assertEquals(HttpStatus.OK, response200.getStatusCode());
		assertNotNull(response200.getBody());
	}

	@Test
	public void cancelTicketPayment() {

		ResponseEntity<String> response200 =
				restTemplate.exchange(
						"/api/easyparking/user/cancel-ticket", HttpMethod.GET, new HttpEntity<>(null, headers), String.class);
		assertEquals(HttpStatus.OK, response200.getStatusCode());
		assertNotNull(response200.getBody());
	}

	@Test
	public void cancelFinePayment() {

		ResponseEntity<String> response200 =
				restTemplate.exchange(
						"/api/easyparking/user/cancel-fine", HttpMethod.GET, new HttpEntity<>(null, headers), String.class);
		assertEquals(HttpStatus.OK, response200.getStatusCode());
		assertNotNull(response200.getBody());
	}

	@Test
	public void getTickets() {
		ParameterizedTypeReference<List<GetFineListResponce>> type = new ParameterizedTypeReference<>() {};

		ResponseEntity<List<GetFineListResponce>> response200 =
				restTemplate.exchange(
						"/api/easyparking/user/get-tickets", HttpMethod.GET, new HttpEntity<>(null, headers), type);
		assertEquals(HttpStatus.OK, response200.getStatusCode());
		assertNotNull(response200.getBody());
	}

	@Test
	public void getFines() {
		ParameterizedTypeReference<List<GetFineListResponce>> type = new ParameterizedTypeReference<>() {};

		ResponseEntity<List<GetFineListResponce>> response200 =
				restTemplate.exchange(
						"/api/easyparking/user/get-fines", HttpMethod.GET, new HttpEntity<>(null, headers), type);
		assertEquals(HttpStatus.OK, response200.getStatusCode());
		assertNotNull(response200.getBody());
	}

	@Test
	public void getFinesNotPaid() {
		ParameterizedTypeReference<List<GetFineListResponce>> type = new ParameterizedTypeReference<>() {};

		ResponseEntity<List<GetFineListResponce>> response200 =
				restTemplate.exchange(
						"/api/easyparking/user/get-fines-n-p", HttpMethod.GET, new HttpEntity<>(null, headers), type);
		assertEquals(HttpStatus.OK, response200.getStatusCode());
		assertNotNull(response200.getBody());
	}

	@Test
	public void getFinesPaid() {
		ParameterizedTypeReference<List<GetFineListResponce>> type = new ParameterizedTypeReference<>() {};

		ResponseEntity<List<GetFineListResponce>> response200 =
				restTemplate.exchange(
						"/api/easyparking/user/get-fines-p", HttpMethod.GET, new HttpEntity<>(null, headers), type);
		assertEquals(HttpStatus.OK, response200.getStatusCode());
		assertNotNull(response200.getBody());
	}
}
