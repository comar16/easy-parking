package core.easyparking.polimi.repository;


import core.easyparking.polimi.entity.ParkingArea;
import core.easyparking.polimi.utils.object.staticvalues.Functionality;
import core.easyparking.polimi.utils.object.staticvalues.ParkingAreaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingAreaRepository extends JpaRepository<ParkingArea, Long> {

	@Query("SELECT a FROM ParkingArea a WHERE a.status <> \'Damaged\'")
	List<ParkingArea> findAllFromParkingArea();

	Optional<ParkingArea> findById(Long paId);

	void deleteById(Long paId);

	@Query("SELECT a FROM ParkingArea a WHERE a.latitude >= :latitudeDeparture AND a.latitude <= :latitudeDestination AND " +
			"a.longitude  >= :longitudeDeparture AND a.longitude <= :longitudeDestination AND a.status = \'Free\'")
	List<ParkingArea> findParkingAreaByZ
			(Double latitudeDeparture, Double longitudeDeparture, Double latitudeDestination, Double longitudeDestination);

	@Query("SELECT a FROM ParkingArea a WHERE a.functionality =:functionality AND a.latitude >= :latitudeDeparture" +
			" AND a.latitude <= :latitudeDestination AND a.longitude  >= :longitudeDeparture" +
			" AND a.longitude <= :longitudeDestination AND a.status = \'Free\'")
	List<ParkingArea> findParkingAreaByZF
			(Functionality functionality, Double latitudeDeparture, Double longitudeDeparture, Double latitudeDestination, Double longitudeDestination);

	@Query("SELECT a FROM ParkingArea a WHERE a.pacId =:pacId AND a.functionality =:functionality " +
			"AND a.latitude >= :latitudeDeparture AND a.latitude <= :latitudeDestination AND a.longitude  >= :longitudeDeparture " +
			"AND a.longitude <= :longitudeDestination AND a.status = \'Free\'")
	List<ParkingArea> findParkingAreaByZFC
			(Long pacId, Functionality functionality, Double latitudeDeparture, Double longitudeDeparture, Double latitudeDestination, Double longitudeDestination);

	@Query("SELECT a FROM ParkingArea a WHERE a.patdId =:patdId AND a.pacId =:pacId AND a.functionality =:functionality" +
			" AND a.latitude >= :latitudeDeparture AND a.latitude <= :latitudeDestination AND a.longitude  >= :longitudeDeparture" +
			" AND a.longitude <= :longitudeDestination AND a.status = \'Free\'")
	List<ParkingArea> findParkingAreaByZFCT
			(Long patdId, Long pacId, Functionality functionality, Double latitudeDeparture, Double longitudeDeparture, Double latitudeDestination, Double longitudeDestination);

	@Query("SELECT a FROM ParkingArea a WHERE a.status = \'Free\' OR a.status = \'Busy\'")
	List<ParkingArea> findForStatus();
}