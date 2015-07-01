package sk.momosi.fuelapp.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

public class Expense implements Serializable{
	
	public static final double VersionUID = 064565131l;
	
	private BigDecimal price;
	private long id;
	private String info;
	private Calendar date;
	private Car car;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Car getCar() {
		return car;
	}
	public void setCar(Car car) {
		this.car = car;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public Calendar getDate() {
		return date;
	}
	public void setDate(Calendar date) {
		this.date = date;
	}

}
