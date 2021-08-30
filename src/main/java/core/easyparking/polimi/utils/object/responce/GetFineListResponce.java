package core.easyparking.polimi.utils.object.responce;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class GetFineListResponce {
	private ParkingArea parkingArea;
	private GetVehicleResponce vehicleId;
	private String cause;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime deadline;
	private Integer removedPoints;
	private Double total;
}
