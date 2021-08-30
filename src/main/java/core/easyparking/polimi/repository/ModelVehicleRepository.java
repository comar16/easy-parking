package core.easyparking.polimi.repository;


import core.easyparking.polimi.entity.Admin;
import core.easyparking.polimi.entity.ModelVehicle;
import core.easyparking.polimi.entity.ParkingAreaColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModelVehicleRepository extends JpaRepository<ModelVehicle, Long> {


    ModelVehicle findByMvId(Long mvId);

    List<ModelVehicle> findAllBy();
}
