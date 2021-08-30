package core.easyparking.polimi.utils.object.responce;

import core.easyparking.polimi.entity.Admin;
import core.easyparking.polimi.utils.object.staticvalues.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetAdminResponce{
	private String name;
	private String surname;
	private Long pcId;
	private Status status;

	public GetAdminResponce(Admin admin) {
		this.name = admin.getName();
		this.surname = admin.getSurname();
		this.pcId = admin.getPcId();
		this.status = admin.getStatus();
	}
}
