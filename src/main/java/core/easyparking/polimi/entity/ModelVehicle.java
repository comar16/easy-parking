package core.easyparking.polimi.entity;

import core.easyparking.polimi.utils.object.staticvalues.Functionality;
import lombok.*;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name="model_vehicle")
public class ModelVehicle implements Serializable {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mvId;

    @Basic
    @Column(nullable = false, length = 64)
    private String brand;

    @Basic
    @Column(nullable = false, length = 64)
    private String name;

    @Basic
    @Column(nullable = false)
    private Integer year;

    @Basic
    @Column(nullable = false)
    private Integer cv;

    @Basic
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Functionality type;

    @Basic
    @Column(columnDefinition="Decimal(4,2)")
    private Double length;

    @Basic
    @Column(columnDefinition="Decimal(4,2)")
    private Double width;

    public ModelVehicle(String brand, String name, Integer year, Integer cv, Functionality type, Double length, Double width) {
        this.brand = brand;
        this.name = name;
        this.year = year;
        this.cv = cv;
        this.type = type;
        this.length = length;
        this.width = width;
    }
}
