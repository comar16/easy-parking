package core.easyparking.polimi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import core.easyparking.polimi.utils.object.request.FineRequest;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import static core.easyparking.polimi.utils.object.Regex.onlyNumberRegex;
import static core.easyparking.polimi.utils.object.Regex.totalRegex;
import static org.hibernate.annotations.OnDeleteAction.CASCADE;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Fine implements Serializable {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fineId;

    @Basic
    @Column(name = "userId")
    private Long userId;

    @Basic
    @Column(name = "paId")
    private Long paId;

    @Basic
    @Column(name = "pId")
    private Long pId;

    @Basic
    @Column(name = "vehicleId")
    private Long vehicleId;

    @Basic
    @Column(nullable = false, length = 1024)
    private String cause;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime date;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime deadline;

    @Basic
    @Column(nullable = false)
    private Integer removedPoints;

    @Basic
    @Column(columnDefinition="Decimal(6,2)")
    private Double total;

    public Fine(Long userId, Long paId, Long pId, Long vehicleId, String cause, LocalDateTime deadline, Integer removedPoints, Double total) {
       this.userId = userId;
       this.paId = paId;
       this.pId = pId;
       this.vehicleId = vehicleId;
       this.cause = cause;
       this.deadline = deadline;
       this.removedPoints = removedPoints;
       this.total = total;
    }

    @PrePersist
    public void prePersist() {this.date = LocalDateTime.now().plusHours(2);}

    @OnDelete(action = CASCADE)
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "userId", referencedColumnName = "userId", insertable = false, updatable = false)
    private Driver user;

    @OnDelete(action = CASCADE)
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "paId", referencedColumnName = "paId", insertable = false, updatable = false)
    private ParkingArea parkingArea;

    @OnDelete(action = CASCADE)
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "pId", referencedColumnName = "paymentId", insertable = false, updatable = false)
    private PaymentInfo payment;

    @OnDelete(action = CASCADE)
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "vehicleId", referencedColumnName = "vehicleId", insertable = false, updatable = false)
    private Vehicle vehicle;

    public static boolean validateFineJsonFields(FineRequest fine) {

        if (!fine.getUserId().toString().matches(onlyNumberRegex) || fine.getUserId() == null) {
            System.out.println("Json field \"userId\" : " + fine.getUserId() + " is invalid");
            return false;
        }

        if (!fine.getPaId().toString().matches(onlyNumberRegex) || fine.getPaId() == null) {
            System.out.println("Json field \"paId\" : " + fine.getUserId() + " is invalid");
            return false;
        }

        if (!fine.getVehicleId().toString().matches(onlyNumberRegex) || fine.getVehicleId() == null) {
            System.out.println("Json field \"vehicleId\" : " + fine.getVehicleId() + " is invalid");
            return false;
        }

        if (!fine.getRemovedPoints().toString().matches(onlyNumberRegex) || fine.getRemovedPoints() == null) {
            System.out.println("Json field \"removedPoints\" : " + fine.getRemovedPoints() + " is invalid");
            return false;
        }

        if (!fine.getTotal().toString().matches(totalRegex) || fine.getTotal() == null) {
            System.out.println("Json field \"total\" : " + fine.getTotal() + " is invalid");
            return false;
        }
        return  true;
    }

}

