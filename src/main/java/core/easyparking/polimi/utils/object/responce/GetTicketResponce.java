package core.easyparking.polimi.utils.object.responce;

import com.fasterxml.jackson.annotation.JsonFormat;
import core.easyparking.polimi.entity.ParkingArea;
import core.easyparking.polimi.entity.Vehicle;
import core.easyparking.polimi.utils.object.request.GetTicketRequest;
import core.easyparking.polimi.utils.object.responce.GetVehicleResponce;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetTicketResponce {
	public GetTicketResponce(GetTicketRequest getTicketRequest) {}

	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime deadline;
	private Double price;
	private ParkingArea parkingArea;
	private GetVehicleResponce vehicle;
}
