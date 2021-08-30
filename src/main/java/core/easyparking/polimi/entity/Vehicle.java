package core.easyparking.polimi.entity;

import lombok.*;
import org.hibernate.annotations.OnDelete;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static core.easyparking.polimi.utils.object.Regex.licensePlateRegex;
import static core.easyparking.polimi.utils.object.Regex.onlyNumberRegex;
import static org.hibernate.annotations.OnDeleteAction.CASCADE;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Vehicle implements Serializable {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehicleId;

    @Basic
    @Column(name = "mvId", nullable = false)
    private Long mvId;

    @Basic
    @Column(name = "userId", nullable = false)
    private Long userId;

    @Basic
    @Column(nullable = false, length = 10)
    private String licensePlate;

    public Vehicle(Long mvId, Long userId, String licensePlate) {
        this.mvId = mvId;
        this.userId = userId;
        this.licensePlate = licensePlate;
    }

    @OnDelete(action = CASCADE)
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "mvId", referencedColumnName = "mvId", insertable = false, updatable = false)
    private ModelVehicle modelVehicle;

    @ManyToOne
    @OnDelete(action = CASCADE)
    @JoinColumn(name = "userId", referencedColumnName = "userId", insertable = false, updatable = false)
    private Driver user;

    public static boolean validateVehicleJsonField(Vehicle vehicle) {

        if (!vehicle.getMvId().toString().matches(onlyNumberRegex) || vehicle.getMvId() == null) {
            System.out.println("Json field \"mvId\" : " + vehicle.getMvId() + " is invalid");
            return false;
        }

        if (!vehicle.getLicensePlate().matches(licensePlateRegex) || vehicle.getLicensePlate() == null || vehicle.getLicensePlate().strip().isEmpty()) {
            System.out.println("Json field \"licensePlate\" : " + vehicle.getLicensePlate() + " is invalid");
            return false;
        }

        if (!vehicle.getUserId().toString().matches(onlyNumberRegex) || vehicle.getUserId() == null) {
            System.out.println("Json field \"userId\" : " + vehicle.getUserId() + " is invalid");
            return false;
        }

        return  true;
    }
}

