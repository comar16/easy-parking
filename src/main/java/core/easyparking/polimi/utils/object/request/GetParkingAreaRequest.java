package core.easyparking.polimi.utils.object.request;

import core.easyparking.polimi.utils.object.staticvalues.Functionality;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static core.easyparking.polimi.utils.object.Regex.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetParkingAreaRequest {
	private Functionality functionality;
	private Double latitudeDeparture;
	private Double longitudeDeparture;
	private Double latitudeDestination;
	private Double longitudeDestination;
	private Long vehicleId;
  private Long pacId;
	private Long patdId;


	public static boolean validateGetParkingAreaJsonBody(GetParkingAreaRequest parkingArea) {

		if (parkingArea.getLatitudeDeparture() != null) {
			if (!parkingArea.getLatitudeDeparture().toString().matches(latitudeRegex)) {
				System.out.println("Json field \"latitudeDeparture\" : " + parkingArea.getLatitudeDeparture() + " is invalid");
				return false;
			}
		}

		if (parkingArea.getLongitudeDeparture() != null) {
			if (!parkingArea.getLongitudeDeparture().toString().matches(longitudeRegex)) {
				System.out.println("Json field \"longitudeDeparture\" : " + parkingArea.getLongitudeDeparture() + " is invalid");
				return false;
			}
		}

		if (parkingArea.getLatitudeDestination() != null) {
			if (!parkingArea.getLatitudeDestination().toString().matches(latitudeRegex)) {
				System.out.println("Json field \"latitudeDestination\" : " + parkingArea.getLatitudeDestination() + " is invalid");
				return false;
			}
		}

		if (parkingArea.getLongitudeDestination() != null) {
			if (!parkingArea.getLongitudeDestination().toString().matches(longitudeRegex)) {
				System.out.println("Json field \"longitudeDestination\" : " + parkingArea.getLongitudeDestination() + " is invalid");
				return false;
			}
		}

		if (parkingArea.getFunctionality() != null) {
			if (!parkingArea.getFunctionality().toString().matches(functionalityRegex)) {
				System.out.println("Json field \"functionality\" : " + parkingArea.getFunctionality() + " is invalid");
				return false;
			}
		}

		if (parkingArea.getVehicleId() != null) {
			if (!parkingArea.getVehicleId().toString().matches(onlyNumberRegex)) {
				System.out.println("Json field \"vehicleId\" : " + parkingArea.getVehicleId() + " is invalid");
				return false;
			}
		}

		if (parkingArea.getPacId() != null) {
			if (!parkingArea.getPacId().toString().matches(onlyNumberRegex)) {
				System.out.println("Json field \"pacId\" : " + parkingArea.getPacId() + " is invalid");
				return false;
			}
		}

		if (parkingArea.getPatdId() != null) {
			if (!parkingArea.getPatdId().toString().matches(onlyNumberRegex)) {
				System.out.println("Json field \"patdId\" : " + parkingArea.getPatdId() + " is invalid");
				return false;
			}
		}

		return  true;
	}
}
