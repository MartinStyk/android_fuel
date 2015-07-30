package sk.momosi.fuelapp.adapters;

import java.util.Comparator;
import sk.momosi.fuelapp.entities.Expense;

public class CustomExpenseComparator implements Comparator<Expense> {
    @Override
    public int compare(Expense e1, Expense e2) {
        return e2.getDate().compareTo(e1.getDate());
    }
}