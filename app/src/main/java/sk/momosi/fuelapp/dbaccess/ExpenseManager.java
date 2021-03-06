package sk.momosi.fuelapp.dbaccess;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import sk.momosi.fuelapp.entities.entitiesImpl.Car;
import sk.momosi.fuelapp.entities.entitiesImpl.Expense;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ExpenseManager {

	public static final String TAG = "ExpenseManager";
	public static final String DATE_FORMAT = "yyyy-MM-dd";

	// DB fields
	private SQLiteDatabase myDatabase;
	private DBHelper myDbHelper;
	private Context myContext;
	private String[] myAllCollums = { DBHelper.COLUMN_EXPENSE_ID,
			DBHelper.COLUMN_EXPENSE_PRICE, DBHelper.COLUMN_EXPENSE_INFO,
			DBHelper.COLUMN_EXPENSE_DATE, DBHelper.COLUMN_EXPENSE_CAR_ID };

	public ExpenseManager(Context ctx) {
		myContext = ctx;
		myDbHelper = new DBHelper(ctx);

		// open db

		try {
			open();
		} catch (SQLException e) {
			Log.e(TAG, "Opening db exception" + e.getMessage());
			e.printStackTrace();
		}
	}

	public void open() throws SQLException {
		myDatabase = myDbHelper.getWritableDatabase();
	}

	public void close() {
		myDbHelper.close();
	}

	public Expense createExpense(Expense myExpense, Car car) {

		ContentValues values = new ContentValues();
		values.put(DBHelper.COLUMN_EXPENSE_PRICE, myExpense.getPrice().toPlainString());
		values.put(DBHelper.COLUMN_EXPENSE_INFO, myExpense.getInfo());
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		values.put(DBHelper.COLUMN_EXPENSE_DATE, sdf.format(myExpense.getDate().getTime()));

		values.put(DBHelper.COLUMN_EXPENSE_CAR_ID, car.getId());

		long insertId = myDatabase
				.insert(DBHelper.TABLE_EXPENSES, null, values);
		Cursor cursor = null;
		Expense newExpense;
		try {
			cursor = myDatabase.query(DBHelper.TABLE_EXPENSES, myAllCollums,
					DBHelper.COLUMN_EXPENSE_ID + " = " + insertId, null, null,
					null, null);
			cursor.moveToFirst();
			newExpense = cursorToExpense(cursor);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return newExpense;
	}

	public void updateExpense(Expense myExpense) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.COLUMN_EXPENSE_PRICE, myExpense.getPrice()
				.toPlainString());
		// BigDecimals stored as plain string
		values.put(DBHelper.COLUMN_EXPENSE_INFO, myExpense.getInfo());
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		values.put(DBHelper.COLUMN_EXPENSE_DATE,
				sdf.format(myExpense.getDate().getTime()));

		values.put(DBHelper.COLUMN_EXPENSE_CAR_ID, myExpense.getCar().getId());
		myDatabase.update(DBHelper.TABLE_EXPENSES, values,
				DBHelper.COLUMN_EXPENSE_ID + "=" + myExpense.getId(), null);

	}

	public List<Expense> getAllExpenses() {
		List<Expense> listExpenses = new ArrayList<Expense>();
		Cursor cursor = null;
		try {
			cursor = myDatabase.query(DBHelper.TABLE_EXPENSES, myAllCollums,
					null, null, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();

				while (!cursor.isAfterLast()) {
					Expense e = cursorToExpense(cursor);
					listExpenses.add(0, e);
					cursor.moveToNext();
				}
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return listExpenses;
	}

	public void deleteExpense(Expense myExpense) {
		long id = myExpense.getId();
		myDatabase.delete(DBHelper.TABLE_EXPENSES, DBHelper.COLUMN_EXPENSE_ID
				+ " = " + id, null);
	}

	public List<Expense> getExpensesOfCar(long carId) {
		List<Expense> lisExpenses = new ArrayList<Expense>();
		Cursor cursor = null;
		try {
			cursor = myDatabase.query(DBHelper.TABLE_EXPENSES, myAllCollums,
					DBHelper.COLUMN_EXPENSE_CAR_ID + " = ?",
					new String[] { String.valueOf(carId) }, null, null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Expense expense = cursorToExpense(cursor);
				lisExpenses.add(0, expense);
				cursor.moveToNext();
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return lisExpenses;
	}

	public Expense getExpenseById(long id) {
		Expense expense = null;
		Cursor cursor = null;
		
		try{ cursor = myDatabase.query(DBHelper.TABLE_EXPENSES, myAllCollums,
				DBHelper.COLUMN_EXPENSE_ID + " = ?",
				new String[] { String.valueOf(id) }, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();

			expense = cursorToExpense(cursor);

		}}finally{
			if(cursor != null){
				cursor.close();
			}
		}
		return expense;
	}

	@SuppressWarnings("deprecation")
	private Expense cursorToExpense(Cursor cursor) {
		if (cursor == null) {
			return null;
		}
		else {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			Expense my = new Expense();
			
			my.setId(cursor.getLong(0));
			my.setPrice(new BigDecimal(cursor.getString(1)));
			my.setInfo(cursor.getString(2));
			try {
				Calendar cal = Calendar.getInstance();
				Date expDate = sdf.parse(cursor.getString(3));
				cal.set(expDate.getYear() + 1900, expDate.getMonth(), expDate.getDate());
				my.setDate(cal);
			} catch (ParseException e) {
				Log.d(TAG, "Error: ExpenseManager.cursorToExpense :: parsing date");
			}

			CarManager carMngr = new CarManager(myContext);
			my.setCar(carMngr.getCarById(cursor.getLong(4)));
			carMngr.close();

			return my;
		}
	}
}
