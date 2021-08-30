package core.easyparking.polimi.service;

import core.easyparking.polimi.configuration.error.ErrorResponse;
import core.easyparking.polimi.entity.*;
import core.easyparking.polimi.repository.*;
import core.easyparking.polimi.service.jwt.JWTService;
import core.easyparking.polimi.utils.object.staticvalues.ParkingAreaStatus;
import core.easyparking.polimi.utils.object.staticvalues.Role;
import core.easyparking.polimi.utils.object.request.FineRequest;
import core.easyparking.polimi.utils.object.request.ParkingAreaRequest;
import core.easyparking.polimi.utils.object.responce.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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
import java.util.List;

import static core.easyparking.polimi.entity.Fine.validateFineJsonFields;
import static core.easyparking.polimi.entity.ParkingArea.validatePAJsonFields;
import static core.easyparking.polimi.entity.ParkingAreaColor.*;
import static core.easyparking.polimi.entity.ParkingAreaTypeDimension.validatePATDJsonFields;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class AdminService {
	private final AccountRepository accountRepository;
	private final AdminRepository adminRepository;
	private final UserRepository userRepository;
	private final ParkingAreaColorRepository parkingAreaColorRepository;
	private final ParkingAreaTypeDimensionRepository parkingAreaTypeDimensionRepository;
	private final ParkingAreaRepository parkingAreaRepository;
	private final FineRepository fineRepository;
	private final UserService userService;
	private final PoliceCardRepository policeCardRepository;



	/**
	 * Allows to retrieve all admin data
	 * @return a Admin: all information of certain admin
	 */
	private Admin getAdmin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			User user = ((User) authentication.getPrincipal());

			if (!user.getAuthorities().contains(new SimpleGrantedAuthority(Role.Admin.name()))) {
				throw new JWTService.TokenVerificationException();
			}

			Account account = accountRepository.findByRoleAndUsername(Role.Admin, user.getUsername())
					.orElseThrow(JWTService.TokenVerificationException::new);
			return adminRepository.findByAccountId(account.getAccountId())
					.orElseThrow(JWTService.TokenVerificationException::new);
		}
		throw new JWTService.TokenVerificationException();
	}

	/**
	 * Allows to retrieve all profile admin data
	 * @return a GetAdminResponce: all information to send as responce for a certain admin
	 */
	public GetAdminResponce getProfile() {
		return new GetAdminResponce(getAdmin().getName(), getAdmin().getSurname(), getAdmin().getPcId(), getAdmin().getStatus());
	}

	/**
	 * Allows to retrieve a list of admins data
	 * @return a GetAdminResponce: all information to send as responce for all admins
	 */
	public List<GetAdminListResponce> getAllAdmins() {

		List<Admin> adminList = adminRepository.findAllBy();
		List<GetAdminListResponce> getAdminListResponces = new ArrayList<>();
		for (Admin admin : adminList) {
			if (admin.getPoliceCard() == null) {
				getAdminListResponces.add(new GetAdminListResponce(admin.getName(),
						admin.getSurname(),
						null,
						admin.getStatus(),
						new GetAccountResponce(admin.getAccount().getUsername(), admin.getAccount().getDateReset(), admin.getAccount().getRole())));
			} else {
				getAdminListResponces.add(new GetAdminListResponce(admin.getName(),
						admin.getSurname(),
						new GetPoliceCardResponce(admin.getPoliceCard().getFrontPhotoPCName(), admin.getPoliceCard().getRetroPhotoPCName(), admin.getPoliceCard().getDateUpload()),
						admin.getStatus(),
						new GetAccountResponce(admin.getAccount().getUsername(), admin.getAccount().getDateReset(), admin.getAccount().getRole())));
			}
		}
		return getAdminListResponces;
	}

	/**
	 * Allows to retrieve a list of users data
	 * @return a List<GetUserListResponce>: all information to send as responce for all users
	 */
	public List<GetUserListResponce> getAllUsers() {
		List<Driver> userList = userRepository.findAllBy();
		List<GetUserListResponce> getUserListResponces = new ArrayList<>();
		for (Driver user : userList) {
			if (user.getLicense() == null) {
				getUserListResponces.add(new GetUserListResponce(user.getName(),
						user.getSurname(),
						null,
						user.getStatus(),
						new GetAccountResponce(user.getAccount().getUsername(), user.getAccount().getDateReset(), user.getAccount().getRole())));
			} else {
				getUserListResponces.add(new GetUserListResponce(user.getName(),
						user.getSurname(),
						new GetLicenseResponce(user.getLicense().getFrontPhotoName(), user.getLicense().getRetroPhotoName(), user.getLicense().getDateUpload()),
						user.getStatus(),
						new GetAccountResponce(user.getAccount().getUsername(), user.getAccount().getDateReset(), user.getAccount().getRole())));
			}
		}
		return getUserListResponces;
	}

	/**
	 * Allows admin to retrieve a list of parking area colors
	 * @return a List<ParkingAreaColor>: all information to send as responce for all parking area colors
	 */
	public List<GetParkingAreaColorResponce> getAllParkingAreaColor() {

		List<ParkingAreaColor> pacList = parkingAreaColorRepository.findAllBy();
		List<GetParkingAreaColorResponce> getPACResponces = new ArrayList<>();
		for (ParkingAreaColor pac : pacList) {
			getPACResponces.add(new GetParkingAreaColorResponce(pac.getColor(),
					pac.getHourlyPrice(),
					pac.getDailyPrice(),
					pac.getWeeklyPrice(),
					pac.getMonthlyPrice()));
		}
		return getPACResponces;
	}

	/**
	 * Allows admin to create a new parking area color
	 * @param parkingAreaColor: json data retrieved from body to complete request
	 * @return a ParkingAreaColor: all information to send as responce for the new parking area color
	 */
	public GetParkingAreaColorResponce createParkingAreaColor(GetParkingAreaColorResponce parkingAreaColor) {

		if (!validatePACJsonFields(parkingAreaColor))
			throw new IllegalArgumentException("Invalid json body");
		ParkingAreaColor pacToCreate = new ParkingAreaColor(
				parkingAreaColor.getColor(),
				parkingAreaColor.getHourlyPrice(),
				parkingAreaColor.getDailyPrice(),
				parkingAreaColor.getWeeklyPrice(),
				parkingAreaColor.getMonthlyPrice());
		parkingAreaColorRepository.save(pacToCreate);

		return new GetParkingAreaColorResponce(pacToCreate.getColor(),
				pacToCreate.getHourlyPrice(),
				pacToCreate.getDailyPrice(),
				pacToCreate.getWeeklyPrice(),
				pacToCreate.getMonthlyPrice());
	}

	/**
	 * Allows admin to update a certain parking area color
	 * @param pacId: the unique identifier of parking area color
	 * @param parkingAreaColor: json data retrieved from body to complete request
	 * @return a ParkingAreaColor: all information to send as responce for the updated parking area color
	 */
	public GetParkingAreaColorResponce updateParkingAreaColor(Long pacId, GetParkingAreaColorResponce parkingAreaColor) {

		if (!validatePACJsonFields(parkingAreaColor))
			throw new IllegalArgumentException("Invalid json body");
		ParkingAreaColor pacToUpdate = parkingAreaColorRepository.findById(pacId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid pacId"));
		pacToUpdate.setColor(parkingAreaColor.getColor());
		pacToUpdate.setHourlyPrice(parkingAreaColor.getHourlyPrice());
		pacToUpdate.setDailyPrice(parkingAreaColor.getDailyPrice());
		pacToUpdate.setWeeklyPrice(parkingAreaColor.getWeeklyPrice());
		pacToUpdate.setMonthlyPrice(parkingAreaColor.getMonthlyPrice());
		parkingAreaColorRepository.save(pacToUpdate);

		return new GetParkingAreaColorResponce(pacToUpdate.getColor(),
				pacToUpdate.getHourlyPrice(),
				pacToUpdate.getDailyPrice(),
				pacToUpdate.getWeeklyPrice(),
				pacToUpdate.getMonthlyPrice());
	}

	/**
	 * Allows admin to delete a certain parking area color
	 * @param pacId: the unique identifier of parking area color
	 */
	public void deleteParkingAreaColor(Long pacId) {
		 parkingAreaColorRepository.findById(pacId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid pacId"));
		parkingAreaColorRepository.deleteById(pacId);
	}

	/**
	 * Allows admin to retrieve a list of parking area type and dimensions
	 * @return a List<ParkingAreaTypeDimension>: all information to send as responce for all parking area colors
	 */
	public List<GetParkingAreaTypeDimensionResponce> getAllParkingAreaTypeDimension() {
		List<ParkingAreaTypeDimension> patdList = parkingAreaTypeDimensionRepository.findAllBy();
		List<GetParkingAreaTypeDimensionResponce> getPATDResponces = new ArrayList<>();
		for (ParkingAreaTypeDimension patd : patdList)
			getPATDResponces.add(new GetParkingAreaTypeDimensionResponce(patd.getType(), patd.getLength(), patd.getWidth()));

		return getPATDResponces;
	}

	/**
	 * Allows admin to create a new parking area type and dimensions
	 * @param parkingAreaTypeDimension: json data retrieved from body to complete request
	 * @return a ParkingAreaTypeDimension: all information to send as responce for the new parking area type and dimensions
	 */
	public GetParkingAreaTypeDimensionResponce createParkingAreaTypeDimensions(GetParkingAreaTypeDimensionResponce parkingAreaTypeDimension) {
		if (!validatePATDJsonFields(parkingAreaTypeDimension))
			throw new IllegalArgumentException("Invalid json body");
		ParkingAreaTypeDimension patdToCreate = new ParkingAreaTypeDimension(
				parkingAreaTypeDimension.getType(),
				parkingAreaTypeDimension.getLength(),
				parkingAreaTypeDimension.getWidth());
		parkingAreaTypeDimensionRepository.save(patdToCreate);

		return new GetParkingAreaTypeDimensionResponce(patdToCreate.getType(),
				patdToCreate.getLength(),
				patdToCreate.getWidth());
	}

	/**
	 * Allows admin to update a certain parking area type and dimensions
	 * @param patdId: the unique identifier of parking area type and dimensions
	 * @param parkingAreaTypeDimension: json data retrieved from body to complete request
	 * @return a ParkingAreaColor: all information to send as responce for the updated parking area type and dimensions
	 */
	public GetParkingAreaTypeDimensionResponce updateParkingAreaTypeDimension(Long patdId, GetParkingAreaTypeDimensionResponce parkingAreaTypeDimension) {

		if (!validatePATDJsonFields(parkingAreaTypeDimension))
			throw new IllegalArgumentException("Invalid json body");
		ParkingAreaTypeDimension patdToUpdate = parkingAreaTypeDimensionRepository.findById(patdId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid patdId"));
		patdToUpdate.setType(parkingAreaTypeDimension.getType());
		patdToUpdate.setLength(parkingAreaTypeDimension.getLength());
		patdToUpdate.setWidth(parkingAreaTypeDimension.getWidth());
		parkingAreaTypeDimensionRepository.save(patdToUpdate);

		return new GetParkingAreaTypeDimensionResponce(patdToUpdate.getType(),
				patdToUpdate.getLength(),
				patdToUpdate.getWidth());
	}

	/**
	 * Allows admin to delete a certain parking area type and dimensions
	 * @param patdId: the unique identifier of parking area type and dimensions
	 */
	public void deleteParkingAreaTypeDimension(Long patdId) {
		parkingAreaTypeDimensionRepository.findById(patdId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid patdId"));
		parkingAreaTypeDimensionRepository.deleteById(patdId);
	}

	/**
	 * Allows admin to retrieve a list of parking area
	 * @return a List<ParkingArea>: all information to send as responce for all parking area
	 */
	public List<ParkingArea> getAllParkingArea() {
		return parkingAreaRepository.findAllFromParkingArea();
	}

	/**
	 * Allows admin to create a new parking area
	 * @param parkingArea: json data retrieved from body to complete request
	 * @return a ParkingArea: all information to send as responce for the new parking area
	 */
	public ParkingArea createParkingArea(ParkingAreaRequest parkingArea) {
		if (!validatePAJsonFields(parkingArea))
			throw new IllegalArgumentException("Invalid json body");

		return parkingAreaRepository.save(new ParkingArea(
				parkingArea.getPacId(),
				parkingArea.getPatdId(),
				parkingArea.getFunctionality(),
				parkingArea.getLatitude(),
				parkingArea.getLongitude(),
				parkingArea.getStatus()));
	}

	/**
	 * Allows admin to update a certain parking area
	 * @param paId: the unique identifier of parking area
	 * @param parkingArea: json data retrieved from body to complete request
	 * @return a ParkingAreaColor: all information to send as responce for the updated parking area
	 */
	public ParkingArea updateParkingArea(Long paId, ParkingAreaRequest parkingArea) {
		if (!validatePAJsonFields(parkingArea))
			throw new IllegalArgumentException("Invalid json body");
		ParkingArea parkingAreaToUpdate = parkingAreaRepository.findById(paId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid paId"));
		parkingAreaToUpdate.setPacId(parkingArea.getPacId());
		parkingAreaToUpdate.setPatdId(parkingArea.getPatdId());
		parkingAreaToUpdate.setFunctionality(parkingArea.getFunctionality());
		parkingAreaToUpdate.setLatitude(parkingArea.getLatitude());
		parkingAreaToUpdate.setLongitude(parkingArea.getLongitude());
		parkingAreaToUpdate.setStatus(parkingArea.getStatus());

		return parkingAreaRepository.save(parkingAreaToUpdate);
	}

	/**
	 * Allows admin to delete a certain parking area
	 * @param paId: the unique identifier of parking area
	 */
	public void deleteParkingArea(Long paId) {
		ParkingArea parkingAreaToDelete = parkingAreaRepository.findById(paId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid paId"));
		parkingAreaToDelete.setStatus(ParkingAreaStatus.Deleted);
		parkingAreaRepository.save(parkingAreaToDelete);
	}

	/**
	 * Allows admin to update hourly price of a certain parking area color
	 * @param pacId: the unique identifier of parking area color
	 * @param hourlyPrice: the new price to update
	 * @return a GetParkingAreaColorResponce: all information to send as responce for the updated parking area color
	 */
	public GetParkingAreaColorResponce updateHourlyPricePAC(Long pacId, Double hourlyPrice) {
		if (!validateHourlyPrice(hourlyPrice))
			throw new IllegalArgumentException("Invalid hourlyPrice value");
		ParkingAreaColor hourlyPricePACToUpdate = parkingAreaColorRepository.findById(pacId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid pacId"));
		hourlyPricePACToUpdate.setHourlyPrice(hourlyPrice);
		parkingAreaColorRepository.save(hourlyPricePACToUpdate);

		return new GetParkingAreaColorResponce(hourlyPricePACToUpdate.getColor(),
				hourlyPricePACToUpdate.getHourlyPrice(),
				hourlyPricePACToUpdate.getDailyPrice(),
				hourlyPricePACToUpdate.getWeeklyPrice(),
				hourlyPricePACToUpdate.getMonthlyPrice());
	}

	/**
	 * Allows admin to update daily price of a certain parking area color
	 * @param pacId: the unique identifier of parking area color
	 * @param dailyPrice: the new price to update
	 * @return a GetParkingAreaColorResponce: all information to send as responce for the updated parking area color
	 */
	public GetParkingAreaColorResponce updateDailyPricePAC(Long pacId, Double dailyPrice) {
		if (!validateDailyPrice(dailyPrice))
			throw new IllegalArgumentException("Invalid dailyPrice value");
		ParkingAreaColor dailyPricePACToUpdate = parkingAreaColorRepository.findById(pacId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid pacId"));
		dailyPricePACToUpdate.setDailyPrice(dailyPrice);
		parkingAreaColorRepository.save(dailyPricePACToUpdate);
		return new GetParkingAreaColorResponce(dailyPricePACToUpdate.getColor(),
				dailyPricePACToUpdate.getHourlyPrice(),
				dailyPricePACToUpdate.getDailyPrice(),
				dailyPricePACToUpdate.getWeeklyPrice(),
				dailyPricePACToUpdate.getMonthlyPrice());
	}

	/**
	 * Allows admin to update weekly price of a certain parking area color
	 * @param pacId: the unique identifier of parking area color
	 * @param weeklyPrice: the new price to update
	 * @return a GetParkingAreaColorResponce: all information to send as responce for the updated parking area color
	 */
	public GetParkingAreaColorResponce updateWeeklyPricePAC(Long pacId, Double weeklyPrice) {
		if (!validateWeeklyPrice(weeklyPrice))
			throw new IllegalArgumentException("Invalid weeklyPrice value");
		ParkingAreaColor weeklyPricePACToUpdate = parkingAreaColorRepository.findById(pacId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid pacId"));
		weeklyPricePACToUpdate.setWeeklyPrice(weeklyPrice);
		parkingAreaColorRepository.save(weeklyPricePACToUpdate);
		return new GetParkingAreaColorResponce(weeklyPricePACToUpdate.getColor(),
				weeklyPricePACToUpdate.getHourlyPrice(),
				weeklyPricePACToUpdate.getDailyPrice(),
				weeklyPricePACToUpdate.getWeeklyPrice(),
				weeklyPricePACToUpdate.getMonthlyPrice());
	}

	/**
	 * Allows admin to update monthly price of a certain parking area color
	 * @param pacId: the unique identifier of parking area color
	 * @param monthlyPrice: the new price to update
	 * @return a GetParkingAreaColorResponce: all information to send as responce for the updated parking area color
	 */
	public GetParkingAreaColorResponce updateMonthlyPricePAC(Long pacId, Double monthlyPrice) {
		if (!validateMonthlyPrice(monthlyPrice))
			throw new IllegalArgumentException("Invalid monthlyPrice value");
		ParkingAreaColor monthlyPricePACToUpdate = parkingAreaColorRepository.findById(pacId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid pacId"));
		monthlyPricePACToUpdate.setMonthlyPrice(monthlyPrice);
		parkingAreaColorRepository.save(monthlyPricePACToUpdate);
		return new GetParkingAreaColorResponce(monthlyPricePACToUpdate.getColor(),
				monthlyPricePACToUpdate.getHourlyPrice(),
				monthlyPricePACToUpdate.getDailyPrice(),
				monthlyPricePACToUpdate.getWeeklyPrice(),
				monthlyPricePACToUpdate.getMonthlyPrice());
	}

	/**
	 * Allows admin to set free a certain parking area color
	 * @param pacId: the unique identifier of parking area color
	 * @return a GetParkingAreaColorResponce: all information to send as responce for the updated parking area color
	 */
	public GetParkingAreaColorResponce setFreePAC(Long pacId) {
		ParkingAreaColor PACToGetFree = parkingAreaColorRepository.findById(pacId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid pacId"));
		PACToGetFree.setHourlyPrice(0.0);
		PACToGetFree.setDailyPrice(0.0);
		PACToGetFree.setWeeklyPrice(0.0);
		PACToGetFree.setMonthlyPrice(0.0);
		parkingAreaColorRepository.save(PACToGetFree);
		return new GetParkingAreaColorResponce(PACToGetFree.getColor(),
				PACToGetFree.getHourlyPrice(),
				PACToGetFree.getDailyPrice(),
				PACToGetFree.getWeeklyPrice(),
				PACToGetFree.getMonthlyPrice());
	}

	/**
	 * Allows admin to set as 'Damaged' a certain parking area
	 * @param paId: the unique identifier of parking area
	 * @return a ParkingArea: all information to send as responce for the updated parking area
	 */
	public ParkingArea setPADamaged(Long paId) {
		ParkingArea PAToSet = parkingAreaRepository.findById(paId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid paId"));
		PAToSet.setStatus(ParkingAreaStatus.Damaged);

		return parkingAreaRepository.save(PAToSet);
	}

	/**
	 * Allows admin to create a new fine
	 * @param fine: json data retrieved from body to complete request
	 * @return a GetFineRespoce: all information to send as responce for the new fine
	 */
	public GetFineResponce createFine(FineRequest fine) {
		if (!validateFineJsonFields(fine))
			throw new IllegalArgumentException("Invalid json body");
		Fine fineToCreate = new Fine(
				fine.getUserId(),
				fine.getPaId(),
				null,
				fine.getVehicleId(),
				fine.getCause(),
				fine.getDeadline(),
				fine.getRemovedPoints(),
				fine.getTotal());
		fineRepository.save(fineToCreate);
		return updateCreateFineResponce(fineToCreate);
	}

	/**
	 * Allows admin to retrieve a list of a fines related to a certain user
	 * @return a List<Fine>: all information to send as responce for all fines
	 */
	public List<GetFineListResponce> getFines(Long userId) {
		List<Fine> fineList = fineRepository.findByUserId(userId);
		return userService.createFineResponce(fineList);
	}

	/**
	 * Allows admin to update a certain fine
	 * @param fineId: the unique identifier of fine
	 * @param fine: json data retrieved from body to complete request
	 * @return a GetFineRespoce: all information to send as responce for the updated fine
	 */
	public GetFineResponce updateFine(Long fineId, FineRequest fine) {
		Fine fineToUpdate = fineRepository.findByFineId(fineId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid fineId"));
		if (!validateFineJsonFields(fine))
			throw new IllegalArgumentException("Invalid json body");
		fineToUpdate.setUserId(fine.getUserId());
		fineToUpdate.setPaId(fine.getPaId());
		fineToUpdate.setVehicleId(fine.getVehicleId());
		fineToUpdate.setCause(fine.getCause());
		fineToUpdate.setDeadline(fine.getDeadline());
		fineToUpdate.setRemovedPoints(fine.getRemovedPoints());
		fineToUpdate.setTotal(fine.getTotal());
		fineRepository.save(fineToUpdate);
		return updateCreateFineResponce(fineToUpdate);
	}

	/**
	 * Allows manipulating Fine and get a GetFineResponce
	 * @return a GetFineResponce: used by methods that needs it
	 */
	public GetFineResponce updateCreateFineResponce(Fine fine) {
		fineRepository.save(fine);
		GetFineResponce fineToReturn;
		if(fine.getUser().getLicense() == null){
			fineToReturn = new GetFineResponce(
					new GetUserListResponce(fine.getUser().getName(),
							fine.getUser().getSurname(),
							null,
							fine.getUser().getStatus(),
							new GetAccountResponce(fine.getUser().getAccount().getUsername(),
									fine.getUser().getAccount().getDateReset(),
									fine.getUser().getAccount().getRole())
					),
					fine.getParkingArea(),
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
					fine.getTotal());
		} else {
			fineToReturn = new GetFineResponce(
					new GetUserListResponce(fine.getUser().getName(),
							fine.getUser().getSurname(),
							new GetLicenseResponce(fine.getUser().getLicense().getFrontPhotoName(),
									fine.getUser().getLicense().getRetroPhotoName(),
									fine.getUser().getLicense().getDateUpload()),
							fine.getUser().getStatus(),
							new GetAccountResponce(fine.getUser().getAccount().getUsername(),
									fine.getUser().getAccount().getDateReset(),
									fine.getUser().getAccount().getRole())
					),
					fine.getParkingArea(),
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
					fine.getTotal());
		}
		return fineToReturn;
	}

	/**
	 * Allows viewing parking area busy without ticket
	 * @return a List<ParkingArea>: with all information about parking areas
	 */
	public List<ParkingArea> getBusyPA() {
		List<ParkingArea> parkingAreaList = parkingAreaRepository.findForStatus();

		for (ParkingArea pa : parkingAreaList) {
			System.out.println("parkingAreaList = " + pa);
			double random = (Math.random()*100);
			System.out.println("random = " + random);
			if(pa.getStatus().equals(ParkingAreaStatus.Free) && random>50)
				parkingAreaList.remove(pa);
			else if(LocalDateTime.now().isBefore(pa.getBusyUntil()))
				parkingAreaList.remove(pa);
			else if(random>50)
				parkingAreaList.remove(pa);
		}
		return parkingAreaList;
	}

	/**
	 * Allows admin to upload police card images
	 * @param frontPhoto: front image to upload
	 * @param retroPhoto: retro image to upload
	 * @return a ResponseEntity<ErrorResponse>: with a correct status
	 */
	public ResponseEntity<ErrorResponse> uploadPoliceCard(MultipartFile frontPhoto, MultipartFile retroPhoto) throws IOException {

		String frontPhotoName = StringUtils.cleanPath(frontPhoto.getOriginalFilename());
		String retroPhotoName = StringUtils.cleanPath(retroPhoto.getOriginalFilename());
		PoliceCard policeCard = policeCardRepository.save(new PoliceCard(frontPhoto.getBytes(), frontPhotoName, retroPhoto.getBytes(), retroPhotoName));
		Admin admin = adminRepository.findById(getAdmin().getAdminId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));
		admin.setPcId(policeCard.getPcId());
		adminRepository.save(admin);
		return ResponseEntity.status(HttpStatus.OK).body(new ErrorResponse(200, "Uploaded successfully files : " + frontPhotoName +  " and " + retroPhotoName));
	}
	/**
	 * Allows admin to download front police crad image
	 * @param id: the unique identifier of image
	 * @return a ResponseEntity<byte[]>: with image
	 */
	public ResponseEntity<byte[]> downloadPoliceCardFP(Long id) {
		if (policeCardRepository.findById(id).isPresent()) {
			PoliceCard policeCard = policeCardRepository.findById(id).get();
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + policeCard.getFrontPhotoPCName() + "\"")
					.body(policeCard.getFrontPhotoPCdata());
		} else return ResponseEntity.badRequest()
				.body(null);
	}

	/**
	 * Allows user to download retro police card image
	 * @param id: the unique identifier of image
	 * @return a ResponseEntity<byte[]>: with image
	 */
	public ResponseEntity<byte[]> downloadPoliceCardRP(Long id) {
		if (policeCardRepository.findById(id).isPresent()) {
			PoliceCard policeCard = policeCardRepository.findById(id).get();
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + policeCard.getRetroPhotoPCName() + "\"")
					.body(policeCard.getRetroPhotoPCdata());
		} else return ResponseEntity.badRequest()
				.body(null);
	}

}
