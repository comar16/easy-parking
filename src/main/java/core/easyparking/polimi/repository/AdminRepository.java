package core.easyparking.polimi.repository;

import core.easyparking.polimi.entity.Admin;
import core.easyparking.polimi.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

	Optional<Admin> findByAccountId(Long accountId);

	List<Admin> findAllBy();

}
