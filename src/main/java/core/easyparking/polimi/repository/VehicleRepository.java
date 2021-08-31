package core.easyparking.polimi.repository;


import core.easyparking.polimi.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByVehicleId(Long vehicleId);

    Optional<Vehicle> findByLicensePlate(String licensePlate);

    List<Vehicle> findByUserId(Long userId);

    Optional<Vehicle> findByVehicleIdAndUserId(Long vehicleId, Long userId);

}
