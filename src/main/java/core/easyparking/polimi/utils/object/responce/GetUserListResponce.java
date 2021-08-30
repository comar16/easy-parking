package core.easyparking.polimi.utils.object.responce;

import core.easyparking.polimi.utils.object.staticvalues.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetUserListResponce {
	private String name;
	private String surname;
	private GetLicenseResponce license;
	private Status status;
	private GetAccountResponce account;
}
