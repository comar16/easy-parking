package core.easyparking.polimi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class License implements Serializable {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long licenseId;

    @Lob
    @Column(nullable = false)
    private byte[] frontPhotoData;

    @Basic
    @Column(nullable = false, length = 64)
    private String frontPhotoName;

    @Lob
    @Column(nullable = false)
    private byte[] retroPhotoData;

    @Basic
    @Column(nullable = false, length = 64)
    private String retroPhotoName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime dateUpload;

    public License(byte[] frontPhotoData, String frontPhotoName, byte[] retroPhotoData, String retroPhotoName) {
        this.frontPhotoData = frontPhotoData;
        this.frontPhotoName = frontPhotoName;
        this.retroPhotoData = retroPhotoData;
        this.retroPhotoName = retroPhotoName;
    }

    @PrePersist
    public void prePersist() {this.dateUpload = LocalDateTime.now().plusHours(2);}

}
