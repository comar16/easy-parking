package core.easyparking.polimi.controller;

import core.easyparking.polimi.configuration.error.ErrorResponse;
import core.easyparking.polimi.entity.ModelVehicle;
import core.easyparking.polimi.entity.ParkingArea;
import core.easyparking.polimi.entity.ParkingAreaColor;
import core.easyparking.polimi.entity.ParkingAreaTypeDimension;
import core.easyparking.polimi.service.UserService;
import core.easyparking.polimi.utils.object.request.GetTicketRequest;
import core.easyparking.polimi.utils.object.responce.*;
import core.easyparking.polimi.utils.object.request.GetParkingAreaRequest;
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
@RequestMapping("api/easyparking/user")
@SecurityRequirement(name = "JWT_User")
@PreAuthorize("hasAnyAuthority('User')")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Tag(name = "User", description = "The User API")
public class UserApiController {

	private final UserService userService;

	@GetMapping(path = "/profile", produces = "application/json")
	@Operation(summary = "Get user data")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	GetUserResponce getProfile() {
		return userService.getProfile();
	}

	@PostMapping(path = "/change-password", produces = "application/json")
	@Operation(summary = "Allows user to change password")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {
   return userService.changePassword(oldPassword,newPassword);
	}

	@PostMapping(path = "/add-vehicle", produces = "application/json")
	@Operation(summary = "Allows user to add a vehicle")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	AddVehicleResponce addVehicle(@RequestParam Long mvId, @RequestParam String licensePlate) {
		return userService.addVehicle(mvId, licensePlate);
	}

	@GetMapping(path = "/get-vehicles", produces = "application/json")
	@Operation(summary = "Allows user to get all his vehicles")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	List<GetVehiclesResponce> getVehicles() {
		return userService.getVehicles();
	}

	@PutMapping(path = "/vehicles-mv/{vehicleId}", produces = "application/json")
	@Operation(summary = "Allows user to update his vehicle model")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	AddVehicleResponce updateMV(@PathVariable("vehicleId") Long vehicleId, @RequestParam("mvId") Long mvId) {
		return userService.updateMV(vehicleId, mvId);
	}

	@PostMapping(path = "/parking-area/z", produces = "application/json")
	@Operation(summary = "Get parking area free between departure zone and destination zone")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	List<ParkingArea> getPAFilteredByZ(@RequestBody GetParkingAreaRequest getParkingArea) {
		return userService.getPAFilteredByZ(getParkingArea);
	}

	@PostMapping(path = "/parking-area/z-f", produces = "application/json")
	@Operation(summary = "Get parking area free between departure zone and destination zone, filtered by parking area functionality")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	List<ParkingArea> getPAFilteredByZF(@RequestBody GetParkingAreaRequest getParkingArea) {
		return userService.getPAFilteredByZF(getParkingArea);
	}

	@PostMapping(path = "/parking-area/z-f-c", produces = "application/json")
	@Operation(summary = "Get parking area free between departure zone and destination zone, filtered by parking area functionality and parking area color")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	List<ParkingArea> getPAFilteredByZFC(@RequestBody GetParkingAreaRequest getParkingArea) {
		return userService.getPAFilteredByZFC(getParkingArea);
	}

	@PostMapping(path = "/parking-area/z-f-c-t", produces = "application/json")
	@Operation(summary = "Get parking area free between departure zone and destination zone, filtered by parking area functionality, parking area color and type")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	List<ParkingArea> getPAFilteredByZFCT(@RequestBody GetParkingAreaRequest getParkingArea) {
		return userService.getPAFilteredByZFCT(getParkingArea);
	}

	@PostMapping(path = "/parking-area/z-f-c-t-v", produces = "application/json")
	@Operation(summary = "Get parking area free between departure zone and destination zone, filtered by parking area functionality, parking area color, type and vehicle dimensions")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	List<ParkingArea> getPAFilteredByZFCTV(@RequestBody GetParkingAreaRequest getParkingArea) {
		return userService.getPAFilteredByZFCTV(getParkingArea);
	}

	@GetMapping(path = "/get-parkingareacolors", produces = "application/json")
	@Operation(summary = "Allows user to get all parking area colors")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	List<ParkingAreaColor> getParkingAreaColors() {
		return userService.getParkingAreaColors();
	}

	@GetMapping(path = "/get-parkingareatd", produces = "application/json")
	@Operation(summary = "Allows user to get all parking area types and dimensions")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	List<ParkingAreaTypeDimension> getParkingAreaTD() {
		return userService.getParkingAreaTD();
	}

	@GetMapping(path = "/get-vehiclemodels", produces = "application/json")
	@Operation(summary = "Allows user to get all vehicle models")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	List<ModelVehicle> getModelsVehicle() {
		return userService.getModelsVehicle();
	}

	@PostMapping(path="/upload-license", produces = "application/json")
	@Operation(summary = "Allows user to upload license photos and number to get 'Approved' account")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "417", description = "Expectation Failed. Couldn't upload the file.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	ResponseEntity<ErrorResponse> uploadLicense (@RequestParam("frontPhoto") MultipartFile frontPhoto, @RequestParam("retroPhoto") MultipartFile retroPhoto) throws IOException {
		return userService.uploadLicense(frontPhoto, retroPhoto);
	}

	@GetMapping(path="/download-licenseFP/{id}", produces = "application/json")
	@Operation(summary = "Allows user to download license photos to verify if he uploaded correct files")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	ResponseEntity<byte[]> downloadLicenseFP(@PathVariable Long id){
		return userService.downloadLicenseFP(id);
	}

	@GetMapping(path="/download-licenseRP/{id}", produces = "application/json")
	@Operation(summary = "Allows user to download license photos to verify if he uploaded correct files")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	ResponseEntity<byte[]> downloadLicenseRP(@PathVariable Long id){
		return userService.downloadLicenseRP(id);
	}

	@PostMapping(path="/get-ticket", produces = "application/json")
	@Operation(summary = "Allows user get a new ticket and can proceed pay it")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	GetTicketResponce getTicket(@RequestBody GetTicketRequest ticket){
		return userService.getTicket(ticket);
	}

	@PostMapping(path="/pay-ticket/{ticketId}", produces = "application/json")
	@Operation(summary = "Allows user to pay ticket got")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	String payTicket(@PathVariable("ticketId") Long ticketId, @RequestParam("currency") String currency){
		return userService.payTicket(ticketId, currency);
	}

	@GetMapping(path="/cancel-ticket", value = "/cancel-ticket", produces = "application/json")
	@Operation(summary = "PayPal payment of the ticket failed")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	public String cancelTicketPayment() { return "PayPal payment of the ticket failed";}

	@GetMapping(path= "/success-ticket", value = "/success-ticket", produces = "application/text")
	@Operation(summary = "PayPal payment of the ticket success")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	String successTicketPayment(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
		return userService.successTicketPayment(paymentId, payerId);
	}

	@PostMapping(path="/pay-fine{fineId}", produces = "application/json")
	@Operation(summary = "Allows user to pay fine got")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	String payFine(@PathVariable("fineId") Long fineId, @RequestParam("currency") String currency){
		return userService.payFine(fineId, currency);
	}

	@GetMapping(path= "/success-fine", value = "/success-fine", produces = "application/text")
	@Operation(summary = "PayPal payment of the fine success")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	String successFinePayment(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
		return userService.successFinePayment(paymentId, payerId);
	}

	@GetMapping(path="/cancel-fine", value = "/cancel-fine", produces = "application/text")
	@Operation(summary = "PayPal payment of the fine failed")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	public String cancelFinePayment() { return "PayPal payment of the fine failed";}

	@GetMapping(path="/get-tickets", produces = "application/json")
	@Operation(summary = "Allows user get a list of his tickets")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	List<GetTicketResponce> getTickets(){
		return userService.getTickets();
	}

	@GetMapping(path="/get-fines", produces = "application/json")
	@Operation(summary = "Allows user get a list of his fines")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	List<GetFineListResponce> getFines(){
		return userService.getFines();
	}

	@GetMapping(path="/get-fines-n-p", produces = "application/json")
	@Operation(summary = "Allows user to get a list of his fines not paid")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	List<GetFineListResponce> getFinesNotPaid(){
		return userService.getFinesNotPaid();
	}

	@GetMapping(path="/get-fines-p", produces = "application/json")
	@Operation(summary = "Allows user to get a list of his fines paid")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	List<GetFineListResponce> getFinesPaid(){
		return userService.getFinesPaid();
	}
}
