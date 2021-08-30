package core.easyparking.polimi.service;

import core.easyparking.polimi.entity.*;
import core.easyparking.polimi.repository.*;
import core.easyparking.polimi.utils.object.request.GetParkingAreaRequest;
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
public class UserServiceTest {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private FineRepository fineRepository;
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
    private PaymentInfoRepository paymentInfoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private UserService userService;

    protected Admin admin;
    private Driver user;
    protected final String adminUsername = "admintestservice@mail.com";
    private final String defaultPasswordSha3 = "363999f7918bb84260f481cceaed396fb046e8dc25750c5c3ae0e8088ae17b22";
    ParkingAreaColor pac = new ParkingAreaColor(Color.Blue,1.60,5.00,15.99, 45.70);
    ParkingAreaTypeDimension patd = new ParkingAreaTypeDimension(Type.Nastro, 12.20, 11.30);
    ParkingArea pa = new ParkingArea(1L, 1L, Functionality.Car, 20.87, 34.56, ParkingAreaStatus.Free);
    ModelVehicle mv1;
    ModelVehicle mv2;
    Vehicle vehicle;
    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
        adminRepository.deleteAll();
        accountRepository.deleteAll();
        modelVehicleRepository.deleteAll();
        vehicleRepository.deleteAll();
        ticketRepository.deleteAll();
        fineRepository.deleteAll();
        String userMail = "userservicetest@mail.com";

        Account userAccount = accountRepository.save(new Account(userMail, defaultPasswordSha3, Role.User));
        user = userRepository.save(new Driver(userAccount.getAccountId(), "userName", "userSurname", Status.Pending));
        user.setAccount(userAccount);
        userRepository.save(user);

        Account adminAccount = accountRepository.save(new Account(adminUsername, defaultPasswordSha3, Role.Admin));
        admin = adminRepository.save(new Admin(adminAccount.getAccountId(), "adminName", "adminSurname", Status.Pending));
        admin.setAccount(adminAccount);
        adminRepository.save(admin);

        mv1 = modelVehicleRepository.save(new ModelVehicle("FIAT", "panda", 1999, 1000, Functionality.Car, 1.78, 1.56));
        mv2 = modelVehicleRepository.save(new ModelVehicle("ALFA ROMEO", "panda", 1999, 1000, Functionality.Car, 1.78, 1.56));
        pac = parkingAreaColorRepository.save(pac);
        patd = parkingAreaTypeDimensionRepository.save(patd);
        pa.setPacId(pac.getPacId());
        pa.setPatdId(patd.getPatdId());
        parkingAreaRepository.save(pa);
        vehicle = vehicleRepository.save(new Vehicle(mv1.getMvId(), user.getUserId(), "AB123EI"));

    }

    @Test
    @WithMockUser(username = "userservicetest@mail.com", password = defaultPasswordSha3, authorities = {"User"})
    public void getProfile() {
        GetUserResponce result = userService.getProfile();
        assertTrue(userRepository.findById(user.getUserId()).isPresent());
        Driver userTest = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid userId"));
        GetUserResponce test = new GetUserResponce(userTest.getName(), userTest.getSurname(), userTest.getLicenseId(), userTest.getStatus());
        assertTrue(EqualsBuilder.reflectionEquals(test,result));
    }

    @Test
    @WithMockUser(username = "userservicetest@mail.com", password = defaultPasswordSha3, authorities = {"User"})
    public void addVehicle() {
        modelVehicleRepository.save(new ModelVehicle("FIAT", "panda", 1999, 1000, Functionality.Car, 1.78, 1.56));
        userService.addVehicle(1L, "AB123ET");
        assertTrue(vehicleRepository.findByLicensePlate("AB123ET").isPresent());
    }

    @Test
    @WithMockUser(username = "userservicetest@mail.com", password = defaultPasswordSha3, authorities = {"User"})
    public void getVehicles() {

        Vehicle vehicle = vehicleRepository.save(new Vehicle(mv1.getMvId(), user.getUserId(), "AB123ET"));
        vehicle.setModelVehicle(modelVehicleRepository.findByMvId(mv1.getMvId()));
        Vehicle vehicleToGet = vehicleRepository.findById(vehicle.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid vehicleId"));
        assertTrue(vehicleRepository.findById(vehicleToGet.getVehicleId()).isPresent());
        GetVehiclesResponce test = new GetVehiclesResponce(vehicleToGet.getLicensePlate(),
                new ModelVehicle(vehicle.getModelVehicle().getBrand(),
                        vehicle.getModelVehicle().getName(),
                        vehicle.getModelVehicle().getYear(),
                        vehicle.getModelVehicle().getCv(),
                        vehicle.getModelVehicle().getType(),
                        vehicle.getModelVehicle().getLength(),
                        vehicle.getModelVehicle().getLength()));
        GetVehiclesResponce result = userService.getVehicles().get(1);
        assertTrue(EqualsBuilder.reflectionEquals(test.getLicensePlate(), result.getLicensePlate()));
    }

    @Test
    @WithMockUser(username = "userservicetest@mail.com", password = defaultPasswordSha3, authorities = {"User"})
    public void updateMV() {

        Vehicle vehicle = vehicleRepository.save(new Vehicle(mv1.getMvId(), user.getUserId(), "AB123ET"));
        vehicle.setMvId(mv2.getMvId());
        vehicle.setUser(user);
        ModelVehicle modelVehicle = modelVehicleRepository.findByMvId(mv2.getMvId());
        vehicle.setModelVehicle(modelVehicle);
        Vehicle vehicleToGet = vehicleRepository.findById(vehicle.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid vehicleId"));
        assertTrue(vehicleRepository.findById(vehicleToGet.getVehicleId()).isPresent());
        AddVehicleResponce test = new AddVehicleResponce(vehicleToGet.getLicensePlate(),
                modelVehicle,
                new GetUserResponce(vehicleToGet.getUser().getName(),
                        vehicleToGet.getUser().getSurname(),
                        vehicleToGet.getUser().getLicenseId(),
                        vehicleToGet.getUser().getStatus()));
        AddVehicleResponce result = userService.updateMV(vehicle.getVehicleId(), mv2.getMvId());
        assertTrue(EqualsBuilder.reflectionEquals(test.getModelVehicle(), result.getModelVehicle()));
    }

    @Test
    @WithMockUser(username = "userservicetest@mail.com", password = defaultPasswordSha3, authorities = {"User"})
    public void getPAFilteredByZ() {

        GetParkingAreaRequest parkingArea = new GetParkingAreaRequest(
        Functionality.Car, 10.0, 10.0, 80.0, 80.0, 1L, 1L, 1L);
        List<ParkingArea> parkingAreaList =  parkingAreaRepository.findParkingAreaByZ(
                parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDeparture(), parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDestination());
        assertTrue(parkingAreaList.contains(pa));
    }

    @Test
    @WithMockUser(username = "userservicetest@mail.com", password = defaultPasswordSha3, authorities = {"User"})
    public void getPAFilteredByZF() {

        GetParkingAreaRequest parkingArea = new GetParkingAreaRequest(
                Functionality.Car, 10.0, 10.0, 80.0, 80.0, 1L, 1L, 1L);
        List<ParkingArea> parkingAreaList =  parkingAreaRepository.findParkingAreaByZF(
                parkingArea.getFunctionality(), parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDeparture(), parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDestination());
        assertTrue(parkingAreaList.contains(pa));
    }

    @Test
    @WithMockUser(username = "userservicetest@mail.com", password = defaultPasswordSha3, authorities = {"User"})
    public void getPAFilteredByZFC() {

        GetParkingAreaRequest parkingArea = new GetParkingAreaRequest(
                Functionality.Car, 10.0, 10.0, 80.0, 80.0, vehicle.getVehicleId(), pac.getPacId(), patd.getPatdId());
        List<ParkingArea> parkingAreaList = parkingAreaRepository.findParkingAreaByZFC(
                parkingArea.getPacId(), parkingArea.getFunctionality(), parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDeparture(), parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDestination());
        assertTrue(parkingAreaList.contains(pa));
    }

    @Test
    @WithMockUser(username = "userservicetest@mail.com", password = defaultPasswordSha3, authorities = {"User"})
    public void getPAFilteredByZFCT() {

        GetParkingAreaRequest parkingArea = new GetParkingAreaRequest(
                Functionality.Car, 10.0, 10.0, 80.0, 80.0, vehicle.getVehicleId(), pac.getPacId(), patd.getPatdId());
        List<ParkingArea> parkingAreaList = parkingAreaRepository.findParkingAreaByZFCT(
                parkingArea.getPatdId(), parkingArea.getPacId(), parkingArea.getFunctionality(), parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDeparture(), parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDestination());
        assertTrue(parkingAreaList.contains(pa));
    }

    @Test
    @WithMockUser(username = "userservicetest@mail.com", password = defaultPasswordSha3, authorities = {"User"})
    public void getPAFilteredByZFCTV() {

        GetParkingAreaRequest parkingArea = new GetParkingAreaRequest(
                Functionality.Car, 10.0, 10.0, 80.0, 80.0, vehicle.getVehicleId(), pac.getPacId(), patd.getPatdId());
        List<ParkingArea> parkingAreaList = parkingAreaRepository.findParkingAreaByZFCT(
                parkingArea.getPatdId(), parkingArea.getPacId(), parkingArea.getFunctionality(), parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDeparture(), parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDestination());
        assertTrue(parkingAreaList.contains(pa));

        Vehicle vehicle = vehicleRepository.save(new Vehicle(mv1.getMvId(), user.getUserId(),"CD908OP"));
        vehicle.setUserId(user.getUserId());
        vehicle.setModelVehicle(modelVehicleRepository.findByMvId(mv1.getMvId()));
        ParkingAreaTypeDimension patd = parkingAreaTypeDimensionRepository.findById(pa.getPatdId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid patdId"));
        pa.setParkingAreaTypeDimension(patd);
        for(int i=0; i<parkingAreaList.size(); i++) {
            if (vehicle.getModelVehicle().getLength() > parkingAreaList.get(i).getParkingAreaTypeDimension().getLength() &&
                    vehicle.getModelVehicle().getWidth() > parkingAreaList.get(i).getParkingAreaTypeDimension().getWidth()){
                parkingAreaList.remove(i);
            }
        }
        assertTrue(parkingAreaList.contains(pa));
    }

    @Test
    @WithMockUser(username = "userservicetest@mail.com", password = defaultPasswordSha3, authorities = {"User"})
    public void getParkingAreaColors() {
        List<ParkingAreaColor> test = parkingAreaColorRepository.findAll();
        List<ParkingAreaColor> result = userService.getParkingAreaColors();
        assertTrue(EqualsBuilder.reflectionEquals(test, result));
    }

    @Test
    @WithMockUser(username = "userservicetest@mail.com", password = defaultPasswordSha3, authorities = {"User"})
    public void getParkingAreaTD() {
        List<ParkingAreaTypeDimension> test = parkingAreaTypeDimensionRepository.findAll();
        List<ParkingAreaTypeDimension> result = userService.getParkingAreaTD();
        assertTrue(EqualsBuilder.reflectionEquals(test, result));
    }

    @Test
    @WithMockUser(username = "userservicetest@mail.com", password = defaultPasswordSha3, authorities = {"User"})
    public void getModelsVehicle() {
        List<ModelVehicle> test = modelVehicleRepository.findAll();
        List<ModelVehicle> result = userService.getModelsVehicle();
        assertTrue(EqualsBuilder.reflectionEquals(test, result));
    }

    @Test
    @WithMockUser(username = "userservicetest@mail.com", password = defaultPasswordSha3, authorities = {"User"})
    public void getTickets() {
        Vehicle vehicle = vehicleRepository.save(new Vehicle(mv1.getMvId(), user.getUserId(),"CD908OP"));
        vehicle.setUserId(user.getUserId());
        vehicle.setModelVehicle(modelVehicleRepository.findByMvId(mv1.getMvId()));
        PaymentInfo pi = paymentInfoRepository.save(new PaymentInfo());
        Ticket ticket = ticketRepository.save(new Ticket(user.getUserId(), pa.getPaId(), pi.getPaymentId(), vehicle.getVehicleId(), LocalDateTime.now().plusDays(15), 150.60));
        List<Ticket> ticketToGet = ticketRepository.findByUserIdAndPIdNotNull(user.getUserId());
        ticket.setVehicle(vehicle);
        assertTrue(ticketRepository.findById(ticketToGet.get(0).getTicketId()).isPresent());
        GetTicketResponce test = new GetTicketResponce(
                ticketToGet.get(0).getDeadline(),
                ticketToGet.get(0).getPrice(),
                ticketToGet.get(0).getParkingArea(),
                new GetVehicleResponce(ticketToGet.get(0).getVehicle().getLicensePlate(),
                        new GetModelVehicleResponce(ticketToGet.get(0).getVehicle().getModelVehicle().getBrand(),
                                ticketToGet.get(0).getVehicle().getModelVehicle().getName(),
                                ticketToGet.get(0).getVehicle().getModelVehicle().getYear(),
                                ticketToGet.get(0).getVehicle().getModelVehicle().getCv(),
                                ticketToGet.get(0).getVehicle().getModelVehicle().getType(),
                                ticketToGet.get(0).getVehicle().getModelVehicle().getLength(),
                                ticketToGet.get(0).getVehicle().getModelVehicle().getWidth())));
        GetTicketResponce result = userService.getTickets().get(0);
        assertTrue(EqualsBuilder.reflectionEquals(test.getPrice(), result.getPrice()));
    }

    @Test
    @WithMockUser(username = "userservicetest@mail.com", password = defaultPasswordSha3, authorities = {"User"})
    public void getFines() {
        ParkingArea parkingArea = parkingAreaRepository.save(pa);
        Vehicle vehicle = vehicleRepository.save(new Vehicle(mv1.getMvId(), user.getUserId(),"CD908OP"));
        vehicle.setUserId(user.getUserId());
        vehicle.setModelVehicle(modelVehicleRepository.findByMvId(mv1.getMvId()));
        PaymentInfo pi = paymentInfoRepository.save(new PaymentInfo());
        Fine fine = fineRepository.save(new Fine(user.getUserId(), parkingArea.getPaId(), pi.getPaymentId(), vehicle.getVehicleId(),  "Cause", LocalDateTime.now().plusDays(15), 0, 150.50));
        fine.setUserId(user.getUserId());
        fine.setVehicle(vehicle);
        List<Fine> fineToGet = fineRepository.findByUserId(user.getUserId());
        assertTrue(fineRepository.findById(fineToGet.get(0).getFineId()).isPresent());
        GetFineListResponce test = new GetFineListResponce(fineToGet.get(0).getParkingArea(),
                new GetVehicleResponce(fineToGet.get(0).getVehicle().getLicensePlate(),
                        new GetModelVehicleResponce(fineToGet.get(0).getVehicle().getModelVehicle().getBrand(),
                                fineToGet.get(0).getVehicle().getModelVehicle().getName(),
                                fineToGet.get(0).getVehicle().getModelVehicle().getYear(),
                                fineToGet.get(0).getVehicle().getModelVehicle().getCv(),
                                fineToGet.get(0).getVehicle().getModelVehicle().getType(),
                                fineToGet.get(0).getVehicle().getModelVehicle().getLength(),
                                fineToGet.get(0).getVehicle().getModelVehicle().getWidth()
                        )),
                fineToGet.get(0).getCause(),
                fineToGet.get(0).getDeadline(),
                fineToGet.get(0).getRemovedPoints(),
                fineToGet.get(0).getTotal());
        GetFineListResponce result = userService.getFines().get(userService.getFines().size()-1);
        assertTrue(EqualsBuilder.reflectionEquals(test.getTotal(), result.getTotal()));
    }

    @Test
    @WithMockUser(username = "userservicetest@mail.com", password = defaultPasswordSha3, authorities = {"User"})
    public void getFinesNotPaid() {
        ParkingArea parkingArea = parkingAreaRepository.save(pa);
        Vehicle vehicle = vehicleRepository.save(new Vehicle(mv1.getMvId(), user.getUserId(),"CD908OP"));
        vehicle.setUserId(user.getUserId());
        vehicle.setModelVehicle(modelVehicleRepository.findByMvId(mv1.getMvId()));
        Fine fine = fineRepository.save(new Fine(user.getUserId(), parkingArea.getPaId(), null, vehicle.getVehicleId(),  "Cause", LocalDateTime.now().plusDays(15), 0, 150.50));
        fine.setUserId(user.getUserId());
        fine.setVehicle(vehicle);
        List<Fine> fineToGet = fineRepository.findByUserIdAndPIdNull(user.getUserId());
        assertTrue(fineRepository.findById(fineToGet.get(0).getFineId()).isPresent());
        GetFineListResponce test = new GetFineListResponce(fineToGet.get(0).getParkingArea(),
                new GetVehicleResponce(fineToGet.get(0).getVehicle().getLicensePlate(),
                        new GetModelVehicleResponce(fineToGet.get(0).getVehicle().getModelVehicle().getBrand(),
                                fineToGet.get(0).getVehicle().getModelVehicle().getName(),
                                fineToGet.get(0).getVehicle().getModelVehicle().getYear(),
                                fineToGet.get(0).getVehicle().getModelVehicle().getCv(),
                                fineToGet.get(0).getVehicle().getModelVehicle().getType(),
                                fineToGet.get(0).getVehicle().getModelVehicle().getLength(),
                                fineToGet.get(0).getVehicle().getModelVehicle().getWidth()
                        )),
                fineToGet.get(0).getCause(),
                fineToGet.get(0).getDeadline(),
                fineToGet.get(0).getRemovedPoints(),
                fineToGet.get(0).getTotal());
        GetFineListResponce result = userService.getFines().get(userService.getFinesNotPaid().size()-1);
        assertTrue(EqualsBuilder.reflectionEquals(test.getTotal(), result.getTotal()));
    }

    @Test
    @WithMockUser(username = "userservicetest@mail.com", password = defaultPasswordSha3, authorities = {"User"})
    public void getFinesPaid() {
        ParkingArea parkingArea = parkingAreaRepository.save(pa);
        Vehicle vehicle = vehicleRepository.save(new Vehicle(mv1.getMvId(), user.getUserId(),"CD908OP"));
        vehicle.setUserId(user.getUserId());
        vehicle.setModelVehicle(modelVehicleRepository.findByMvId(mv1.getMvId()));
        PaymentInfo pi = paymentInfoRepository.save(new PaymentInfo());
        Fine fine = fineRepository.save(new Fine(user.getUserId(), parkingArea.getPaId(), pi.getPaymentId(), vehicle.getVehicleId(),  "Cause", LocalDateTime.now().plusDays(15), 0, 150.50));
        fine.setUserId(user.getUserId());
        fine.setVehicle(vehicle);
        List<Fine> fineToGet = fineRepository.findByUserIdAndPIdNotNull(user.getUserId());
        assertTrue(fineRepository.findById(fineToGet.get(0).getFineId()).isPresent());
        GetFineListResponce test = new GetFineListResponce(fineToGet.get(0).getParkingArea(),
                new GetVehicleResponce(fineToGet.get(0).getVehicle().getLicensePlate(),
                        new GetModelVehicleResponce(fineToGet.get(0).getVehicle().getModelVehicle().getBrand(),
                                fineToGet.get(0).getVehicle().getModelVehicle().getName(),
                                fineToGet.get(0).getVehicle().getModelVehicle().getYear(),
                                fineToGet.get(0).getVehicle().getModelVehicle().getCv(),
                                fineToGet.get(0).getVehicle().getModelVehicle().getType(),
                                fineToGet.get(0).getVehicle().getModelVehicle().getLength(),
                                fineToGet.get(0).getVehicle().getModelVehicle().getWidth()
                        )),
                fineToGet.get(0).getCause(),
                fineToGet.get(0).getDeadline(),
                fineToGet.get(0).getRemovedPoints(),
                fineToGet.get(0).getTotal());
        GetFineListResponce result = userService.getFines().get(userService.getFinesPaid().size()-1);
        assertTrue(EqualsBuilder.reflectionEquals(test.getTotal(), result.getTotal()));
    }


}
