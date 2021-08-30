package core.easyparking.polimi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import core.easyparking.polimi.utils.object.staticvalues.Color;
import core.easyparking.polimi.utils.object.staticvalues.Functionality;
import core.easyparking.polimi.utils.object.staticvalues.ParkingAreaStatus;
import core.easyparking.polimi.utils.object.staticvalues.Type;
import core.easyparking.polimi.utils.object.request.ParkingAreaRequest;
import lombok.*;
import org.hibernate.annotations.OnDelete;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import static core.easyparking.polimi.utils.object.Regex.*;
import static org.hibernate.annotations.OnDeleteAction.CASCADE;
import static org.hibernate.annotations.OnDeleteAction.NO_ACTION;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name="parking_area")
public class ParkingArea implements Serializable {

    @Id
    @Column(name = "paId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paId;

    @Basic
    @Column(name = "pacId", nullable = false)
    private Long pacId;

    @Basic
    @Column(name = "patdId", nullable = false)
    private Long patdId;

    @Basic
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Functionality functionality;

    @Basic
    @Column(columnDefinition="Decimal(9,7)")
    private Double latitude;

    @Basic
    @Column(columnDefinition="Decimal(9,7)")
    private Double longitude;

    @Basic
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ParkingAreaStatus status;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime busyUntil;

    public ParkingArea(Long pacId, Long patdId, Functionality functionality, Double latitude, Double longitude, ParkingAreaStatus status) {
        this.pacId = pacId;
        this.patdId = patdId;
        this.functionality = functionality;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    public ParkingArea(Long pacId, Long patdId, Functionality functionality, Double latitude, Double longitude, ParkingAreaStatus status, LocalDateTime busyUntil) {
        this.pacId = pacId;
        this.patdId = patdId;
        this.functionality = functionality;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.busyUntil = busyUntil;
    }

    @OnDelete(action = CASCADE)
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "pacId", referencedColumnName = "pacId", insertable = false, updatable = false)
    private ParkingAreaColor parkingAreaColor;

    @OnDelete(action = CASCADE)
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "patdId", referencedColumnName = "patdId", insertable = false, updatable = false)
    private ParkingAreaTypeDimension parkingAreaTypeDimension;

    public static boolean validatePAJsonFields(ParkingAreaRequest parkingArea) {

        if (!parkingArea.getPacId().toString().matches(onlyNumberRegex) || parkingArea.getPacId() == null) {
            System.out.println("Json field \"pacId\" : " + parkingArea.getPacId() + " is invalid");
            return false;
        }

        if (!parkingArea.getPatdId().toString().matches(onlyNumberRegex) || parkingArea.getPatdId() == null) {
            System.out.println("Json field \"patdId\" : " + parkingArea.getPatdId() + " is invalid");
            return false;
        }

        if (!parkingArea.getFunctionality().toString().matches(functionalityRegex) || parkingArea.getFunctionality() == null) {
            System.out.println("Json field \"functionality\" : " + parkingArea.getFunctionality() + " is invalid");
            return false;
        }

        if (!parkingArea.getLatitude().toString().matches(latitudeRegex) || parkingArea.getLatitude() == null) {
            System.out.println("Json field \"latitude\" : " + parkingArea.getLatitude() + " is invalid");
            return false;
        }

        if (!parkingArea.getLongitude().toString().matches(longitudeRegex) || parkingArea.getLongitude() == null) {
            System.out.println("Json field \"longitude\" : " + parkingArea.getLongitude() + " is invalid");
            return false;
        }

        if (!parkingArea.getStatus().toString().matches(parkingAreaStatusRegex) || parkingArea.getStatus() == null) {
            System.out.println("Json field \"status\" : " + parkingArea.getStatus() + " is invalid");
            return false;
        }

        return  true;
    }

    public static boolean validateType(Type type) {

        if (!type.toString().matches(typeRegex) || type == null) {
            System.out.println("Json field \"type\" : " + type + " is invalid");
            return false;
        }
        return  true;
    }

    public static boolean validateLatitude(Double latitude) {

        if (!latitude.toString().matches(latitudeRegex) || latitude == null) {
            System.out.println("Json field \"latitude\" : " + latitude + " is invalid");
            return false;
        }
        return  true;
    }

    public static boolean validateLongitude(Double longitude) {

        if (!longitude.toString().matches(longitudeRegex) || longitude == null) {
            System.out.println("Json field \"latitude\" : " + longitude + " is invalid");
            return false;
        }
        return  true;
    }

    public static boolean validateFunctionality(Functionality functionality) {

        if (!functionality.toString().matches(functionalityRegex) || functionality == null) {
            System.out.println("Json field \"functionality\" : " + functionality + " is invalid");
            return false;
        }
        return  true;
    }
}

