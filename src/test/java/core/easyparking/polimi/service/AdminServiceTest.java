package core.easyparking.polimi.service;

import core.easyparking.polimi.entity.*;
import core.easyparking.polimi.repository.*;
import core.easyparking.polimi.utils.object.request.ParkingAreaRequest;
import core.easyparking.polimi.utils.object.responce.*;
import core.easyparking.polimi.utils.object.staticvalues.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@Transactional
@SpringJUnitConfig
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = NONE)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ComponentScan({"core.easyparking.polimi.service", "core.easyparking.polimi.configuration.mail"})
public class AdminServiceTest {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private FineRepository fineRepository;
    @Autowired
    private LicenseRepository licenseRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ModelVehicleRepository modelVehicleRepository;
    @Autowired
    private ParkingAreaColorRepository parkingAreaColorRepository;
    @Autowired
    private ParkingAreaRepository parkingAreaRepository;
    @Autowired
    private ParkingAreaTypeDimensionRepository parkingAreaTypeDimensionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private AdminService adminService;


    private Admin admin;
    private Driver user;
    private final String adminUsername = "admintestservice@mail.com";
    private final String defaultPasswordSha3 = "363999f7918bb84260f481cceaed396fb046e8dc25750c5c3ae0e8088ae17b22";
    private final LocalDateTime now = LocalDateTime.now().withNano(0);
    ParkingAreaColor pac = new ParkingAreaColor(Color.Blue,1.60,5.00,15.99, 45.70);
    ParkingAreaTypeDimension patd = new ParkingAreaTypeDimension(Type.Nastro, 12.20, 11.30);
    ParkingArea pa = new ParkingArea(pac.getPacId(), patd.getPatdId(), Functionality.Car, 20.87, 34.56, ParkingAreaStatus.Free);
    /*Vehicle vehicle = new Vehicle(Long.valueOf(1), user.getUserId(), "AS2334RT");
    Fine fine = new Fine(user.getUserId(), pa.getPaId(), null, vehicle.getVehicleId(),
            "Tow away zone",
            LocalDateTime.parse("2021-08-21 02:13:03"),
            Integer.valueOf(0),
            89.90);*/
    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
        adminRepository.deleteAll();
        accountRepository.deleteAll();
        String user1Mail = "user1@mail.com";

        Account userAccount = accountRepository.save(new Account(user1Mail, defaultPasswordSha3, Role.User));
        user = userRepository.save(new Driver(userAccount.getAccountId(), "userName", "userSurname", Status.Pending));
        user.setAccount(userAccount);
        userRepository.save(user);

        Account adminAccount = accountRepository.save(new Account(adminUsername, defaultPasswordSha3, Role.Admin));
        admin = adminRepository.save(new Admin(adminAccount.getAccountId(), "adminName", "adminSurname", Status.Pending));
        admin.setAccount(adminAccount);
        adminRepository.save(admin);

    }

    @Test
    @WithMockUser(username = adminUsername, password = defaultPasswordSha3, authorities = {"Admin"})
    public void getProfile() {
        GetAdminResponce result = adminService.getProfile();
        assertTrue(adminRepository.findById(admin.getAdminId()).isPresent());
        Admin adminTest = adminRepository.findById(admin.getAdminId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));
        GetAdminResponce test = new GetAdminResponce(adminTest.getName(), adminTest.getSurname(), adminTest.getPcId(), adminTest.getStatus());
        assertTrue(EqualsBuilder.reflectionEquals(test,result));
    }

    @Test
    @WithMockUser(username = adminUsername, password = defaultPasswordSha3, authorities = {"Admin"})
    public void getAllAdmins() {
        Admin adminToGet = adminRepository.findById(admin.getAdminId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));
        assertTrue(adminRepository.findById(admin.getAdminId()).isPresent());
        GetAdminListResponce test = new GetAdminListResponce(adminToGet.getName(),
                adminToGet.getSurname(),
                null,
                adminToGet.getStatus(),
                new GetAccountResponce(adminToGet.getAccount().getUsername(),
                        adminToGet.getAccount().getDateReset(),
                        adminToGet.getAccount().getRole()));
        GetAdminListResponce result = adminService.getAllAdmins().get(adminService.getAllAdmins().size()-1);
        assertTrue(EqualsBuilder.reflectionEquals(test.getAccount(),result.getAccount()));
    }

    @Test
    @WithMockUser(username = adminUsername, password = defaultPasswordSha3, authorities = {"Admin"})
    public void getAllUsers() {
        Driver userToGet = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid userId"));
        assertTrue(userRepository.findById(user.getUserId()).isPresent());
        GetUserListResponce test = new GetUserListResponce(userToGet.getName(),
                userToGet.getSurname(),
                null,
                userToGet.getStatus(),
                new GetAccountResponce(userToGet.getAccount().getUsername(),
                        userToGet.getAccount().getDateReset(),
                        userToGet.getAccount().getRole()));
        GetUserListResponce result = adminService.getAllUsers().get(adminService.getAllUsers().size()-1);
        assertTrue(EqualsBuilder.reflectionEquals(test.getAccount(),result.getAccount()));
    }

    @Test
    @WithMockUser(username = adminUsername, password = defaultPasswordSha3, authorities = {"Admin"})
    public void getAllParkingAreaColor() {
        parkingAreaColorRepository.save(pac);
        ParkingAreaColor pacToGet = parkingAreaColorRepository.findById(pac.getPacId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid userId"));
        assertTrue(parkingAreaColorRepository.findById(pacToGet.getPacId()).isPresent());
        GetParkingAreaColorResponce test = new GetParkingAreaColorResponce(pacToGet.getColor(),
                pacToGet.getHourlyPrice(),
                pacToGet.getDailyPrice(),
                pacToGet.getWeeklyPrice(),
                pacToGet.getMonthlyPrice());
        GetParkingAreaColorResponce result = adminService.getAllParkingAreaColor().get(adminService.getAllParkingAreaColor().size()-1);
        assertTrue(EqualsBuilder.reflectionEquals(test.getColor(), result.getColor()));
    }

    @Test
    @WithMockUser(username = adminUsername, password = defaultPasswordSha3, authorities = {"Admin"})
    public void createParkingAreaColor() {
        parkingAreaColorRepository.save(pac);
        GetParkingAreaColorResponce test = new GetParkingAreaColorResponce(pac.getColor(),
                pac.getHourlyPrice(),
                pac.getDailyPrice(),
                pac.getWeeklyPrice(),
                pac.getMonthlyPrice());
        GetParkingAreaColorResponce result = adminService.createParkingAreaColor(test);
        assertTrue(parkingAreaColorRepository.findById(pac.getPacId()).isPresent());
        assertTrue(EqualsBuilder.reflectionEquals(test, result));
    }

    @Test
    @WithMockUser(username = adminUsername, password = defaultPasswordSha3, authorities = {"Admin"})
    public void updateParkingAreaColor() {
        parkingAreaColorRepository.save(pac);
        pac.setColor(Color.Green);
        parkingAreaColorRepository.save(pac);
        GetParkingAreaColorResponce test = new GetParkingAreaColorResponce(pac.getColor(),
                pac.getHourlyPrice(),
                pac.getDailyPrice(),
                pac.getWeeklyPrice(),
                pac.getMonthlyPrice());
        GetParkingAreaColorResponce result = adminService.updateParkingAreaColor(pac.getPacId(), test);
        assertTrue(parkingAreaColorRepository.findById(pac.getPacId()).isPresent());
        assertTrue(EqualsBuilder.reflectionEquals(test, result));
    }

    @Test
    @WithMockUser(username = adminUsername, password = defaultPasswordSha3, authorities = {"Admin"})
    public void deleteParkingAreaColor() {
        parkingAreaColorRepository.save(pac);
        assertTrue(parkingAreaColorRepository.findById(pac.getPacId()).isPresent());
        parkingAreaColorRepository.deleteById(pac.getPacId());
        assertFalse(parkingAreaColorRepository.findById(pac.getPacId()).isPresent());
    }

    @Test
    @WithMockUser(username = adminUsername, password = defaultPasswordSha3, authorities = {"Admin"})
    public void getAllParkingAreaTypeDimension() {
        parkingAreaTypeDimensionRepository.save(patd);
        ParkingAreaTypeDimension ptdToGet = parkingAreaTypeDimensionRepository.findById(patd.getPatdId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid userId"));
        assertTrue(parkingAreaTypeDimensionRepository.findById(ptdToGet.getPatdId()).isPresent());
        GetParkingAreaTypeDimensionResponce test = new GetParkingAreaTypeDimensionResponce(patd.getType(),
                patd.getLength(),
                patd.getWidth());
        GetParkingAreaTypeDimensionResponce result = adminService.getAllParkingAreaTypeDimension().get(adminService.getAllParkingAreaTypeDimension().size()-1);
        assertTrue(EqualsBuilder.reflectionEquals(test.getType(), result.getType()));
    }

    @Test
    @WithMockUser(username = adminUsername, password = defaultPasswordSha3, authorities = {"Admin"})
    public void createParkingAreaTypeDimensions() {
        parkingAreaTypeDimensionRepository.save(patd);
        GetParkingAreaTypeDimensionResponce test = new GetParkingAreaTypeDimensionResponce(patd.getType(),
                patd.getLength(),
                patd.getWidth());
        GetParkingAreaTypeDimensionResponce result = adminService.createParkingAreaTypeDimensions(test);
        assertTrue(parkingAreaTypeDimensionRepository.findById(patd.getPatdId()).isPresent());
        assertTrue(EqualsBuilder.reflectionEquals(test, result));
    }

    @Test
    @WithMockUser(username = adminUsername, password = defaultPasswordSha3, authorities = {"Admin"})
    public void updateParkingAreaTypeDimension() {
        parkingAreaTypeDimensionRepository.save(patd);
        patd.setType(Type.Pettine);
        parkingAreaTypeDimensionRepository.save(patd);
        GetParkingAreaTypeDimensionResponce test = new GetParkingAreaTypeDimensionResponce(patd.getType(),
                patd.getLength(),
                patd.getWidth());
        GetParkingAreaTypeDimensionResponce result = adminService.updateParkingAreaTypeDimension(patd.getPatdId(), test);
        assertTrue(parkingAreaTypeDimensionRepository.findById(patd.getPatdId()).isPresent());
        assertTrue(EqualsBuilder.reflectionEquals(test, result));
    }

    @Test
    @WithMockUser(username = adminUsername, password = defaultPasswordSha3, authorities = {"Admin"})
    public void deleteParkingAreaTypeDimension() {
        parkingAreaTypeDimensionRepository.save(patd);
        assertTrue(parkingAreaTypeDimensionRepository.findById(patd.getPatdId()).isPresent());
        parkingAreaTypeDimensionRepository.deleteById(patd.getPatdId());
        assertFalse(parkingAreaTypeDimensionRepository.findById(patd.getPatdId()).isPresent());
    }

    @Test
    @WithMockUser(username = adminUsername, password = defaultPasswordSha3, authorities = {"Admin"})
    public void getAllParkingArea() {
        List<ParkingArea> test = parkingAreaRepository.findAllFromParkingArea();
        List<ParkingArea> result = adminService.getAllParkingArea();
        assertTrue(EqualsBuilder.reflectionEquals(test, result));
    }

    @Test
    @WithMockUser(username = adminUsername, password = defaultPasswordSha3, authorities = {"Admin"})
    public void createParkingArea() {
        ParkingAreaColor pacSaved = parkingAreaColorRepository.save(pac);
        ParkingAreaTypeDimension patdSaved = parkingAreaTypeDimensionRepository.save(patd);
        pa.setPacId(pacSaved.getPacId());
        pa.setPatdId(patdSaved.getPatdId());
        ParkingAreaRequest test = new ParkingAreaRequest(pa.getPacId(),
                pa.getPatdId(),
                pa.getFunctionality(),
                pa.getLatitude(),
                pa.getLongitude(),
                pa.getStatus());
        ParkingArea paReturned =  adminService.createParkingArea(test);
        ParkingAreaRequest result = new ParkingAreaRequest(paReturned.getPacId(),
                paReturned.getPatdId(),
                paReturned.getFunctionality(),
                paReturned.getLatitude(),
                paReturned.getLongitude(),
                paReturned.getStatus());
        assertTrue(EqualsBuilder.reflectionEquals(test, result));
        assertTrue(parkingAreaRepository.findById(paReturned.getPaId()).isPresent());
    }

    @Test
    @WithMockUser(username = adminUsername, password = defaultPasswordSha3, authorities = {"Admin"})
    public void updateParkingArea() {
        ParkingAreaColor pacSaved = parkingAreaColorRepository.save(pac);
        ParkingAreaTypeDimension patdSaved = parkingAreaTypeDimensionRepository.save(patd);
        pa.setPacId(pacSaved.getPacId());
        pa.setPatdId(patdSaved.getPatdId());
        pa.setStatus(ParkingAreaStatus.Deleted);
        parkingAreaRepository.save(pa);
        ParkingAreaRequest test = new ParkingAreaRequest(pa.getPacId(),
                pa.getPatdId(),
                pa.getFunctionality(),
                pa.getLatitude(),
                pa.getLongitude(),
                pa.getStatus());
        ParkingArea paReturned =  adminService.updateParkingArea(pa.getPaId(),test);
        ParkingAreaRequest result = new ParkingAreaRequest(paReturned.getPacId(),
                paReturned.getPatdId(),
                paReturned.getFunctionality(),
                paReturned.getLatitude(),
                paReturned.getLongitude(),
                paReturned.getStatus());
        assertTrue(EqualsBuilder.reflectionEquals(test, result));
        assertTrue(parkingAreaRepository.findById(paReturned.getPaId()).isPresent());
    }

    @Test
    @WithMockUser(username = adminUsername, password = defaultPasswordSha3, authorities = {"Admin"})
    public void deleteParkingArea() {
        ParkingAreaColor pacSaved = parkingAreaColorRepository.save(pac);
        ParkingAreaTypeDimension patdSaved = parkingAreaTypeDimensionRepository.save(patd);
        pa.setPacId(pacSaved.getPacId());
        pa.setPatdId(patdSaved.getPatdId());
        parkingAreaRepository.save(pa);
        assertTrue(parkingAreaRepository.findById(pa.getPaId()).isPresent());
        parkingAreaRepository.deleteById(pa.getPaId());
        assertFalse(parkingAreaRepository.findById(pa.getPaId()).isPresent());
    }

    @Test
    @WithMockUser(username = adminUsername, password = defaultPasswordSha3, authorities = {"Admin"})
    public void updateHourlyPricePAC() {
        parkingAreaColorRepository.save(pac);
        pac.setHourlyPrice(5.60);
        parkingAreaColorRepository.save(pac);
        GetParkingAreaColorResponce test = new GetParkingAreaColorResponce(pac.getColor(),
                pac.getHourlyPrice(),
                pac.getDailyPrice(),
                pac.getWeeklyPrice(),
                pac.getMonthlyPrice());
        GetParkingAreaColorResponce result = adminService.updateHourlyPricePAC(pac.getPacId(), 5.60);
        assertTrue(parkingAreaColorRepository.findById(pac.getPacId()).isPresent());
        assertTrue(EqualsBuilder.reflectionEquals(test, result));
    }

    @Test
    @WithMockUser(username = adminUsername, password = defaultPasswordSha3, authorities = {"Admin"})
    public void updateDailyPricePAC() {
        parkingAreaColorRepository.save(pac);
        pac.setDailyPrice(5.60);
        parkingAreaColorRepository.save(pac);
        GetParkingAreaColorResponce test = new GetParkingAreaColorResponce(pac.getColor(),
                pac.getHourlyPrice(),
                pac.getDailyPrice(),
                pac.getWeeklyPrice(),
                pac.getMonthlyPrice());
        GetParkingAreaColorResponce result = adminService.updateDailyPricePAC(pac.getPacId(), 5.60);
        assertTrue(parkingAreaColorRepository.findById(pac.getPacId()).isPresent());
        assertTrue(EqualsBuilder.reflectionEquals(test, result));
    }

    @Test
    @WithMockUser(username = adminUsername, password = defaultPasswordSha3, authorities = {"Admin"})
    public void updateWeeklyPricePAC() {
        parkingAreaColorRepository.save(pac);
        pac.setWeeklyPrice(5.60);
        parkingAreaColorRepository.save(pac);
        GetParkingAreaColorResponce test = new GetParkingAreaColorResponce(pac.getColor(),
                pac.getHourlyPrice(),
                pac.getDailyPrice(),
                pac.getWeeklyPrice(),
                pac.getMonthlyPrice());
        GetParkingAreaColorResponce result = adminService.updateWeeklyPricePAC(pac.getPacId(), 5.60);
        assertTrue(parkingAreaColorRepository.findById(pac.getPacId()).isPresent());
        assertTrue(EqualsBuilder.reflectionEquals(test, result));
    }

    @Test
    @WithMockUser(username = adminUsername, password = defaultPasswordSha3, authorities = {"Admin"})
    public void updateMonthlyPricePAC() {
        parkingAreaColorRepository.save(pac);
        pac.setMonthlyPrice(5.60);
        parkingAreaColorRepository.save(pac);
        GetParkingAreaColorResponce test = new GetParkingAreaColorResponce(pac.getColor(),
                pac.getHourlyPrice(),
                pac.getDailyPrice(),
                pac.getWeeklyPrice(),
                pac.getMonthlyPrice());
        GetParkingAreaColorResponce result = adminService.updateMonthlyPricePAC(pac.getPacId(), 5.60);
        assertTrue(parkingAreaColorRepository.findById(pac.getPacId()).isPresent());
        assertTrue(EqualsBuilder.reflectionEquals(test, result));
    }

    @Test
    @WithMockUser(username = adminUsername, password = defaultPasswordSha3, authorities = {"Admin"})
    public void setFreePAC() {
        parkingAreaColorRepository.save(pac);
        pac.setHourlyPrice(0.0);
        pac.setDailyPrice(0.0);
        pac.setWeeklyPrice(0.0);
        pac.setMonthlyPrice(0.0);
        parkingAreaColorRepository.save(pac);
        GetParkingAreaColorResponce test = new GetParkingAreaColorResponce(pac.getColor(),
                pac.getHourlyPrice(),
                pac.getDailyPrice(),
                pac.getWeeklyPrice(),
                pac.getMonthlyPrice());
        GetParkingAreaColorResponce result = adminService.setFreePAC(pac.getPacId());
        assertTrue(parkingAreaColorRepository.findById(pac.getPacId()).isPresent());
        assertTrue(EqualsBuilder.reflectionEquals(test, result));
    }

    @Test
    @WithMockUser(username = adminUsername, password = defaultPasswordSha3, authorities = {"Admin"})
    public void setPADamaged() {
        ParkingAreaColor pacSaved = parkingAreaColorRepository.save(pac);
        ParkingAreaTypeDimension patdSaved = parkingAreaTypeDimensionRepository.save(patd);
        pa.setPacId(pacSaved.getPacId());
        pa.setPatdId(patdSaved.getPatdId());
        pa.setStatus(ParkingAreaStatus.Damaged);
        parkingAreaRepository.save(pa);
        ParkingArea test = parkingAreaRepository.save(pa);
        ParkingArea result = adminService.setPADamaged(pa.getPaId());
        assertTrue(parkingAreaRepository.findById(pa.getPaId()).isPresent());
        assertTrue(EqualsBuilder.reflectionEquals(test, result));
    }
}
