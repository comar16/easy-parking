package core.easyparking.polimi.repository;


import core.easyparking.polimi.entity.ParkingAreaColor;
import core.easyparking.polimi.utils.object.staticvalues.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingAreaColorRepository extends JpaRepository<ParkingAreaColor, Long> {

	List<ParkingAreaColor> findAllBy();

	Optional<ParkingAreaColor> findById(Long pacId);

	void deleteById(Long pacId);

	ParkingAreaColor findByColorAndAndHourlyPriceAndDailyPriceAndWeeklyPriceAndMonthlyPrice(Color color, Double hourlyPrice, Double dailyPrice, Double weeklyPrice, Double monthlyPrice);

}