package core.easyparking.polimi.service;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import core.easyparking.polimi.configuration.error.ErrorResponse;
import core.easyparking.polimi.entity.*;
import core.easyparking.polimi.repository.*;
import core.easyparking.polimi.service.jwt.JWTAuthenticationService;
import core.easyparking.polimi.service.jwt.JWTService;
import core.easyparking.polimi.utils.object.request.GetParkingAreaRequest;
import core.easyparking.polimi.utils.object.request.GetTicketRequest;
import core.easyparking.polimi.utils.object.responce.*;
import core.easyparking.polimi.utils.object.staticvalues.ParkingAreaStatus;
import core.easyparking.polimi.utils.object.staticvalues.Role;
import core.easyparking.polimi.utils.object.staticvalues.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static core.easyparking.polimi.entity.Vehicle.validateVehicleJsonField;
import static core.easyparking.polimi.utils.object.request.GetParkingAreaRequest.validateGetParkingAreaJsonBody;
import static core.easyparking.polimi.utils.object.request.GetTicketRequest.validateGetTicketJsonBody;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserService {
	private final AccountRepository accountRepository;
	private final UserRepository userRepository;
	private final JWTAuthenticationService authenticationService;
	private final ParkingAreaRepository parkingAreaRepository;
	private final VehicleRepository vehicleRepository;
	private final ModelVehicleRepository modelVehicleRepository;
	private final ParkingAreaColorRepository parkingAreaColorRepository;
	private final ParkingAreaTypeDimensionRepository parkingAreaTypeDimensionRepository;
	private final LicenseRepository licenseRepository;
	private final TicketRepository ticketRepository;
	private final FineRepository fineRepository;
	private final PayPalService payPalService;
	private Ticket ticket = new Ticket();
	private Fine fine = new Fine();



	/**
	 * Allows to retrieve all user data
	 * @return a Driver: all information of certain user
	 */
	private Driver getUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			User user = ((User) authentication.getPrincipal());
			if (!user.getAuthorities().contains(new SimpleGrantedAuthority(Role.User.name()))) {
				throw new JWTService.TokenVerificationException();
			}
			Account account = accountRepository.findByRoleAndUsername(Role.User, user.getUsername())
					.orElseThrow(JWTService.TokenVerificationException::new);
			return userRepository.findByAccountId(account.getAccountId())
					.orElseThrow(JWTService.TokenVerificationException::new);
		}
		throw new JWTService.TokenVerificationException();
	}

	/**
	 * Allows to retrieve all profile user data
	 * @return a GetUserResponce: all information to send as responce for a certain user
	 */
	public GetUserResponce getProfile() {
		return new GetUserResponce(getUser().getName(), getUser().getSurname(), getUser().getLicenseId(), getUser().getStatus());
	}

	/**
	 * Allows user to change password
	 * @return a String: jwt authorization to can log with the new password
	 */
	public String changePassword(String oldPassword, String newPassword) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			User user = ((User) authentication.getPrincipal());
			if (!user.getAuthorities().contains(new SimpleGrantedAuthority(Role.User.name()))) {
				throw new JWTService.TokenVerificationException();
			}
			Account account = accountRepository.findByRoleAndUsername(Role.User, user.getUsername())
					.orElseThrow(JWTService.TokenVerificationException::new);
			// Password validate
			String pattern = "^(?=.*?[0-9])(?=.*?[a-z])(?=.*?[A-Z])(.{8,30})$";
			if (!newPassword.matches(pattern) || newPassword.length() > 30) {
				throwIllegal("Invalid new password");
			}
			if (!account.getPassword().equals(DigestUtils.sha3_256Hex(oldPassword))){
				throwIllegal("Invalid old password");
			} else {
				String shaNewPass = DigestUtils.sha3_256Hex(newPassword);
				account.setPassword(shaNewPass);
				accountRepository.save(account);
				return authenticationService.login(account.getRole(), account.getUsername(), shaNewPass);
			}
		}
		throw new JWTService.TokenVerificationException();
	}

	/**
	 * Allows retrieving of all user data from his authentication
	 * @return a Account: all information about user logged
	 */
	public Account getUserInfo(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			User user = ((User) authentication.getPrincipal());
			if (!user.getAuthorities().contains(new SimpleGrantedAuthority(Role.User.name()))) {
				throw new JWTService.TokenVerificationException();
			}
			return accountRepository.findByRoleAndUsername(Role.User, user.getUsername())
					.orElseThrow(JWTService.TokenVerificationException::new);
		}
		throw new JWTService.TokenVerificationException();
	}

	/**
	 * Allows user to inserts a new vehicle in his profile
	 * @param mvId: the unique identifier of vehicle model
	 * @param licensePlate: the unique license plate of vehicle
	 * @return a AddVehicleResponce: all information about a new vehicle inserted
	 */
	public AddVehicleResponce addVehicle(Long mvId, String licensePlate) {

		Driver userToGet = userRepository.findByAccountId(getUserInfo().getAccountId())
				.orElseThrow(() -> new IllegalArgumentException("Some error occurred"));

		ModelVehicle modelToGet = modelVehicleRepository.findByMvId(mvId);
		Vehicle vehicle= new Vehicle(mvId, userToGet.getUserId(), licensePlate);
		vehicle.setUser(userToGet);
		vehicle.setModelVehicle(modelToGet);

		if (!validateVehicleJsonField(vehicle))
			throwIllegal("Invalid json body");
		vehicleRepository.save(vehicle);

		return new AddVehicleResponce(licensePlate,
				vehicle.getModelVehicle(),
				new GetUserResponce(vehicle.getUser().getName(),
						vehicle.getUser().getSurname(),
						vehicle.getUser().getLicenseId(),
						vehicle.getUser().getStatus()));
	}

	/**
	 * Allows user to get a list of his vehicles
	 * @return a List<GetVehiclesResponce>: all vehicles about the user profile
	 */
	public List<GetVehiclesResponce> getVehicles() {

		Driver userToGet = userRepository.findByAccountId(getUserInfo().getAccountId())
				.orElseThrow(() -> new IllegalArgumentException("Some error occurred"));
		List <Vehicle> vehicleList = vehicleRepository.findByUserId(userToGet.getUserId());
		List<GetVehiclesResponce> getVehiclesResponces = new ArrayList<>();
		for (Vehicle vehicle : vehicleList) {
			getVehiclesResponces.add(new GetVehiclesResponce(
					vehicle.getLicensePlate(),
					vehicle.getModelVehicle()));
		}
		return getVehiclesResponces;
	}

	/**
	 * Allows user to update a vehicle model in his vehicle
	 * @param vehicleId: the unique identifier of vehicle
	 * @param mvId: the unique identifier of vehicle model
	 * @return a AddVehicleResponce: all information about a vehicle updated
	 */
	public AddVehicleResponce updateMV(Long vehicleId, Long mvId) {

		Vehicle vehicleToUpdate = vehicleRepository.findByVehicleId(vehicleId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid vehicleId"));
		ModelVehicle modelVehicle = modelVehicleRepository.findByMvId(mvId);

		vehicleToUpdate.setMvId(mvId);
		vehicleRepository.save(vehicleToUpdate);
		return new AddVehicleResponce(vehicleToUpdate.getLicensePlate(),
				modelVehicle,
				new GetUserResponce(vehicleToUpdate.getUser().getName(),
						vehicleToUpdate.getUser().getSurname(),
						vehicleToUpdate.getUser().getLicenseId(),
						vehicleToUpdate.getUser().getStatus()));
	}

	/**
	 * Allows user to get parking areas free between departure zone and destination zone
	 * @param parkingArea: json data retrieved from body to complete request
	 * @return a List<ParkingArea>: all parking area filtered by related request
	 */
	public List<ParkingArea> getPAFilteredByZ(GetParkingAreaRequest parkingArea) {

		if(!validateGetParkingAreaJsonBody(parkingArea))
			throwIllegal("Invalid json body");

		if (parkingArea.getLatitudeDeparture() > parkingArea.getLatitudeDestination() &&
				parkingArea.getLongitudeDeparture() > parkingArea.getLongitudeDestination())
			return parkingAreaRepository.findParkingAreaByZ(
					parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDestination(), parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDestination());
		else if (parkingArea.getLatitudeDeparture() > parkingArea.getLatitudeDestination())
			return parkingAreaRepository.findParkingAreaByZ(
					parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDeparture(), parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDestination());
		else if (parkingArea.getLongitudeDeparture() > parkingArea.getLongitudeDestination())
			return parkingAreaRepository.findParkingAreaByZ(
					parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDestination(), parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDeparture());
		else
			return parkingAreaRepository.findParkingAreaByZ(
					parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDeparture(), parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDestination());
	}

	/**
	 * Allows user to get parking areas free between departure zone and destination zone,
	 * filtered by parking area functionality
	 * @param parkingArea: json data retrieved from body to complete request
	 * @return a List<ParkingArea>: all parking area filtered by related request
	 */
	public List<ParkingArea> getPAFilteredByZF(GetParkingAreaRequest parkingArea) {

		if(!validateGetParkingAreaJsonBody(parkingArea))
			throwIllegal("Invalid json body");

		if (parkingArea.getLatitudeDeparture() > parkingArea.getLatitudeDestination() &&
				parkingArea.getLongitudeDeparture() > parkingArea.getLongitudeDestination())
			return parkingAreaRepository.findParkingAreaByZF(
					parkingArea.getFunctionality(),parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDestination(), parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDestination());
		else if (parkingArea.getLatitudeDeparture() > parkingArea.getLatitudeDestination())
			return parkingAreaRepository.findParkingAreaByZF(
					parkingArea.getFunctionality(), parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDeparture(), parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDestination());
		else if (parkingArea.getLongitudeDeparture() > parkingArea.getLongitudeDestination())
			return parkingAreaRepository.findParkingAreaByZF(
					parkingArea.getFunctionality(), parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDestination(), parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDeparture());
		else
			return parkingAreaRepository.findParkingAreaByZF(
					parkingArea.getFunctionality(), parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDeparture(), parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDestination());
	}

	/**
	 * Allows user to get parking areas free between departure zone and destination zone,
	 * filtered by parking area functionality and parking area color
	 * @param parkingArea: json data retrieved from body to complete request
	 * @return a List<ParkingArea>: all parking area filtered by related request
	 */
	public List<ParkingArea> getPAFilteredByZFC(GetParkingAreaRequest parkingArea) {

		if(!validateGetParkingAreaJsonBody(parkingArea))
			throwIllegal("Invalid json body");

		if (parkingArea.getLatitudeDeparture() > parkingArea.getLatitudeDestination() &&
				parkingArea.getLongitudeDeparture() > parkingArea.getLongitudeDestination())
			return parkingAreaRepository.findParkingAreaByZFC(
					parkingArea.getPacId(), parkingArea.getFunctionality(),parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDestination(), parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDestination());
		else if (parkingArea.getLatitudeDeparture() > parkingArea.getLatitudeDestination())
			return parkingAreaRepository.findParkingAreaByZFC(
					parkingArea.getPacId(), parkingArea.getFunctionality(), parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDeparture(), parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDestination());
		else if (parkingArea.getLongitudeDeparture() > parkingArea.getLongitudeDestination())
			return parkingAreaRepository.findParkingAreaByZFC(
					parkingArea.getPacId(), parkingArea.getFunctionality(), parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDestination(), parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDeparture());
		else
			return parkingAreaRepository.findParkingAreaByZFC(
					parkingArea.getPacId(), parkingArea.getFunctionality(), parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDeparture(), parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDestination());
	}

	/**
	 * Allows user to get parking areas free between departure zone and destination zone,
	 * filtered by parking area functionality, parking area color and type
	 * @param parkingArea: json data retrieved from body to complete request
	 * @return a List<ParkingArea>: all parking area filtered by related request
	 */
	public List<ParkingArea> getPAFilteredByZFCT(GetParkingAreaRequest parkingArea) {

		if(!validateGetParkingAreaJsonBody(parkingArea))
			throwIllegal("Invalid json body");

		if (parkingArea.getLatitudeDeparture() > parkingArea.getLatitudeDestination() &&
				parkingArea.getLongitudeDeparture() > parkingArea.getLongitudeDestination())
			return parkingAreaRepository.findParkingAreaByZFCT(
					parkingArea.getPatdId(), parkingArea.getPacId(), parkingArea.getFunctionality(),parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDestination(), parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDestination());
		else if (parkingArea.getLatitudeDeparture() > parkingArea.getLatitudeDestination())
			return parkingAreaRepository.findParkingAreaByZFCT(
					parkingArea.getPatdId(), parkingArea.getPacId(), parkingArea.getFunctionality(), parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDeparture(), parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDestination());
		else if (parkingArea.getLongitudeDeparture() > parkingArea.getLongitudeDestination())
			return parkingAreaRepository.findParkingAreaByZFCT(
					parkingArea.getPatdId(), parkingArea.getPacId(), parkingArea.getFunctionality(), parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDestination(), parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDeparture());
		else
			return parkingAreaRepository.findParkingAreaByZFCT(
					parkingArea.getPatdId(), parkingArea.getPacId(), parkingArea.getFunctionality(), parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDeparture(), parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDestination());
	}

	/**
	 * Allows user to get parking areas free between departure zone and destination zone,
	 * filtered by parking area functionality, parking area color, type and vehicle dimensions
	 * @param parkingArea: json data retrieved from body to complete request
	 * @return a List<ParkingArea>: all parking area filtered by related request
	 */
	public List<ParkingArea> getPAFilteredByZFCTV(GetParkingAreaRequest parkingArea) {

		if(!validateGetParkingAreaJsonBody(parkingArea))
			throwIllegal("Invalid json body");
		List<ParkingArea> parkingAreaToReturn;

		if (parkingArea.getLatitudeDeparture() > parkingArea.getLatitudeDestination() &&
				parkingArea.getLongitudeDeparture() > parkingArea.getLongitudeDestination())
		parkingAreaToReturn = parkingAreaRepository.findParkingAreaByZFCT(
					parkingArea.getPatdId(), parkingArea.getPacId(), parkingArea.getFunctionality(),parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDestination(), parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDestination());
		else if (parkingArea.getLatitudeDeparture() > parkingArea.getLatitudeDestination())
			parkingAreaToReturn = parkingAreaRepository.findParkingAreaByZFCT(
					parkingArea.getPatdId(), parkingArea.getPacId(), parkingArea.getFunctionality(), parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDeparture(), parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDestination());
		else if (parkingArea.getLongitudeDeparture() > parkingArea.getLongitudeDestination())
			parkingAreaToReturn = parkingAreaRepository.findParkingAreaByZFCT(
					parkingArea.getPatdId(), parkingArea.getPacId(), parkingArea.getFunctionality(), parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDestination(), parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDeparture());
		else
			parkingAreaToReturn = parkingAreaRepository.findParkingAreaByZFCT(
					parkingArea.getPatdId(), parkingArea.getPacId(), parkingArea.getFunctionality(), parkingArea.getLatitudeDeparture(), parkingArea.getLongitudeDeparture(), parkingArea.getLatitudeDestination(), parkingArea.getLongitudeDestination());
		Vehicle vehicle = vehicleRepository.findByVehicleId(parkingArea.getVehicleId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid vehicleId"));

		for(int i=0; i<parkingAreaToReturn.size(); i++) {
			if (vehicle.getModelVehicle().getLength() > parkingAreaToReturn.get(i).getParkingAreaTypeDimension().getLength() ||
					vehicle.getModelVehicle().getWidth() > parkingAreaToReturn.get(i).getParkingAreaTypeDimension().getWidth())
				parkingAreaToReturn.remove(i);
		}
		return parkingAreaToReturn;
	}

	/**
	 * Allows user to retrieve a list of parking area colors
	 * @return a List<ParkingAreaColor>: all information to send as responce for all parking area colors
	 */
	public List<ParkingAreaColor> getParkingAreaColors() {
		return parkingAreaColorRepository.findAllBy();
	}

	/**
	 * Allows user to retrieve a list of parking area type and dimension
	 * @return a List<ParkingAreaTypeDimension>: all information to send as responce for all parking area type and dimension
	 */
	public List<ParkingAreaTypeDimension> getParkingAreaTD() {
		return parkingAreaTypeDimensionRepository.findAllBy();
	}

	/**
	 * Allows user to retrieve a list of vehicle models
	 * @return a List<ModelVehicle>: all information to send as responce for all vehicle models
	 */
	public List<ModelVehicle> getModelsVehicle() {
		return modelVehicleRepository.findAllBy();
	}

	/**
	 * Allows to calculate a total price of ticket
	 * @param ticket: json data retrieved from body to complete request
	 * @param parkingArea: parking area information about price and dimensions
	 * @return a Double: total price
	 */
	private Double getTotalPrice (GetTicketRequest ticket, ParkingArea parkingArea){

		ParkingAreaColor parkingAreaColor = parkingAreaColorRepository.findById(parkingArea.getPacId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid pacId"));
		Double minutPrice = parkingAreaColor.getHourlyPrice()/60;
		Double hourlyPrice = parkingAreaColor.getHourlyPrice();
		Double dailyPrice = parkingAreaColor.getDailyPrice();
		Double weeklyPrice = parkingAreaColor.getWeeklyPrice();
		Double monthlyPrice = parkingAreaColor.getMonthlyPrice();
		return minutPrice*ticket.getMinutes() + hourlyPrice*ticket.getHours() + dailyPrice*ticket.getDays() + weeklyPrice*ticket.getWeeks() +  monthlyPrice*ticket.getMonths();
	}


	/**
	 * Allows user to upload license images
	 * @param frontPhoto: front image to upload
	 * @param retroPhoto: retro image to upload
	 * @return a ResponseEntity<ErrorResponse>: with a correct status
	 */
	public ResponseEntity<ErrorResponse> uploadLicense(MultipartFile frontPhoto, MultipartFile retroPhoto) throws IOException {

			String frontPhotoName = StringUtils.cleanPath(frontPhoto.getOriginalFilename());
			String retroPhotoName = StringUtils.cleanPath(retroPhoto.getOriginalFilename());
		  License license = licenseRepository.save(new License(frontPhoto.getBytes(), frontPhotoName, retroPhoto.getBytes(), retroPhotoName));
			Driver user = userRepository.findById(getUser().getUserId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid userId"));
			user.setLicenseId(license.getLicenseId());
			userRepository.save(user);
			return ResponseEntity.status(HttpStatus.OK).body(new ErrorResponse(200, "Uploaded successfully files : " + frontPhotoName +  " and " + retroPhotoName));
	}

	/**
	 * Allows user to download front license image
	 * @param id: the unique identifier of image
	 * @return a ResponseEntity<byte[]>: with image
	 */
	public ResponseEntity<byte[]> downloadLicenseFP(Long id) {
		if (licenseRepository.findById(id).isPresent()) {
			License license = licenseRepository.findById(id).get();
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + license.getFrontPhotoName() + "\"")
					.body(license.getFrontPhotoData());
		} else return ResponseEntity.badRequest()
				.body(null);
	}

	/**
	 * Allows user to download retro license image
	 * @param id: the unique identifier of image
	 * @return a ResponseEntity<byte[]>: with image
	 */
	public ResponseEntity<byte[]> downloadLicenseRP(Long id) {
		if (licenseRepository.findById(id).isPresent()) {
			License license = licenseRepository.findById(id).get();
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + license.getRetroPhotoName() + "\"")
					.body(license.getRetroPhotoData());
		} else return ResponseEntity.badRequest()
				.body(null);
	}


	/**
	 * Allows user get a new ticket and can proceed pay it
	 * @param ticket: json data retrieved from body to complete request
	 * @return a GetTicketResponce: all information to send as responce for the ticket generated
	 */
	public GetTicketResponce getTicket(GetTicketRequest ticket) {

		if(!validateGetTicketJsonBody(ticket))
			throwIllegal("Invalid json body");

		Driver userToGet = userRepository.findByAccountId(getUserInfo().getAccountId())
				.orElseThrow(() -> new IllegalArgumentException("Some error occurred"));

		Vehicle vehicle = vehicleRepository.findByVehicleIdAndUserId(ticket.getVehicleId(), userToGet.getUserId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid vehicleId"));

		ParkingArea parkingArea = parkingAreaRepository.findById(ticket.getPaId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid paId"));

		if(parkingArea.getStatus() == ParkingAreaStatus.Busy) {
			if (LocalDateTime.now().isAfter(parkingArea.getBusyUntil()))
					parkingArea.setStatus(ParkingAreaStatus.Free);
			parkingAreaRepository.save(parkingArea);
		}

		if(userToGet.getStatus() != Status.Approved)
			throwIllegal( "Account in Rejected or Pending status");
		else if(parkingArea.getFunctionality() != vehicle.getModelVehicle().getType())
			throwIllegal("Parking area functionality and type vehicle not match");
		else if(parkingArea.getStatus() != ParkingAreaStatus.Free && LocalDateTime.now().isBefore(parkingArea.getBusyUntil()))
			throwIllegal("Parking area busy, check the time to get ticket");
		else if(vehicle.getModelVehicle().getLength() > parkingArea.getParkingAreaTypeDimension().getLength())
			throwIllegal("Parking area length is smaller than your vehicle length");
		else if(vehicle.getModelVehicle().getWidth() > parkingArea.getParkingAreaTypeDimension().getWidth())
			throwIllegal("Parking area width is smaller than your vehicle width");

		LocalDateTime prenotationTime = LocalDateTime.now().plusMinutes(ticket.getMinutes()).plusHours(ticket.getHours())
				.plusDays(ticket.getDays()).plusWeeks(ticket.getWeeks()).plusMonths(ticket.getMonths());
		 ticketRepository.save( new Ticket(userToGet.getUserId(),
				parkingArea.getPaId(),
				null,
				vehicle.getVehicleId(),
				prenotationTime,
				getTotalPrice(ticket, parkingArea)));
		return new GetTicketResponce(prenotationTime,
					getTotalPrice(ticket,parkingArea),
				parkingArea,
				new GetVehicleResponce(vehicle.getLicensePlate(), new GetModelVehicleResponce(
						vehicle.getModelVehicle().getBrand(),
						vehicle.getModelVehicle().getName(),
						vehicle.getModelVehicle().getYear(),
						vehicle.getModelVehicle().getCv(),
						vehicle.getModelVehicle().getType(),
						vehicle.getModelVehicle().getLength(),
						vehicle.getModelVehicle().getWidth())));
	}

	/**
	 * Allows user to pay ticket got
	 * @param ticketId: the unique identifier of ticket
   * @param currency: the currency of payment
	 * @return a String: with link to redirect the user
	 */
	public String payTicket(Long ticketId, String currency) {
		ticket = ticketRepository.findById(ticketId)
				.orElseThrow(() -> new IllegalArgumentException("Some error occurred"));
		if(LocalDateTime.now().minusMinutes(3).isAfter(ticket.getDate()))
			throwIllegal("Renew ticket, because have passed already 3 minutes from your reservation");
		ParkingArea parkingAreaToRetrieve = parkingAreaRepository.findById(ticket.getPaId())
				.orElseThrow(() -> new IllegalArgumentException("Some error occurred"));
		if (parkingAreaToRetrieve.getStatus() == ParkingAreaStatus.Busy)
			throwIllegal("Parking area became busy in your waiting");
		else {
			try {
				Payment payment = payPalService.createPayment(ticket.getPrice(), currency, "paypal",
						"sale", "Easy parking payment", "http://localhost:5050/api/easyparking/user" + "/cancel-ticket",
						"http://localhost:5050/api/easyparking/user" + "/success-ticket");
				for (Links link : payment.getLinks()) {
					if (link.getRel().equals("approval_url")) {
						return "redirect:" + link.getHref();
					}
				}
			} catch (PayPalRESTException e) {
				e.printStackTrace();
			}
		}
		return "redirect:/";
	}


	/**
	 * PayPal payment of the ticket success
	 * @param paymentId: the unique identifier of payment
	 * @param payerId: the unique identifier of payer
	 * @return a String: with link to redirect the user
	 */
	public String successTicketPayment(String paymentId, String payerId) {
		try {
			Payment payment = payPalService.executePayment(paymentId, payerId);
			System.out.println(payment.toJSON());
			if (payment.getState().equals("approved")) {
				PaymentInfo paymentInfo = payPalService.insertPayment();
				ticket.setPId(paymentInfo.getPaymentId());
				ticketRepository.save(ticket);
				ParkingArea parkingAreaToUpdate = parkingAreaRepository.findById(ticket.getPaId())
						.orElseThrow(() -> new IllegalArgumentException("Some error occurred"));
					parkingAreaToUpdate.setStatus(ParkingAreaStatus.Busy);
					parkingAreaRepository.save(parkingAreaToUpdate);
				return "PayPal payment of the ticket success";
			}
		} catch (PayPalRESTException e) {
			System.out.println(e.getMessage());
		}
		return "redirect:/";
	}

	/**
	 * Allows user to get a list of his tickets
	 * @return a List<GetTicketResponce>: all tickets about the user profile
	 */
	public List<GetTicketResponce> getTickets() {

		List<Ticket> ticketList = ticketRepository.findByUserIdAndPIdNotNull(getUser().getUserId());
		List<GetTicketResponce> getTicketResponces = new ArrayList<>();
		for (Ticket ticket : ticketList) {
			getTicketResponces.add(new GetTicketResponce(
			ticket.getDeadline(),
			ticket.getPrice(),
			ticket.getParkingArea(),
			new GetVehicleResponce(ticket.getVehicle().getLicensePlate(),
					new GetModelVehicleResponce(ticket.getVehicle().getModelVehicle().getBrand(),
							ticket.getVehicle().getModelVehicle().getName(),
							ticket.getVehicle().getModelVehicle().getYear(),
							ticket.getVehicle().getModelVehicle().getCv(),
							ticket.getVehicle().getModelVehicle().getType(),
							ticket.getVehicle().getModelVehicle().getLength(),
							ticket.getVehicle().getModelVehicle().getWidth()))));
		}
		return getTicketResponces;
	}

	/**
	 * Allows user to pay fine got
	 * @param fineId: the unique identifier of fine
	 * @param currency: the currency of payment
	 * @return a String: with link to redirect the user
	 */
	public String payFine(Long fineId, String currency) {
		fine = fineRepository.findById(fineId)
				.orElseThrow(() -> new IllegalArgumentException("Some error occurred"));
		try {
			Payment payment = payPalService.createPayment(fine.getTotal(), currency, "paypal",
					"sale", "Easy parking payment", "http://localhost:5050/api/easyparking/user" + "/cancel-fine",
					"http://localhost:5050/api/easyparking/user" + "/success-fine");
			for (Links link : payment.getLinks()) {
				if (link.getRel().equals("approval_url")) {
					return "redirect:" + link.getHref();
				}
			}
		} catch (PayPalRESTException e) {
			e.printStackTrace();
		}
		return "redirect:/";
	}

	/**
	 * PayPal payment of the fine success
	 * @param paymentId: the unique identifier of payment
	 * @param payerId: the unique identifier of payer
	 * @return a String: with link to redirect the user
	 */
	public String successFinePayment(String paymentId, String payerId) {
		try {
			Payment payment = payPalService.executePayment(paymentId, payerId);
			System.out.println(payment.toJSON());
			if (payment.getState().equals("approved")) {
				PaymentInfo paymentInfo = payPalService.insertPayment();
				fine.setPId(paymentInfo.getPaymentId());
				fineRepository.save(fine);
				return "PayPal payment of the fine success";
			}
		} catch (PayPalRESTException e) {
			System.out.println(e.getMessage());
		}
		return "redirect:/";
	}

	/**
	 * Allows user to get a list of all his fines
	 * @return a List<GetFineListRespoce>: all fine about the user profile
	 */
	public List<GetFineListResponce> getFines() {
		List<Fine> fineList = fineRepository.findByUserId(getUser().getUserId());
		return createFineResponce(fineList);
	}

	/**
	 * Allows user to get a list of his fines not paid
	 * @return a List<GetFineListRespoce>: all not paid fine about the user profile
	 */
	public List<GetFineListResponce> getFinesNotPaid() {
		List<Fine> fineList = fineRepository.findByUserIdAndPIdNull(getUser().getUserId());
		return createFineResponce(fineList);
	}

	/**
	 * Allows user to get a list of his fines paid
	 * @return a List<GetFineListRespoce>: all paid fine about the user profile
	 */
	public List<GetFineListResponce> getFinesPaid() {
		List<Fine> fineList = fineRepository.findByUserIdAndPIdNotNull(getUser().getUserId());
		return createFineResponce(fineList);
	}

	/**
	 * Allows manipulating List<Fine> and get a List<GetFineListResponce>
	 * @return a List<GetFineListResponce>: used by methods that needs it
	 */
	public List<GetFineListResponce> createFineResponce(List<Fine> fineList) {
		List<GetFineListResponce> getFineRespoces = new ArrayList<>();
		for (Fine fine : fineList)
			getFineRespoces.add(new GetFineListResponce(fine.getParkingArea(),
					new GetVehicleResponce(fine.getVehicle().getLicensePlate(),
							new GetModelVehicleResponce(fine.getVehicle().getModelVehicle().getBrand(),
									fine.getVehicle().getModelVehicle().getName(),
									fine.getVehicle().getModelVehicle().getYear(),
									fine.getVehicle().getModelVehicle().getCv(),
									fine.getVehicle().getModelVehicle().getType(),
									fine.getVehicle().getModelVehicle().getLength(),
									fine.getVehicle().getModelVehicle().getWidth()
							)),
					fine.getCause(),
					fine.getDeadline(),
					fine.getRemovedPoints(),
					fine.getTotal()));
		return getFineRespoces;
	}
	private void throwIllegal(String msg) throws IllegalArgumentException {
		throw throwIllegalReturn(msg);
	}

	private IllegalArgumentException throwIllegalReturn(String msg) {
		return new IllegalArgumentException(msg);
	}

}
