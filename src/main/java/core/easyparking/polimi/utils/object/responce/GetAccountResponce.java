package core.easyparking.polimi.utils.object.responce;

import com.fasterxml.jackson.annotation.JsonFormat;
import core.easyparking.polimi.utils.object.staticvalues.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetAccountResponce {
	private String username;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss")
	private LocalDateTime dateReset;
	private Role role;
}
