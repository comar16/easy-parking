package core.easyparking.polimi.utils.object.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FineRequest {
	private Long userId;
	private Long paId;
	private Long vehicleId;
	private String cause;
	private LocalDateTime deadline;
	private Integer removedPoints;
	private Double total;
}
