package core.easyparking.polimi.repository;


import core.easyparking.polimi.entity.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {

    List<Fine> findByUserId(Long userId);

    Optional<Fine> findByFineId(Long fineId);

    @Query("SELECT a FROM Fine a WHERE a.userId =:userId AND a.pId IS NOT NULL ")
    List<Fine> findByUserIdAndPIdNotNull(Long userId);

    @Query("SELECT a FROM Fine a WHERE a.userId =:userId AND a.pId IS NULL")
    List<Fine> findByUserIdAndPIdNull(Long userId);

}