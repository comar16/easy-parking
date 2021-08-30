package core.easyparking.polimi.controller;

import core.easyparking.polimi.configuration.error.ErrorResponse;
import core.easyparking.polimi.entity.ParkingArea;
import core.easyparking.polimi.service.AdminService;
import core.easyparking.polimi.utils.object.request.FineRequest;
import core.easyparking.polimi.utils.object.request.ParkingAreaRequest;
import core.easyparking.polimi.utils.object.responce.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/easyparking/admin")
@SecurityRequirement(name = "JWT_Admin")
@PreAuthorize("hasAnyAuthority('Admin')")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Tag(name = "Admin", description = "The Admin API")
public class AdminApiController {

	private final AdminService adminService;

	@GetMapping(path = "/profile", produces = "application/json")
	@Operation(summary = "Get admin profile data")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	GetAdminResponce getProfile() {
		return adminService.getProfile();
	}

	@GetMapping(path = "/list-users", produces = "application/json")
	@Operation(summary = "Get list of users")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	List<GetUserListResponce> getAllUsers () {
		return adminService.getAllUsers();
	}

	@GetMapping(path = "/list-admins", produces = "application/json")
	@Operation(summary = "Get list of admins")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	List<GetAdminListResponce> getAllAdmin() {
		return adminService.getAllAdmins();
	}

	@GetMapping(path = "/parking-area-color", produces = "application/json")
	@Operation(summary = "Get list of parking area colors")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	List<GetParkingAreaColorResponce> getAllParkingAreaColor() {
		return adminService.getAllParkingAreaColor();
	}

	@PostMapping(path = "/parking-area-color", produces = "application/json")
	@Operation(summary = "Create new parking area color")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	GetParkingAreaColorResponce createParkingAreaColor(@RequestBody() GetParkingAreaColorResponce parkingAreaColor) {
		return adminService.createParkingAreaColor(parkingAreaColor);
	}

	@PutMapping(path = "/parking-area-color/{pacId}", produces = "application/json")
	@Operation(summary = "Update values of a parking area color")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	GetParkingAreaColorResponce updateParkingAreaColor(@PathVariable("pacId") Long pacId, @RequestBody() GetParkingAreaColorResponce parkingAreaColor) {
		return adminService.updateParkingAreaColor(pacId, parkingAreaColor);
	}

	@DeleteMapping(path = "/parking-area-color/{pacId}")
	@Operation(summary = "Delete a parking area color")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	void deleteParkingAreaColor(@PathVariable("pacId") Long pacId) {
		adminService.deleteParkingAreaColor(pacId);
	}

	@GetMapping(path = "/parking-area-type-dimension", produces = "application/json")
	@Operation(summary = "Get list of parking area types and dimensions")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	List<GetParkingAreaTypeDimensionResponce> getAllParkingAreaTypeDimension() {
		return adminService.getAllParkingAreaTypeDimension();
	}

	@PostMapping(path = "/parking-area-type-dimension", produces = "application/json")
	@Operation(summary = "Create new parking area type and dimensions")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	GetParkingAreaTypeDimensionResponce createParkingAreaTypeDimensions(@RequestBody() GetParkingAreaTypeDimensionResponce parkingAreaTypeDimension) {
		return adminService.createParkingAreaTypeDimensions(parkingAreaTypeDimension);
	}

	@PutMapping(path = "/parking-area-type-dimension/{patdId}", produces = "application/json")
	@Operation(summary = "Update values of a parking area type and dimensions")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	GetParkingAreaTypeDimensionResponce updateParkingAreaTypeDimension(@PathVariable("patdId") Long patdId, @RequestBody() GetParkingAreaTypeDimensionResponce parkingAreaTypeDimension) {
		return adminService.updateParkingAreaTypeDimension(patdId, parkingAreaTypeDimension);
	}

	@DeleteMapping(path = "/parking-area-type-dimension/{patdId}")
	@Operation(summary = "Delete a parking area type and dimensions")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	void deleteParkingAreaTypeDimension(@PathVariable("patdId") Long patdId) {
		adminService.deleteParkingAreaTypeDimension(patdId);
	}

	@GetMapping(path = "/parking-area", produces = "application/json")
	@Operation(summary = "Get list of parking areas")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	List<ParkingArea> getAllParkingArea() {
		return adminService.getAllParkingArea();
	}

	@PostMapping(path = "/parking-area", produces = "application/json")
	@Operation(summary = "Create new parking area")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	ParkingArea createParkingArea(@RequestBody() ParkingAreaRequest parkingArea) {
		return adminService.createParkingArea(parkingArea);
	}

	@PutMapping(path = "/parking-area/{paId}", produces = "application/json")
	@Operation(summary = "Update values of a parking area ")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	ParkingArea updateParkingArea(@PathVariable("paId") Long paId, @RequestBody() ParkingAreaRequest parkingArea) {
		return adminService.updateParkingArea(paId, parkingArea);
	}

	@DeleteMapping(path = "parking-area/{paId}")
	@Operation(summary = "Delete a parking area")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	void deleteParkingArea(@PathVariable("paId") Long paId) {
		adminService.deleteParkingArea(paId);
	}

	@PutMapping(path = "/pac/hourly-price/{pacId}", produces = "application/json")
	@Operation(summary = "Update hourly price value of a parking area color")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	GetParkingAreaColorResponce updateHourlyPricePAC(@PathVariable("pacId") Long pacId, @RequestParam("hourlyPrice") Double hourlyPrice) {
		return adminService.updateHourlyPricePAC(pacId, hourlyPrice);
	}

	@PutMapping(path = "/pac/daily-price/{pacId}", produces = "application/json")
	@Operation(summary = "Update daily price value of a parking area color")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	GetParkingAreaColorResponce updateDailyPricePAC(@PathVariable("pacId") Long pacId, @RequestParam("dailyPrice") Double dailyPrice) {
		 return adminService.updateDailyPricePAC(pacId, dailyPrice);
	}

	@PutMapping(path = "/pac/weekly-price/{pacId}", produces = "application/json")
	@Operation(summary = "Update weekly price value of a parking area color")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	GetParkingAreaColorResponce updateWeeklyPricePAC(@PathVariable("pacId") Long pacId, @RequestParam("weeklyPrice") Double weeklyPrice) {
		return adminService.updateWeeklyPricePAC(pacId, weeklyPrice);
	}

	@PutMapping(path = "/pac/monthly-price/{pacId}", produces = "application/json")
	@Operation(summary = "Update monthly price value of a parking area color")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	GetParkingAreaColorResponce updateMonthlyPricePAC(@PathVariable("pacId") Long pacId, @RequestParam("monthlyPrice") Double monthlyPrice) {
		return adminService.updateMonthlyPricePAC(pacId, monthlyPrice);
	}

	@PutMapping(path = "/pac/zero-price/{pacId}", produces = "application/json")
	@Operation(summary = "Update hourly/daily/weekly/monthly price to zero value of a parking area color")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	GetParkingAreaColorResponce setFreePAC(@PathVariable("pacId") Long pacId) {
		return adminService.setFreePAC(pacId);
	}

	@PutMapping(path = "/parking-area/status/{paId}", produces = "application/json")
	@Operation(summary = "Update parking area status to 'Damaged'")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	ParkingArea setPADamaged(@PathVariable("paId") Long paId) {
		return adminService.setPADamaged(paId);
	}

	@GetMapping(path = "/fine/{userId}", produces = "application/json")
	@Operation(summary = "Get list of fines referred to certain user")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	List<GetFineListResponce> getFine(@PathVariable("userId") Long userId) {
		return adminService.getFines(userId);
	}

	@PostMapping(path = "/fine", produces = "application/json")
	@Operation(summary = "Create a new fine referred to certain user")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	GetFineResponce createFine(@RequestBody() FineRequest fine) {
		return adminService.createFine(fine);
	}

	@PutMapping(path = "/fine/{fineId}", produces = "application/json")
	@Operation(summary = "Update values of certain fine")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	GetFineResponce updateFine(@PathVariable("fineId") Long fineId, @RequestBody() FineRequest fine) {
		return adminService.updateFine(fineId, fine);
	}

	@GetMapping(path = "/get-busy", produces = "application/json")
	@Operation(summary = "Get list of parking area busy without a ticket")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	List<ParkingArea> getBusyPA() {return adminService.getBusyPA();}

	@PostMapping(path="/upload-police-card", produces = "application/json")
	@Operation(summary = "Allows admin to upload police card photos to get 'Approved' account")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "417", description = "Expectation Failed. Couldn't upload the file.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	ResponseEntity<ErrorResponse> uploadLicense (@RequestParam("frontPhoto") MultipartFile frontPhoto, @RequestParam("retroPhoto") MultipartFile retroPhoto) throws IOException {
		return adminService.uploadPoliceCard(frontPhoto, retroPhoto);
	}

	@GetMapping(path="/download-policecard-f-p/{id}", produces = "application/json")
	@Operation(summary = "Allows admin to download police card photos to verify if he uploaded correct files")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	ResponseEntity<byte[]> downloadPoliceCardFP(@PathVariable Long id){
		return adminService.downloadPoliceCardFP(id);
	}

	@GetMapping(path="/download-policecard-r-p/{id}", produces = "application/json")
	@Operation(summary = "Allows user to download police card photos to verify if he uploaded correct files")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	ResponseEntity<byte[]> downloadPoliceCardRP(@PathVariable Long id){
		return adminService.downloadPoliceCardRP(id);
	}

}
