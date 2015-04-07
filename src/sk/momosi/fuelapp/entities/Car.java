package sk.momosi.fuelapp.entities;

import java.io.Serializable;

public class Car implements Serializable, Cloneable {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Id == null) ? 0 : Id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Car other = (Car) obj;
		if (Id == null) {
			if (other.Id != null)
				return false;
		} else if (!Id.equals(other.Id))
			return false;
		return true;
	}

	// tag of this class, should be used in Log
	public static final String TAG = "Car";

	// value for serializable purpose, we dont care about
	private static final long serialVersionUID = -7406082437623008261L;

	private Long Id;
	private String nick;
	private String typeName;
	private Long startMileage;
	private Long actualMileage;
	private CarType carType;
	private CarCurrency carCurrency;
	private CarDistanceUnit distanceUnit;

	public CarType getCarType() {
		return carType;
	}

	public void setCarType(CarType carType) {
		this.carType = carType;
	}

	private double avgFuelConsumption;

	public Long getActualMileage() {
		return actualMileage;
	}

	public void setActualMileage(Long actualMileage) {
		this.actualMileage = actualMileage;
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public Long getStartMileage() {
		return startMileage;
	}

	public void setStartMileage(Long startMileage) {
		this.startMileage = startMileage;
	}

	public double getAvgFuelConsumption() {
		return avgFuelConsumption;
	}

	public void setAvgFuelConsumption(double avgFuelConsumption) {
		this.avgFuelConsumption = avgFuelConsumption;
	}

	public CarDistanceUnit getDistanceUnit() {
		return distanceUnit;
	}

	public void setDistanceUnit(CarDistanceUnit distanceUnit) {
		this.distanceUnit = distanceUnit;
	}

	public CarCurrency getCarCurrency() {
		return carCurrency;
	}

	public void setCarCurrency(CarCurrency carCurrency) {
		this.carCurrency = carCurrency;
	}

	public enum CarType {
		Sedan, Hatchback, Combi, Van, Motocycle, Pickup, Quad, Sport, Suv,
	}
	public enum CarCurrency {
		EUR, CZK,
	}
	public enum CarDistanceUnit {
		kilometres, miles,
	}
}
