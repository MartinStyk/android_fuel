package sk.momosi.fuelapp.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

import sk.momosi.fuelapp.entities.entitiesImpl.Car;

/**
 * Created by Martin Styk on 31.07.2015.
 */
public interface ExpenseEntry extends Serializable {

    Long getId();

    void setId(Long id);

    Car getCar();

    void setCar(Car car);

    BigDecimal getPrice();

    void setPrice(BigDecimal price);

    String getInfo();

    void setInfo(String info);

    Calendar getDate();

    void setDate(Calendar date);
}
