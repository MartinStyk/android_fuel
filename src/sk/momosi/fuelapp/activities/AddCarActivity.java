package sk.momosi.fuelapp.activities;

import sk.momosi.fuel.R;
import sk.momosi.fuelapp.dbaccess.CarManager;
import sk.momosi.fuelapp.entities.Car;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class AddCarActivity extends Activity implements OnClickListener {

	public static final String TAG = "AddCarActivity";

	private EditText mTxtNick;
	private EditText mTxtTypeName;
	private EditText mTxtActualMileage;
	private Spinner mTypeSpinner;
	private Spinner mCurrencySpinner;
	private Spinner mDistanceSpinner;
	private Button mBtnAdd;

	private CarManager mCarManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_car);

		initViews();

		mCarManager = new CarManager(this);
	}

	private void initViews() {
		this.mTxtNick = (EditText) findViewById(R.id.txt_addcar_nick);
		this.mTxtTypeName = (EditText) findViewById(R.id.txt_type_name);
		this.mTxtActualMileage = (EditText) findViewById(R.id.txt_start_mileage);
		this.mBtnAdd = (Button) findViewById(R.id.btn_add);
		this.mTypeSpinner = (Spinner) findViewById(R.id.spinner_types);
		this.mCurrencySpinner = (Spinner) findViewById(R.id.spinner_currency);
		this.mDistanceSpinner = (Spinner) findViewById(R.id.spinner_distance_units);

		mTxtActualMileage.setRawInputType(Configuration.KEYBOARD_QWERTY);

		this.mBtnAdd.setOnClickListener(this);
		mTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		}

		);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_add:
			Editable nick = mTxtNick.getText();
			Editable typeName = mTxtTypeName.getText();
			Editable actualMileage = mTxtActualMileage.getText();

			if (!TextUtils.isEmpty(nick)
					&& !TextUtils.isEmpty(typeName)
					&& !TextUtils.isEmpty(actualMileage)) {
				// add the car to database
				Car createdCar = new Car();
				Long createdMileage = Long.valueOf(0);
				String msg = null;

				try {
					createdMileage = Long.parseLong(actualMileage.toString());
				} catch (NumberFormatException ex) {
					Log.d(TAG, getString(R.string.addCarActivity_LOG_badLongNumberFormat));
					msg = getString(R.string.addCarActivity_wrong_number_format);
				}
				if (msg == null) {
					createdCar.setNick(nick.toString());
					createdCar.setTypeName(typeName.toString());
					createdCar.setStartMileage(createdMileage);
					createdCar.setActualMileage(createdMileage);
					createdCar.setAvgFuelConsumption(0.0);
					createdCar.setCarType(Car.CarType.valueOf(mTypeSpinner.getSelectedItem().toString()));
					createdCar.setCarCurrency(Car.CarCurrency.valueOf(mCurrencySpinner.getSelectedItem().toString()));
					createdCar.setDistanceUnit(Car.CarDistanceUnit.valueOf(mDistanceSpinner.getSelectedItem().toString()));
					
					Log.d(TAG, getString(R.string.addCarActivity_LOG_wantToAdd) + " "
									+ createdCar.getNick() + "-"
									+ createdCar.getTypeName());
					createdCar = mCarManager.createCar(createdCar);

					Log.d(TAG, getString(R.string.addCarActivity_LOG_added) + " "
									+ createdCar.getNick() + "-"
									+ createdCar.getTypeName());

					Intent intent = new Intent();
					intent.putExtra(ListCarsActivity.EXTRA_ADDED_CAR,
							createdCar);
					setResult(RESULT_OK, intent);
					Toast.makeText(this, getString(R.string.addCarActivity_Toast_added01) + " \""
							+ createdCar.getNick() + "\" " + getString(R.string.addCarActivity_Toast_added02),
							Toast.LENGTH_LONG).show();
					finish();
				} else {
					Toast.makeText(this, msg + "- actualMileage", Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(this, getString(R.string.addCarActivity_Toast_emptyFields), Toast.LENGTH_LONG).show();
			}
			break;

		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mCarManager.close();
	}

}
