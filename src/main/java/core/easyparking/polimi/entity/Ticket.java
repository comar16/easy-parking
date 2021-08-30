package core.easyparking.polimi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.OnDelete;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import static org.hibernate.annotations.OnDeleteAction.CASCADE;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Ticket implements Serializable {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @Basic
    @Column(name = "userId", nullable = false)
    private Long userId;

    @Basic
    @Column(name = "paId", nullable = false)
    private Long paId;

    @Basic
    @Column(name = "pId")
    private Long pId;

    @Basic
    @Column(name = "vehicleId", nullable = false)
    private Long vehicleId;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime date;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime deadline;

    @Basic
    @Column(columnDefinition="Decimal(6,2)")
    private Double price;

    public Ticket(Long userId, Long paId, Long pId, Long vehicleId, LocalDateTime deadline, Double price) {
       this.userId = userId;
       this.paId = paId;
       this.pId = pId;
       this.vehicleId = vehicleId;
       this.deadline = deadline.plusHours(2);
       this.price = price;
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
}
