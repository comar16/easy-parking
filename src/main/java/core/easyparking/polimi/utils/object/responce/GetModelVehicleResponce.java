package core.easyparking.polimi.utils.object.responce;

import core.easyparking.polimi.utils.object.staticvalues.Functionality;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetModelVehicleResponce {
	private String brand;
	private String name;
	private Integer year;
	private Integer cv;
	private Functionality type;
	private Double length;
	private Double width;
}
