package core.easyparking.polimi.utils.object.responce;

import core.easyparking.polimi.entity.ParkingArea;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetFineResponce {
	private GetUserListResponce user;
	private ParkingArea parkingArea;
	private GetVehicleResponce vehicleId;
	private String cause;
	private LocalDateTime deadline;
	private Integer removedPoints;
	private Double total;
}
