package sk.momosi.fuelapp.activities;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;

import sk.momosi.fuel.R;
import sk.momosi.fuelapp.dbaccess.CarManager;
import sk.momosi.fuelapp.dbaccess.ExpenseManager;
import sk.momosi.fuelapp.entities.entitiesImpl.Car;
import sk.momosi.fuelapp.entities.entitiesImpl.Expense;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddExpenseActivity extends Activity implements OnClickListener {

    public static final String TAG = "AddExpenseActivity";
    public static final String EXTRA_EXPENSE = "extra_expense_to_update";

    private Bundle savedInstanceState;

    private EditText mTxtInfo;
    private EditText mTxtPrice;
    private TextView mTxtDate;
    private TextView mTxtPriceUnit;
    private RelativeLayout mLayDate;

    private Button mBtnAdd;

    private ExpenseManager mExpenseManager;
    private CarManager mCarManager;

    private Car mSelectedCar;
    private Expense mSelectedExpense;
    private Mode mode;

    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_add_expense);

        Intent intent = getIntent();

        if (intent != null) {
            mSelectedCar = (Car) intent.getSerializableExtra(AddFillUpActivity.EXTRA_CAR);
            mSelectedExpense = (Expense) intent.getSerializableExtra(EXTRA_EXPENSE);
            getInstanceMode();

            if (mode == Mode.UPDATING) {
                mSelectedCar = mSelectedExpense.getCar();
            }
        }

        mExpenseManager = new ExpenseManager(this);
        mCarManager = new CarManager(this);

        if (mSelectedCar != null) {
            mSelectedCar = mCarManager.getCarById(mSelectedCar.getId());
        }

        initViews();
        initActionBar();

        mTxtPriceUnit.setText(mSelectedCar.getCurrencyFormatted());
        if (mode == Mode.UPDATING && savedInstanceState == null) {
            populateFields();
        }

    }

    private void initViews() {
        this.mTxtInfo = (EditText) findViewById(R.id.txt_addexpense_information);
        this.mTxtPrice = (EditText) findViewById(R.id.txt_addexpense_price);
        this.mTxtDate = (TextView) findViewById(R.id.txt_addexpense_date);
        this.mBtnAdd = (Button) findViewById(R.id.btn_addexpense_add);
        this.mLayDate = (RelativeLayout) findViewById(R.id.rel_addexpense_lay_date);
        this.mTxtPriceUnit = (TextView) findViewById(R.id.txt_addexpense_priceunit);

        this.mBtnAdd.setOnClickListener(this);
        setDateTimeField();

    }

    private void initActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (mode == Mode.UPDATING) {
            actionBar.setTitle("Update expense");
        } else {
            actionBar.setTitle("Create expense");
        }
    }

    private void populateFields() {
        if (mSelectedExpense != null) {
            mTxtInfo.setText(mSelectedExpense.getInfo());
            mTxtPrice.setText(mSelectedExpense.getPrice().toString());
            mTxtDate.setText(android.text.format.DateFormat.getDateFormat(getApplicationContext()).format(mSelectedExpense.getDate().getTime()));
            mBtnAdd.setText(getString(R.string.addExpenseActivity_btnTxt_update));
        }
    }

    private void setDateTimeField() {
        mLayDate.setOnClickListener(this);

        Calendar serviceDate = Calendar.getInstance();
        if (mode == Mode.CREATING && savedInstanceState == null) {
            mTxtDate.setText(android.text.format.DateFormat.getDateFormat(getApplicationContext()).format(Calendar.getInstance().getTime()));
        }
        datePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar serviceDate = Calendar.getInstance();
                if (mode == Mode.UPDATING) {
                    serviceDate.set(mSelectedExpense.getDate().get(Calendar.YEAR),
                            mSelectedExpense.getDate().get(Calendar.MONTH),
                            mSelectedExpense.getDate().get(Calendar.DAY_OF_MONTH));
                }
                serviceDate.set(year, monthOfYear, dayOfMonth);
                mTxtDate.setText(android.text.format.DateFormat.getDateFormat(getApplicationContext()).format(serviceDate.getTime()));
            }
        }, serviceDate.get(Calendar.YEAR), serviceDate.get(Calendar.MONTH), serviceDate.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_addexpense_add:

                Editable info = mTxtInfo.getText();
                Editable price = mTxtPrice.getText();
                String date = mTxtDate.getText().toString();

                if (!TextUtils.isEmpty(info) && !TextUtils.isEmpty(price) && !TextUtils.isEmpty(date)) {

                    //Toast.makeText(this, "AddExpenseActivity - button click", Toast.LENGTH_LONG).show();	//TO DO delete
                    String msg = null;
                    BigDecimal createdPrice = null;
                    Calendar createdDate = Calendar.getInstance();
                    try {
                        createdPrice = new BigDecimal(price.toString());
                    } catch (NumberFormatException ex) {
                        Log.d(TAG, "tried bad number format");
                        msg = "price";
                    }
                    try {
                        DateFormat dateFormatter = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                        createdDate.setTime(dateFormatter.parse(date));
                    } catch (ParseException ex) {
                        Log.d(TAG, "tried bad date");
                        msg = "date";
                    }
                    if (msg == null) {
                        if (mode == Mode.CREATING) {
                            Expense createdExpense = new Expense();
                            createdExpense.setDate(createdDate);
                            createdExpense.setInfo(info.toString());
                            createdExpense.setPrice(createdPrice);
                            createdExpense.setCar(mSelectedCar);

                            createdExpense = mExpenseManager.createExpense(createdExpense, mSelectedCar);
                            Toast.makeText(this, R.string.addExpense_Toast_createdSuccessfully, Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            mSelectedExpense.setDate(createdDate);
                            mSelectedExpense.setInfo(info.toString());
                            mSelectedExpense.setPrice(createdPrice);

                            mExpenseManager.updateExpense(mSelectedExpense);
                            Toast.makeText(this, R.string.addExpense_Toast_updatedSuccessfully, Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                } else {
                    Toast.makeText(this, R.string.addExpenseActivity_emptyFields,
                            Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.rel_addexpense_lay_date:
                datePickerDialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mExpenseManager.close();
        mCarManager.close();
    }

    @Override
    public Intent getParentActivityIntent() {
        Intent intent = new Intent(this, ListFillUpsFragment.class);
        intent.putExtra(ListCarsActivity.EXTRA_ADDED_CAR, mSelectedCar);
        return intent;
    }

    private void getInstanceMode() {
        if (mSelectedExpense != null) {
            mode = Mode.UPDATING;
        } else {
            mode = Mode.CREATING;
        }
    }

    enum Mode {
        UPDATING, CREATING
    }
}
