package core.easyparking.polimi.repository;


import core.easyparking.polimi.entity.PaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentInfoRepository extends JpaRepository<PaymentInfo, Long> {

	Optional<PaymentInfo> findById(Long pId);
}