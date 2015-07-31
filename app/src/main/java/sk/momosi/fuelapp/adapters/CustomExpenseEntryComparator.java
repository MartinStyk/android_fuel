package sk.momosi.fuelapp.adapters;

import java.util.Comparator;

import sk.momosi.fuelapp.entities.ExpenseEntry;

public class CustomExpenseEntryComparator implements Comparator<ExpenseEntry> {
    @Override
    public int compare(ExpenseEntry e1, ExpenseEntry e2) {
        return e2.getDate().compareTo(e1.getDate());
    }
}