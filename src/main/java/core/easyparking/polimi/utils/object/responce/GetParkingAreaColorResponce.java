package core.easyparking.polimi.utils.object.responce;

import core.easyparking.polimi.entity.ParkingArea;
import core.easyparking.polimi.entity.ParkingAreaColor;
import core.easyparking.polimi.utils.object.staticvalues.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetParkingAreaColorResponce {
	private Color color;

	public GetParkingAreaColorResponce(ParkingAreaColor parkingAreaColor) {
		this.color = parkingAreaColor.getColor();
		this.hourlyPrice = parkingAreaColor.getHourlyPrice();
		this.dailyPrice = parkingAreaColor.getDailyPrice();
		this.weeklyPrice = parkingAreaColor.getWeeklyPrice();
		this.monthlyPrice = parkingAreaColor.getMonthlyPrice();
	}

	private Double hourlyPrice;
	private Double dailyPrice;
	private Double weeklyPrice;
	private Double monthlyPrice;
}
