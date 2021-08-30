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
public class GetTicketRequest {
	private Long vehicleId;
	private Long paId;
	private Long minutes;
	private Long hours;
	private Long days;
	private Long weeks;
	private Long months;

	public static boolean validateGetTicketJsonBody(GetTicketRequest ticket) {

		if (!ticket.getVehicleId().toString().matches(onlyNumberRegex) || ticket.getVehicleId() == null) {
			System.out.println("Json field \"vehicleId\" : " + ticket.getVehicleId() + " is invalid");
			return false;
		}

		if (!ticket.getPaId().toString().matches(onlyNumberRegex) || ticket.getPaId() == null) {
			System.out.println("Json field \"paId\" : " + ticket.getPaId() + " is invalid");
			return false;
		}

		if (ticket.getMinutes() != null) {
			if (!ticket.getMinutes().toString().matches(onlyNumberRegex)) {
				System.out.println("Json field \"minutes\" : " + ticket.getMinutes() + " is invalid");
				return false;
			}
		}

		if (ticket.getHours() != null) {
			if (!ticket.getHours().toString().matches(onlyNumberRegex)) {
				System.out.println("Json field \"hours\" : " + ticket.getHours() + " is invalid");
				return false;
			}
		}

		if (ticket.getDays() != null) {
			if (!ticket.getDays().toString().matches(onlyNumberRegex)) {
				System.out.println("Json field \"days\" : " + ticket.getDays() + " is invalid");
				return false;
			}
		}

		if (ticket.getWeeks() != null) {
			if (!ticket.getWeeks().toString().matches(onlyNumberRegex)) {
				System.out.println("Json field \"weeks\" : " + ticket.getWeeks() + " is invalid");
				return false;
			}
		}

		if (ticket.getMonths() != null) {
			if (!ticket.getMonths().toString().matches(onlyNumberRegex)) {
				System.out.println("Json field \"months\" : " + ticket.getMonths() + " is invalid");
				return false;
			}
		}

		return  true;
	}
}
