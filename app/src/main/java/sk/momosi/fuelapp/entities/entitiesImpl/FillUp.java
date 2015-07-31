package sk.momosi.fuelapp.entities.entitiesImpl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

import sk.momosi.fuelapp.entities.ExpenseEntry;

public class FillUp implements ExpenseEntry {

    public static final String TAG = "Car";

    private static final long serialVersionUID = -7406089937623011561L;

    private Long Id;
    private Long distanceFromLastFillUp;
    private double fuelVolume;
    private BigDecimal fuelPricePerLitre;
    private BigDecimal fuelPriceTotal;
    private boolean isFullFillUp;
    private Car filledCar;
    private Calendar date;
    private String info;

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public Calendar getDate() {
        return date;
    }

    @Override
    public void setDate(Calendar date) {
        this.date = date;
    }

    @Override
    public Car getCar() {
        return filledCar;
    }

    @Override
    public void setCar(Car filledCar) {
        this.filledCar = filledCar;
    }

    public Boolean isFullFillUp() {
        return Boolean.valueOf(isFullFillUp);
    }

    public void setFullFillUp(boolean isFullFillUp) {
        this.isFullFillUp = isFullFillUp;
    }

    @Override
    public Long getId() {
        return Id;
    }

    @Override
    public void setId(Long id) {
        Id = id;
    }

    public Long getDistanceFromLastFillUp() {
        return distanceFromLastFillUp;
    }

    public void setDistanceFromLastFillUp(Long distanceFromLastFillUp) {
        this.distanceFromLastFillUp = distanceFromLastFillUp;
    }

    public Double getFuelVolume() {
        return Double.valueOf(fuelVolume);
    }

    public void setFuelVolume(double fuelVolume) {
        this.fuelVolume = fuelVolume;
    }

    public BigDecimal getFuelPricePerLitre() {
        return fuelPricePerLitre;
    }

    public void setFuelPricePerLitre(BigDecimal fuelPricePerLitre) {
        this.fuelPricePerLitre = fuelPricePerLitre;
    }

    @Override
    public BigDecimal getPrice() {
        return fuelPriceTotal;
    }

    @Override
    public void setPrice(BigDecimal fuelPriceTotal) {
        this.fuelPriceTotal = fuelPriceTotal;
    }


}
