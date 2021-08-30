package core.easyparking.polimi.utils.object.responce;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetVehicleResponce {
	private String licensePlate;
	private GetModelVehicleResponce getModelVehicleResponce;
}
