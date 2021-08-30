package core.easyparking.polimi.controller;

import core.easyparking.polimi.configuration.error.ErrorResponse;
import core.easyparking.polimi.service.PublicService;
import core.easyparking.polimi.utils.object.request.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("api/easyparking")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Tag(name = "Public", description = "The Public API")
public class PublicApiController {

	private final PublicService publicService;

	@PostMapping(path = "/register/user")
	@Operation(summary = "User Register")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation", content = @Content(mediaType = "text/plain")),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public String register(
			@RequestBody RegisterRequest data) {
		return publicService.register(data);
	}

	@PostMapping(path = "/login/user")
	@Operation(summary = "User Login")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation", content = @Content(mediaType = "text/plain")),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public String loginUser(
			@RequestParam("username") String username,
			@RequestParam("password") String password) {
		return publicService.loginUser(username, password);
	}

	@PostMapping(path = "/login/admin")
	@Operation(summary = "Admin Login")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation", content = @Content(mediaType = "text/plain")),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public String loginAdmin(
			@RequestParam("username") String username,
			@RequestParam("password") String password) {
		return publicService.loginAdmin(username, password);
	}


	@PostMapping(path = "/reset/user")
	@Operation(summary = "User Password Reset")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public void resetUser(
			@RequestParam("username") String username) {
		publicService.resetUser(username);
	}

	@PostMapping(path = "/reset/admin")
	@Operation(summary = "Admin Password Reset")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public void resetAdmin(
			@RequestParam("username") String username) {
			publicService.resetAdmin(username);
	}

}
