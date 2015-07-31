package sk.momosi.fuelapp.dbaccess;

import java.util.ArrayList;
import java.util.List;

import sk.momosi.fuelapp.entities.entitiesImpl.Car;
import sk.momosi.fuelapp.entities.entitiesImpl.Car.CarType;
import sk.momosi.fuelapp.entities.entitiesImpl.Car.CarCurrency;
import sk.momosi.fuelapp.entities.entitiesImpl.Car.CarDistanceUnit;
import sk.momosi.fuelapp.entities.entitiesImpl.Expense;
import sk.momosi.fuelapp.entities.entitiesImpl.FillUp;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CarManager {

	public static final String TAG = "CarManager";

	// DB fields
	private SQLiteDatabase myDatabase;
	private DBHelper myDbHelper;
	private Context myContext;
	private String[] myAllCollums = { DBHelper.COLUMN_CAR_ID,
			DBHelper.COLUMN_CAR_MANUFACTURERNAME, DBHelper.COLUMN_CAR_TYPENAME,
			DBHelper.COLUMN_CAR_STARTMILEAGE,
			DBHelper.COLUMN_CAR_ACTUALMILEAGE,
			DBHelper.COLUMN_CAR_AVERAGEFUELCONSUMPTION,
			DBHelper.COLUMN_CAR_TYPE, DBHelper.COLUMN_CAR_DEFAULT_CURRENCY,
			DBHelper.COLUMN_CAR_DEFAULT_DISTANCE_UNIT };

	public CarManager(Context ctx) {
		myContext = ctx;
		myDbHelper = new DBHelper(ctx);

		// open db
		Log.w(TAG, "Creating CarManager - constructor");
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

	public Car createCar(Car myCar) {

		ContentValues values = new ContentValues();
		values.put(DBHelper.COLUMN_CAR_MANUFACTURERNAME, myCar.getNick());
		values.put(DBHelper.COLUMN_CAR_TYPENAME, myCar.getTypeName());
		values.put(DBHelper.COLUMN_CAR_STARTMILEAGE, myCar.getStartMileage());
		values.put(DBHelper.COLUMN_CAR_ACTUALMILEAGE, myCar.getActualMileage());
		values.put(DBHelper.COLUMN_CAR_AVERAGEFUELCONSUMPTION,
				myCar.getAvgFuelConsumption());
		values.put(DBHelper.COLUMN_CAR_TYPE, myCar.getCarType().toString());
		values.put(DBHelper.COLUMN_CAR_DEFAULT_CURRENCY, myCar.getCarCurrency()
				.toString());
		values.put(DBHelper.COLUMN_CAR_DEFAULT_DISTANCE_UNIT, myCar
				.getDistanceUnit().toString());

		long insertId = myDatabase.insert(DBHelper.TABLE_CARS, null, values);

		Cursor cursor = null;
		Car newCar;
		try {
			cursor = myDatabase.query(DBHelper.TABLE_CARS, myAllCollums,
					DBHelper.COLUMN_CAR_ID + " = " + insertId, null, null,
					null, null);
			cursor.moveToFirst();
			Log.w(TAG, "teraz ide CursorToCar");
			newCar = cursorToCar(cursor);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return newCar;
	}

	public void updateCar(Car myCar) {

		ContentValues values = new ContentValues();
		values.put(DBHelper.COLUMN_CAR_MANUFACTURERNAME, myCar.getNick());
		values.put(DBHelper.COLUMN_CAR_TYPENAME, myCar.getTypeName());
		values.put(DBHelper.COLUMN_CAR_STARTMILEAGE, myCar.getStartMileage());
		values.put(DBHelper.COLUMN_CAR_ACTUALMILEAGE, myCar.getActualMileage());
		values.put(DBHelper.COLUMN_CAR_AVERAGEFUELCONSUMPTION,
				myCar.getAvgFuelConsumption());
		values.put(DBHelper.COLUMN_CAR_TYPE, myCar.getCarType().toString());
		values.put(DBHelper.COLUMN_CAR_DEFAULT_CURRENCY, myCar.getCarCurrency()
				.toString());
		values.put(DBHelper.COLUMN_CAR_DEFAULT_DISTANCE_UNIT, myCar
				.getDistanceUnit().toString());

		myDatabase.update(DBHelper.TABLE_CARS, values, DBHelper.COLUMN_CAR_ID
				+ "=" + myCar.getId(), null);
	}

	public void deleteCar(Car myCar) {
		long id = myCar.getId();

		// delete all fill ups of this car

		FillUpManager fillUpMng = new FillUpManager(myContext);
		List<FillUp> allFillUpsOfCar = fillUpMng.getFillUpsOfCar(id);

		if (allFillUpsOfCar != null && !allFillUpsOfCar.isEmpty()) {
			for (FillUp entry : allFillUpsOfCar) {
				fillUpMng.deleteFillUp(entry);
			}
		}

		// delete expenses of this car

		ExpenseManager expenseMng = new ExpenseManager(myContext);
		List<Expense> allExpenses = expenseMng.getExpensesOfCar(id);
		if (allExpenses != null && !allExpenses.isEmpty()) {
			for (Expense entry : allExpenses) {
				expenseMng.deleteExpense(entry);
			}
		}

		// delete car
		myDatabase.delete(DBHelper.TABLE_CARS, DBHelper.COLUMN_CAR_ID + " = "
				+ id, null);

		fillUpMng.close();
		expenseMng.close();
	}

	public List<Car> getAllCars() {
		List<Car> listCars = new ArrayList<Car>();
		Cursor cursor = null;
		Log.w(TAG, "getting all cars - CarManager");

		try {
			cursor = myDatabase.query(DBHelper.TABLE_CARS, myAllCollums, null,
					null, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();

				while (!cursor.isAfterLast()) {
					Car car = cursorToCar(cursor);
					listCars.add(car);
					cursor.moveToNext();
				}
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		Log.w(TAG, "getting all cars - CarManager : SUCCESS, count: "
				+ listCars.size());
		return listCars;
	}

	public Car getCarById(long id) {

		Car car = null;
		Cursor cursor = null;
		try {
			cursor = myDatabase.query(DBHelper.TABLE_CARS, myAllCollums,
					DBHelper.COLUMN_CAR_ID + " = ?",
					new String[] { String.valueOf(id) }, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();

				car = cursorToCar(cursor);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return car;
	}

	private Car cursorToCar(Cursor cursor) {
		if (cursor == null)
			return null;
		else {
			Car myCar = new Car();
			myCar.setId(cursor.getLong(0));
			myCar.setNick(cursor.getString(1));
			myCar.setTypeName(cursor.getString(2));
			myCar.setStartMileage(cursor.getLong(3));
			myCar.setActualMileage(cursor.getLong(4));
			myCar.setAvgFuelConsumption(cursor.getDouble(5));
			myCar.setCarType(CarType.valueOf((cursor.getString(6))));
			myCar.setCarCurrency(CarCurrency.valueOf((cursor.getString(7))));
			myCar.setDistanceUnit(CarDistanceUnit.valueOf((cursor.getString(8))));
			return myCar;
		}

	}
}
