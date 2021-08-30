package core.easyparking.polimi.utils.object.responce;

import core.easyparking.polimi.entity.ModelVehicle;
import core.easyparking.polimi.entity.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddVehicleResponce {
	private String licensePlate;
	private ModelVehicle modelVehicle;
	private GetUserResponce user;

	public AddVehicleResponce(Vehicle vehicle) {
		this.licensePlate = licensePlate;
		this.modelVehicle = modelVehicle;
		this.user = user;
	}
}
