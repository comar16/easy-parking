package core.easyparking.polimi.utils.object.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static core.easyparking.polimi.utils.object.Regex.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
	private String username;
	private String password;
	private String name;
	private String surname;

	public static boolean validateRegisterRequestJsonFields(RegisterRequest registerRequest) {

		if (!registerRequest.getUsername().matches(usernameRegex) || registerRequest.getUsername() == null) {
			System.out.println("Json field \"username\" : " + registerRequest.getUsername() + " is invalid");
			return false;
		}

		if (!registerRequest.getPassword().matches(passwordRegex) || registerRequest.getPassword() == null) {
			System.out.println("Json field \"password\" : " + registerRequest.getPassword() + " is invalid");
			return false;
		}

		if (!registerRequest.getName().matches(nameRegex) || registerRequest.getName() == null) {
			System.out.println("Json field \"name\" : " + registerRequest.getName() + " is invalid");
			return false;
		}

		if (!registerRequest.getSurname().matches(surnameRegex) || registerRequest.getSurname() == null) {
			System.out.println("Json field \"surname\" : " + registerRequest.getSurname() + " is invalid");
			return false;
		}
		return  true;
	}
}
