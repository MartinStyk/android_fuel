package sk.momosi.fuelapp.entities;

import java.io.Serializable;
import java.util.Currency;

import sk.momosi.fuel.R;

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
	private Currency carCurrency;
	private CarDistanceUnit distanceUnit;
	//private Locale locale;
	
	
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

	public Currency getCarCurrency() {
		return carCurrency;
	}

	public void setCarCurrency(CarCurrency carCurrency) {
		switch (carCurrency){
		case EUR: this.carCurrency = Currency.getInstance("EUR");
				//this.locale = getLocale(this.carCurrency.getCurrencyCode());
				break;
		case CZK: this.carCurrency = Currency.getInstance("CZK");
				//this.locale = getLocale(this.carCurrency.getCurrencyCode());
				break;
		case USD: this.carCurrency = Currency.getInstance("USD");
				//this.locale = getLocale(this.carCurrency.getCurrencyCode());
				break;
		case PLN: this.carCurrency = Currency.getInstance("PLN");
				//this.locale = getLocale(this.carCurrency.getCurrencyCode());
				break;
		case GBP: this.carCurrency = Currency.getInstance("GBP");
				//this.locale = getLocale(this.carCurrency.getCurrencyCode());
				break;
		case INR: this.carCurrency = Currency.getInstance("INR");
				//this.locale = getLocale(this.carCurrency.getCurrencyCode());
				break;
		case AUD: this.carCurrency = Currency.getInstance("AUD");
				//this.locale = getLocale(this.carCurrency.getCurrencyCode());
				break;
		case CAD: this.carCurrency = Currency.getInstance("CAD");
				//this.locale = getLocale(this.carCurrency.getCurrencyCode());
				break;
		case SGD: this.carCurrency = Currency.getInstance("SGD");
				//this.locale = getLocale(this.carCurrency.getCurrencyCode());
				break;
		case CHF: this.carCurrency = Currency.getInstance("CHF");
				//this.locale = getLocale(this.carCurrency.getCurrencyCode());
				break;
		case MYR: this.carCurrency = Currency.getInstance("MYR");
				//this.locale = getLocale(this.carCurrency.getCurrencyCode());
				break;
		case JPY: this.carCurrency = Currency.getInstance("JPY");
				//this.locale = getLocale(this.carCurrency.getCurrencyCode());
				break;
		case CNY: this.carCurrency = Currency.getInstance("CNY");
				//this.locale = getLocale(this.carCurrency.getCurrencyCode());
				break;
		}
	}
	
	public String getCurrencyFormatted(){
		return this.carCurrency.getSymbol();
	}
	
	public String getDistanceUnitString(){
		switch (this.distanceUnit){
		case kilometres: return "km";
		case miles: return "mi";
		default: return "km";
		}
	}
	
	//ZISTI LOCALE Z MENY
	/*private static Locale getLocale(String strCode) {
	     
	    for (Locale locale : NumberFormat.getAvailableLocales()) {
	        String code = NumberFormat.getCurrencyInstance(locale).getCurrency().getCurrencyCode();
	        if (strCode.equals(code)) {
	            return locale;
	        }
	    }  
	    return null;
	}*/
	
	public enum CarType {
		Sedan, Hatchback, Combi, Van, Motocycle, Pickup, Quad, Sport, SUV,
	}
	public enum CarCurrency {
		 EUR,
         CZK,
         USD,
         PLN,
         GBP,
         INR,
         AUD,
         CAD,
         SGD,
         CHF,
         MYR,
         JPY,
         CNY,
	}
	public enum CarDistanceUnit {
		kilometres, miles,
	}
}
