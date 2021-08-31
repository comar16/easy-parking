package core.easyparking.polimi.repository;

import core.easyparking.polimi.entity.PoliceCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoliceCardRepository extends JpaRepository<PoliceCard, Long> {

}