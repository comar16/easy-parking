package core.easyparking.polimi.entity;

import core.easyparking.polimi.utils.object.staticvalues.Type;
import core.easyparking.polimi.utils.object.responce.GetParkingAreaTypeDimensionResponce;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

import static core.easyparking.polimi.utils.object.Regex.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name="parking_area_type_dimension")
public class ParkingAreaTypeDimension implements Serializable {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long patdId;

    @Basic
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Basic
    @Column(columnDefinition="Decimal(4,2)")
    private Double length;

    @Basic
    @Column(columnDefinition="Decimal(4,2)")
    private Double width;

    public ParkingAreaTypeDimension(Type type, Double length, Double width) {
        this.type = type;
        this.length = length;
        this.width = width;
    }

    public static boolean validatePATDJsonFields(GetParkingAreaTypeDimensionResponce patd) {

        if (!patd.getType().toString().matches(typeRegex) || patd.getType() == null) {
            System.out.println("Json field \"type\" : " + patd.getType() + " is invalid");
            return false;
        }

        if (!patd.getLength().toString().matches(lengthRegex) || patd.getLength() == null) {
            System.out.println("Json field \"length\" : " + patd.getLength() + " is invalid");
            return false;
        }

        if (!patd.getWidth().toString().matches(widthRegex) || patd.getWidth() == null) {
            System.out.println("Json field \"width\" : " + patd.getWidth() + " is invalid");
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

    public static boolean validateLength(Double length) {

        if (!length.toString().matches(lengthRegex) || length == null) {
            System.out.println("Json field \"length\" : " + length + " is invalid");
            return false;
        }
        return  true;
    }

    public static boolean validateWidth(Double width) {

        if (!width.toString().matches(widthRegex) || width == null) {
            System.out.println("Json field \"width\" : " + width + " is invalid");
            return false;
        }
        return  true;
    }

}
