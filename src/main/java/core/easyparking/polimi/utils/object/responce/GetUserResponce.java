package core.easyparking.polimi.utils.object.responce;

import core.easyparking.polimi.entity.Driver;
import core.easyparking.polimi.utils.object.staticvalues.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetUserResponce {
	private String name;
	private String surname;
	private Long licenseId;
	private Status status;

	public GetUserResponce(Driver user) {
		this.name = name;
		this.surname = surname;
		this.licenseId = licenseId;
		this.status = status;
	}
}
