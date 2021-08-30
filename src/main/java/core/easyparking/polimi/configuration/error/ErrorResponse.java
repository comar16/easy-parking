package core.easyparking.polimi.configuration.error;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ErrorResponse implements Serializable {
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
	public final LocalDateTime timestamp;
	public final Integer status;
	public final String message;

	public ErrorResponse(Integer status, String message) {
		this.timestamp = LocalDateTime.now();
		this.status = status;
		this.message = message;
	}
}
