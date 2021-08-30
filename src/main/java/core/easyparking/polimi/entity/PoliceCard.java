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
@Table(name="police_card")
public class PoliceCard implements Serializable {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pcId;

    @Lob
    @Column(nullable = false)
    private byte[] frontPhotoPCdata;

    @Basic
    @Column(nullable = false, length = 64)
    private String frontPhotoPCName;

    @Lob
    @Column(nullable = false)
    private byte[] retroPhotoPCdata;

    @Basic
    @Column(nullable = false, length = 64)
    private String retroPhotoPCName;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dateUpload;

    public PoliceCard(byte[] frontPhotoPCdata, String frontPhotoPCName, byte[] retroPhotoPCdata,String retroPhotoPCName) {
        this.frontPhotoPCdata = frontPhotoPCdata;
        this.frontPhotoPCName = frontPhotoPCName;
        this.retroPhotoPCdata = retroPhotoPCdata;
        this.retroPhotoPCName = retroPhotoPCName;
    }

    @PrePersist
    public void prePersist() {this.dateUpload = LocalDateTime.now().plusHours(2);}

}
