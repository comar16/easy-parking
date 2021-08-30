package core.easyparking.polimi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import core.easyparking.polimi.utils.object.staticvalues.Color;
import core.easyparking.polimi.utils.object.responce.GetParkingAreaColorResponce;
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
@Table(name="parking_area_color")
public class ParkingAreaColor implements Serializable {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pacId;

    @Basic
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Color color;

    @Basic
    @Column(columnDefinition="Decimal(5,2)")
    private Double hourlyPrice;

    @Basic
    @Column(columnDefinition="Decimal(5,2)")
    private Double dailyPrice;

    @Basic
    @Column(columnDefinition="Decimal(6,2)")
    private Double weeklyPrice;

    @Basic
    @Column(columnDefinition="Decimal(6,2)")
    private Double monthlyPrice;

    public ParkingAreaColor(Color color, Double hourlyPrice, Double dailyPrice, Double weeklyPrice, Double monthlyPrice) {
        this.color = color;
        this.hourlyPrice = hourlyPrice;
        this.dailyPrice = dailyPrice;
        this.weeklyPrice = weeklyPrice;
        this.monthlyPrice = monthlyPrice;
    }

    public static boolean validatePACJsonFields(GetParkingAreaColorResponce pac) {

        if (!pac.getColor().toString().matches(colorRegex) || pac.getColor() == null) {
            System.out.println("Json field \"color\" : " + pac.getColor() + " is invalid");
            return false;
        }

        if (!pac.getHourlyPrice().toString().matches(hourlyPriceRegex) || pac.getHourlyPrice() == null) {
            System.out.println("Json field \"hourlyPrice\" : " + pac.getHourlyPrice() + " is invalid");
            return false;
        }

        if (!pac.getDailyPrice().toString().matches(dailyPriceRegex) || pac.getDailyPrice() == null) {
            System.out.println("Json field \"dailyPrice\" : " + pac.getDailyPrice() + " is invalid");
            return false;
        }

        if (!pac.getWeeklyPrice().toString().matches(weeklyPriceRegex) || pac.getWeeklyPrice() == null) {
            System.out.println("Json field \"weeklyPrice\" : " + pac.getWeeklyPrice() + " is invalid");
            return false;
        }

        if (!pac.getMonthlyPrice().toString().matches(monthlyPriceRegex) || pac.getMonthlyPrice() == null) {
            System.out.println("Json field \"monthlyPrice\" : " + pac.getMonthlyPrice() + " is invalid");
            return false;
        }

        return  true;
    }

    public static boolean validateHourlyPrice(Double hourlyPrice) {

        if (!hourlyPrice.toString().matches(hourlyPriceRegex) || hourlyPrice == null) {
            System.out.println("Json field \"hourlyPrice\" : " + hourlyPrice + " is invalid");
            return false;
        }
        return  true;
    }

    public static boolean validateDailyPrice(Double dailyPrice) {

        if (!dailyPrice.toString().matches(dailyPriceRegex) || dailyPrice == null) {
            System.out.println("Json field \"dailyPrice\" : " + dailyPrice + " is invalid");
            return false;
        }
        return  true;
    }

    public static boolean validateWeeklyPrice(Double weeklyPrice) {

        if (!weeklyPrice.toString().matches(weeklyPriceRegex) || weeklyPrice == null) {
            System.out.println("Json field \"weeklyPrice\" : " + weeklyPrice + " is invalid");
            return false;
        }
        return  true;
    }

    public static boolean validateMonthlyPrice(Double monthlyPrice) {

        if (!monthlyPrice.toString().matches(monthlyPriceRegex) || monthlyPrice == null) {
            System.out.println("Json field \"monthlyPrice\" : " + monthlyPrice + " is invalid");
            return false;
        }
        return  true;
    }
}
