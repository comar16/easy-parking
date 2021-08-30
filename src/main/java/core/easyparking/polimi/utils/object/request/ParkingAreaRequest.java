package core.easyparking.polimi.utils.object.request;

import core.easyparking.polimi.utils.object.staticvalues.Functionality;
import core.easyparking.polimi.utils.object.staticvalues.ParkingAreaStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingAreaRequest {
	private Long pacId;
	private Long patdId;
  private Functionality functionality;
	private Double latitude;
	private Double longitude;
	private ParkingAreaStatus status;
}
