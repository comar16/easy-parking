package core.easyparking.polimi.repository;


import core.easyparking.polimi.entity.ModelVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ModelVehicleRepository extends JpaRepository<ModelVehicle, Long> {


    ModelVehicle findByMvId(Long mvId);

    List<ModelVehicle> findAllBy();
}
