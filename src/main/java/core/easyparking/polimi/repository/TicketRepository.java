package core.easyparking.polimi.repository;


import core.easyparking.polimi.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT a FROM Ticket a WHERE a.userId =:userId AND a.pId IS NOT NULL ")
    List<Ticket> findByUserIdAndPIdNotNull(Long userId);
}