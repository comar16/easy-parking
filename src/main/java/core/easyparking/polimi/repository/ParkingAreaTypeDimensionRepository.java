package core.easyparking.polimi.repository;


import core.easyparking.polimi.entity.ParkingAreaColor;
import core.easyparking.polimi.entity.ParkingAreaTypeDimension;
import core.easyparking.polimi.utils.object.staticvalues.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingAreaTypeDimensionRepository extends JpaRepository<ParkingAreaTypeDimension, Long> {

	List<ParkingAreaTypeDimension> findAllBy();

	Optional<ParkingAreaTypeDimension> findById(Long patdId);

	void deleteById(Long patdId);

	ParkingAreaTypeDimension findByTypeAndLengthAndWidth(Type type, double length, double width);

}