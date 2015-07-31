package sk.momosi.fuelapp.dbaccess;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import sk.momosi.fuelapp.entities.entitiesImpl.Car;
import sk.momosi.fuelapp.entities.entitiesImpl.FillUp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FillUpManager {

    public static final String TAG = "FillUpManager";
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    // DB fields
    private SQLiteDatabase myDatabase;
    private DBHelper myDbHelper;
    private Context myContext;
    private String[] myAllCollumns = {DBHelper.COLUMN_FILLUP_ID,
            DBHelper.COLUMN_FILLUP_DISTANCEFROMLASTFILLUP,
            DBHelper.COLUMN_FILLUP_FUELPRICEPERLITRE,
            DBHelper.COLUMN_FILLUP_FUELPRICETOTAL,
            DBHelper.COLUMN_FILLUP_FUELVOLUME,
            DBHelper.COLUMN_FILLUP_ISFULL,
            DBHelper.COLUMN_FILLUP_INFO,
            DBHelper.COLUMN_EXPENSE_DATE,
            DBHelper.COLUMN_FILLUP_CAR_ID};

    public FillUpManager(Context ctx) {
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

    public FillUp createFillUp(FillUp myFill, Car filledCar) {

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_FILLUP_DISTANCEFROMLASTFILLUP,
                myFill.getDistanceFromLastFillUp());
        // BigDecimals stored as plain string
        values.put(DBHelper.COLUMN_FILLUP_FUELPRICEPERLITRE, myFill
                .getFuelPricePerLitre().toPlainString());
        values.put(DBHelper.COLUMN_FILLUP_FUELPRICETOTAL, myFill
                .getPrice().toPlainString());
        values.put(DBHelper.COLUMN_FILLUP_FUELVOLUME, myFill.getFuelVolume());
        values.put(DBHelper.COLUMN_FILLUP_ISFULL, myFill.isFullFillUp() ? 1 : 0); // boolean
        // saved
        // as
        // int
        // 1true
        // 0false
        values.put(DBHelper.COLUMN_FILLUP_INFO, myFill.getInfo());
        values.put(DBHelper.COLUMN_FILLUP_CAR_ID, filledCar.getId());
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        values.put(DBHelper.COLUMN_EXPENSE_DATE, sdf.format(myFill.getDate().getTime()));
        long insertId = myDatabase.insert(DBHelper.TABLE_FILLUPS, null, values);

        Cursor cursor = null;
        FillUp newFillUp = null;
        try {
            cursor = myDatabase.query(DBHelper.TABLE_FILLUPS, myAllCollumns,
                    DBHelper.COLUMN_FILLUP_ID + " = " + insertId, null, null,
                    null, null);
            cursor.moveToFirst();
            newFillUp = cursorToFillUp(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return newFillUp;
    }

    public void updateFillUp(FillUp fillUpNewValues) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_FILLUP_DISTANCEFROMLASTFILLUP,
                fillUpNewValues.getDistanceFromLastFillUp());
        // BigDecimals stored as plain string
        values.put(DBHelper.COLUMN_FILLUP_FUELPRICEPERLITRE, fillUpNewValues
                .getFuelPricePerLitre().toPlainString());
        values.put(DBHelper.COLUMN_FILLUP_FUELPRICETOTAL, fillUpNewValues
                .getPrice().toPlainString());
        values.put(DBHelper.COLUMN_FILLUP_FUELVOLUME,
                fillUpNewValues.getFuelVolume());
        values.put(DBHelper.COLUMN_FILLUP_ISFULL,
                fillUpNewValues.isFullFillUp() ? 1 : 0); // boolean
        // saved
        // as
        // int
        // 1true
        // 0false
        values.put(DBHelper.COLUMN_FILLUP_INFO, fillUpNewValues.getInfo());
        values.put(DBHelper.COLUMN_FILLUP_CAR_ID, fillUpNewValues.getId());
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        values.put(DBHelper.COLUMN_EXPENSE_DATE, sdf.format(fillUpNewValues.getDate().getTime()));

        values.put(DBHelper.COLUMN_FILLUP_CAR_ID, fillUpNewValues
                .getCar().getId());
        myDatabase
                .update(DBHelper.TABLE_FILLUPS,
                        values,
                        DBHelper.COLUMN_FILLUP_ID + "="
                                + fillUpNewValues.getId(), null);

    }

    public List<FillUp> getAllFillUps() {
        List<FillUp> listFillUps = new ArrayList<FillUp>();

        Cursor cursor = null;
        try {
            cursor = myDatabase.query(DBHelper.TABLE_FILLUPS, myAllCollumns,
                    null, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {
                    FillUp fu = cursorToFillUp(cursor);
                    listFillUps.add(0, fu);
                    cursor.moveToNext();
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return listFillUps;
    }

    public double getFuel(Car car) {
        List<FillUp> listFillUps = new ArrayList<FillUp>();
        listFillUps = getAllFillUps();
        double fuelAll = 0d;
        for (FillUp now : listFillUps) {
            if (now.getCar().equals(car)) {
                fuelAll += now.getFuelVolume();
            }
        }
        return fuelAll;
    }

    public void deleteFillUp(FillUp myFillUp) {
        long id = myFillUp.getId();
        myDatabase.delete(DBHelper.TABLE_FILLUPS, DBHelper.COLUMN_FILLUP_ID
                + " = " + id, null);
    }

    public List<FillUp> getFillUpsOfCar(long carId) {
        List<FillUp> listFillUps = new ArrayList<FillUp>();

        Cursor cursor = null;
        try {
            cursor = myDatabase.query(DBHelper.TABLE_FILLUPS, myAllCollumns,
                    DBHelper.COLUMN_FILLUP_CAR_ID + " = ?",
                    new String[]{String.valueOf(carId)}, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                FillUp fillUp = cursorToFillUp(cursor);
                listFillUps.add(0, fillUp);
                cursor.moveToNext();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return listFillUps;
    }

    public FillUp getFillUpById(long id) {
        FillUp fillUp = null;
        Cursor cursor = null;
        try {
            cursor = myDatabase.query(DBHelper.TABLE_FILLUPS, myAllCollumns,
                    DBHelper.COLUMN_FILLUP_ID + " = ?",
                    new String[]{String.valueOf(id)}, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();

                fillUp = cursorToFillUp(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return fillUp;
    }

    private FillUp cursorToFillUp(Cursor cursor) {
        if (cursor == null)
            return null;
        else {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            FillUp myFillUp = new FillUp();

            myFillUp.setId(cursor.getLong(0));
            myFillUp.setDistanceFromLastFillUp(cursor.getLong(1));
            myFillUp.setFuelVolume(cursor.getDouble(4));
            myFillUp.setFuelPricePerLitre(new BigDecimal(cursor.getString(2)));
            myFillUp.setPrice(new BigDecimal(cursor.getString(3)));
            myFillUp.setFullFillUp((cursor.getShort(5) == 1) ? true : false);
            try {
                Calendar cal = Calendar.getInstance();
                Date expDate = sdf.parse(cursor.getString(7));
                cal.set(expDate.getYear() + 1900, expDate.getMonth(), expDate.getDate());
                myFillUp.setDate(cal);
            } catch (ParseException e) {
                Log.d(TAG, "Error: ExpenseManager.cursorToExpense :: parsing date");
            }
            myFillUp.setInfo(cursor.getString(6));
            // get Car
            CarManager carMngr = new CarManager(myContext);
            myFillUp.setCar(carMngr.getCarById(cursor.getLong(8)));
            carMngr.close();

            return myFillUp;
        }

    }

}
