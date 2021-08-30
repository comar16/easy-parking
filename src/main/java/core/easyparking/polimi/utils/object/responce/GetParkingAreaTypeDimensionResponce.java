package core.easyparking.polimi.utils.object.responce;

import core.easyparking.polimi.utils.object.staticvalues.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetParkingAreaTypeDimensionResponce {
	private Type type;
	private Double length;
	private Double width;
}
