package sk.momosi.fuelapp.entities.entitiesImpl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

import sk.momosi.fuelapp.entities.ExpenseEntry;

public class Expense implements ExpenseEntry {

    public static final double VersionUID = 064565131l;

    private BigDecimal price;
    private Long id;
    private String info;
    private Calendar date;
    private Car car;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Car getCar() {
        return car;
    }

    @Override
    public void setCar(Car car) {
        this.car = car;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

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

}
