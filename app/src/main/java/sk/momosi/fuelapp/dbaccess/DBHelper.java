package sk.momosi.fuelapp.dbaccess;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    // class tag could be used in Log
    public static final String TAG = "DBHelper";

    // database info
    private static final String DATABASE_NAME = "app_data.db";
    private static final int DATABASE_VERSION = 1;

    // columns of car table
    public static final String TABLE_CARS = "cars";
    public static final String COLUMN_CAR_ID = "_id";    //0
    public static final String COLUMN_CAR_MANUFACTURERNAME = "manufacturer_name"; //1
    public static final String COLUMN_CAR_TYPENAME = "type_name";    //2
    public static final String COLUMN_CAR_STARTMILEAGE = "start_mileage";    //3
    public static final String COLUMN_CAR_ACTUALMILEAGE = "actual_mileage";    //4
    public static final String COLUMN_CAR_AVERAGEFUELCONSUMPTION = "avg_fuel_consumption";    //5
    public static final String COLUMN_CAR_TYPE = "car_type"; //6
    public static final String COLUMN_CAR_DEFAULT_CURRENCY = "car_currency"; //7
    public static final String COLUMN_CAR_DEFAULT_DISTANCE_UNIT = "car_distance_unit"; //8

    // columns of fill ups table
    public static final String TABLE_FILLUPS = "fill_ups";
    public static final String COLUMN_FILLUP_ID = "_id";
    public static final String COLUMN_FILLUP_DISTANCEFROMLASTFILLUP = "distance_from_last_fillup";
    public static final String COLUMN_FILLUP_FUELVOLUME = "fuel_volume";
    public static final String COLUMN_FILLUP_FUELPRICEPERLITRE = "fuel_price_per_litre";
    public static final String COLUMN_FILLUP_FUELPRICETOTAL = "fuel_price_total";
    public static final String COLUMN_FILLUP_ISFULL = "is_full_fillup";
    public static final String COLUMN_FILLUP_CAR_ID = "car_id";    //foreign key
    public static final String COLUMN_FILLUP_INFO = "info";
    public static final String COLUMN_FILLUP_DATE = "date";

    //columns expenses
    public static final String TABLE_EXPENSES = "expenses";
    public static final String COLUMN_EXPENSE_ID = "_id";
    public static final String COLUMN_EXPENSE_PRICE = "price";
    public static final String COLUMN_EXPENSE_INFO = "info";
    public static final String COLUMN_EXPENSE_DATE = "date";
    public static final String COLUMN_EXPENSE_CAR_ID = "car_id";    //foreign key

    //SQL create table cars
    private static final String SQL_CREATE_TABLE_CARS = "CREATE TABLE IF NOT EXISTS " + TABLE_CARS + " ("
            + COLUMN_CAR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CAR_MANUFACTURERNAME + " TEXT NOT NULL,"
            + COLUMN_CAR_TYPENAME + " TEXT NOT NULL, "
            + COLUMN_CAR_STARTMILEAGE + " INTEGER NOT NULL, "
            + COLUMN_CAR_ACTUALMILEAGE + " INTEGER NOT NULL, "
            + COLUMN_CAR_AVERAGEFUELCONSUMPTION + " REAL NOT NULL, "
            + COLUMN_CAR_TYPE + " TEXT NOT NULL, "
            + COLUMN_CAR_DEFAULT_CURRENCY + " TEXT NOT NULL, "
            + COLUMN_CAR_DEFAULT_DISTANCE_UNIT + " TEXT NOT NULL"
            //+ COLUMN_CAR_DEFAULT_LOCALE + " TEXT NOT NULL"
            + ");";

    //SQL create table fillups
    private static final String SQL_CREATE_TABLE_FILLUPS = "CREATE TABLE IF NOT EXISTS " + TABLE_FILLUPS + " ( "
            + COLUMN_FILLUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_FILLUP_DISTANCEFROMLASTFILLUP + " REAL NOT NULL, "
            + COLUMN_FILLUP_FUELPRICEPERLITRE + " STRING NOT NULL, "
            + COLUMN_FILLUP_FUELPRICETOTAL + " STRING NOT NULL, "
            + COLUMN_FILLUP_FUELVOLUME + " REAL NOT NULL, "
            + COLUMN_FILLUP_ISFULL + " SMALLINT NOT NULL, "
            + COLUMN_FILLUP_INFO + " STRING , "
            + COLUMN_FILLUP_DATE + " STRING NOT NULL, "
            + COLUMN_FILLUP_CAR_ID + " INTEGER NOT NULL "
            + ");";
    //SQL create table expense
    private static final String SQL_CREATE_TABLE_EXPENSES = "CREATE TABLE IF NOT EXISTS " + TABLE_EXPENSES + " ( "
            + COLUMN_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_EXPENSE_INFO + " STRING NOT NULL, "
            + COLUMN_EXPENSE_DATE + " STRING NOT NULL, "
            + COLUMN_EXPENSE_PRICE + " STRING NOT NULL, "
            + COLUMN_EXPENSE_CAR_ID + " INTEGER NOT NULL "
            + ");";

    public DBHelper(Context context) {                            //kto to vola
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(SQL_CREATE_TABLE_CARS);
        database.execSQL(SQL_CREATE_TABLE_FILLUPS);
        database.execSQL(SQL_CREATE_TABLE_EXPENSES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Updating db " + DATABASE_NAME + " from version + " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILLUPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        onCreate(db);
    }

    public DBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

}
