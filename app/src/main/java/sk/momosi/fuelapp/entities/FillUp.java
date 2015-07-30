package sk.momosi.fuelapp.entities;

import java.io.Serializable;
import java.math.BigDecimal;

public class FillUp implements Serializable {
	

	public static final String TAG = "Car";

	private static final long serialVersionUID = -7406089937623011561L;

	private Long Id;
	private Long distanceFromLastFillUp;
	private double fuelVolume;
	private BigDecimal fuelPricePerLitre;
	private BigDecimal fuelPriceTotal;
	private boolean isFullFillUp;
	private Car filledCar;
	
	public Car getFilledCar() {
		return filledCar;
	}
	public void setFilledCar(Car filledCar) {
		this.filledCar = filledCar;
	}
	public Boolean isFullFillUp() {
		return Boolean.valueOf(isFullFillUp);
	}
	public void setFullFillUp(boolean isFullFillUp) {
		this.isFullFillUp = isFullFillUp;
	}
	public Long getId() {
		return Id;
	}
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
	public BigDecimal getFuelPriceTotal() {
		return fuelPriceTotal;
	}
	public void setFuelPriceTotal(BigDecimal fuelPriceTotal) {
		this.fuelPriceTotal = fuelPriceTotal;
	}

	
}
