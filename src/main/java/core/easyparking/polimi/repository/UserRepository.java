package core.easyparking.polimi.repository;

import core.easyparking.polimi.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Driver, Long> {

	Optional<Driver> findByAccountId(Long accountId);

	List<Driver> findAllBy();

}
