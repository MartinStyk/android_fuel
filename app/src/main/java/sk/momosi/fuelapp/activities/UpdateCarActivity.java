package sk.momosi.fuelapp.activities;

import java.util.Locale;

import sk.momosi.fuel.R;
import sk.momosi.fuelapp.dbaccess.CarManager;
import sk.momosi.fuelapp.entities.Car;
import sk.momosi.fuelapp.entities.Car.CarType;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateCarActivity extends Activity {

	public static final String EXTRA_CAR = "extra_car_to_add_fillup";
	public static final String TAG = "UpdateCarActivity";
	
	private Car mCar;
	private CarManager mCarManager;
	
	private EditText mNick;
	private EditText mManufacturer;
	private EditText mMileage;
	private TextView mMileageUnit;
	private Spinner mTypeSpinner;
	
	private CarType mCarType;
	private String mNickToUpdate;
	private String mManufacturerToUpdate;
	private Long mMileageToUpdate;
	private Long mActualMileageToUpdate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_car_update);

		Intent intent = getIntent();

		if (intent != null) {
			mCar = (Car) intent
					.getSerializableExtra(EXTRA_CAR);
			if (mCar == null) {
				setResult(RESULT_CANCELED);
				finish();
			}
		}
		
		mCarManager = new CarManager(this);
		initViews();
	}
	
	private void initViews(){
		mNick = (EditText) findViewById(R.id.txt_carupdate_nick);
		mManufacturer = (EditText) findViewById(R.id.txt_carupdate_manufacturer);
		mMileage = (EditText) findViewById(R.id.txt_carupdate_mileage);
		mMileageUnit = (TextView) findViewById(R.id.txt_carupdate_unit);
		mTypeSpinner = (Spinner) findViewById(R.id.spinner_carupdate_types);
		
		mNick.setText(mCar.getNick());
		mManufacturer.setText(mCar.getTypeName());
		mMileage.setText(mCar.getStartMileage().toString());
		mTypeSpinner.setSelection(mCar.getCarType().ordinal());
		mMileageUnit.setText(mCar.getDistanceUnitString());
	}
	
	public void onUpdateBtnClick(View v){
		Editable nick = mNick.getText();
		Editable manufacturer = mManufacturer.getText();
		Editable mileage = mMileage.getText();
		
		Log.d(TAG, getString(R.string.updateCarActivity_LOG_updateBtnClicked));
		if (TextUtils.isEmpty(nick)
			|| TextUtils.isEmpty(manufacturer)
			|| TextUtils.isEmpty(mileage)){
			Toast.makeText(this, R.string.updateCarActivity_emptyFields, Toast.LENGTH_LONG).show();
		}
		else{
			String msg = null;
			Long createdMileage = Long.valueOf(0);
			
			try {
				createdMileage = Long.parseLong(mileage.toString());
			} catch (NumberFormatException ex) {
				Log.d(TAG, getString(R.string.updateCarActivity_LOG_badLongNumberFormat));
				msg = getString(R.string.updateCarActivity_wrong_number_format);
			}
			if(msg != null){
				Toast.makeText(this, msg + " - " + getString(R.string.car_update_mileage), Toast.LENGTH_LONG).show();
			}
			else{
				if(nick.toString().equals(mCar.getNick())
						&& manufacturer.toString().equals(mCar.getTypeName())
						&& createdMileage.equals(mCar.getStartMileage())
						&& Car.CarType.valueOf(mTypeSpinner.getSelectedItem().toString()).equals(mCar.getCarType())){
					//ziadna zmena = konec
					Toast.makeText(this, getString(R.string.updateCarActivity_Toast_carNoUpdate), Toast.LENGTH_LONG).show();
					setResult(RESULT_OK);
					finish();
				}
				else if(!createdMileage.equals(mCar.getStartMileage())){
					//varovanie pri zmene startMielage
					mNickToUpdate = nick.toString();
					mManufacturerToUpdate = manufacturer.toString();
					mMileageToUpdate = createdMileage;
					mCarType = Car.CarType.valueOf(mTypeSpinner.getSelectedItem().toString());
					
					showUpdateDialogConfirmation(createdMileage);
				}
				else{
					//pri zmene nazvov len zmena nazvov
					mCar.setNick(nick.toString());
					mCar.setTypeName(manufacturer.toString());
					mCar.setCarType(Car.CarType.valueOf(mTypeSpinner.getSelectedItem().toString()));
					
					mCarManager.updateCar(mCar);
					Toast.makeText(this, getString(R.string.updateCarActivity_Toast_carUpdatedSuccesfully01) + " \""
							+ mCar.getNick() + "\" " + getString(R.string.updateCarActivity_Toast_carUpdatedSuccesfully02),
							Toast.LENGTH_LONG).show();
					
					setResult(RESULT_OK);
					finish();
				}
			}
		}
	}
	
	public void onDeleteBtnClick(View v){
		Log.d(TAG, getString(R.string.updateCarActivity_LOG_deleteBtnClicked));
		showDeleteDialogConfirmation();
	}
	
	private void showUpdateDialogConfirmation(Long mileage) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		mActualMileageToUpdate = mileage - mCar.getStartMileage() + mCar.getActualMileage(); 
		alertDialogBuilder.setTitle(getString(R.string.updateCarActivity_DialogUpdate_title));
		alertDialogBuilder.setMessage(getString(R.string.updateCarActivity_DialogUpdate_msg01) + " " 	//you want change from
				+ mCar.getStartMileage().toString() + mCar.getDistanceUnitString() + " "				//VALUE
				+ getString(R.string.updateCarActivity_DialogUpdate_msg02) + " " + mileage.toString()	//to VALUE
				+ mCar.getDistanceUnitString()
				+ getString(R.string.updateCarActivity_DialogUpdate_msg03) + " "						//this also change from
				+ mCar.getActualMileage().toString() + mCar.getDistanceUnitString() + " "				//VALUE
				+ getString(R.string.updateCarActivity_DialogUpdate_msg04) + " "						//to
				+ mActualMileageToUpdate.toString() + mCar.getDistanceUnitString()						//VALUE
				+ getString(R.string.updateCarActivity_DialogUpdate_msg05));							//are you sure?
		
		// set positive button YES message
				alertDialogBuilder.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialogInterface, int which) {
								Log.d(TAG, getString(R.string.updateCarActivity_LOG_dialogConfirm));
								Dialog dialog = (Dialog) dialogInterface;
								Context context = dialog.getContext();
								
								mCar.setNick(mNickToUpdate);
								mCar.setTypeName(mManufacturerToUpdate);
								mCar.setStartMileage(mMileageToUpdate);
								mCar.setActualMileage(mActualMileageToUpdate);
								mCar.setCarType(mCarType);
								
								mCarManager.updateCar(mCar);
								Toast.makeText(context, getString(R.string.updateCarActivity_Toast_carUpdatedSuccesfully01) + " \""
										+ mCar.getNick() + "\" " + getString(R.string.updateCarActivity_Toast_carUpdatedSuccesfully02),
										Toast.LENGTH_LONG).show();
								dialog.dismiss();
								setResult(RESULT_OK);
								finish();
							}
						});

				// set neutral button OK
				alertDialogBuilder.setNeutralButton(android.R.string.no,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// Dismiss the dialog
								Log.d(TAG, getString(R.string.updateCarActivity_LOG_dialogDecline));
								dialog.dismiss();
							}
						});

				AlertDialog alertDialog = alertDialogBuilder.create();
				// show alert
				alertDialog.show();
	}
	
	private void showDeleteDialogConfirmation() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		
		alertDialogBuilder.setTitle(getString(R.string.updateCarActivity_DialogDelete_title));
		alertDialogBuilder.setMessage(getString(R.string.updateCarActivity_DialogDelete_msg01) + " "
				+ mCar.getNick().toUpperCase(Locale.getDefault()) + " - " + mCar.getTypeName() + "?");

		// set positive button YES message
		alertDialogBuilder.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialogInterface, int which) {
						// delete the company and refresh the list
						Log.d(TAG, getString(R.string.updateCarActivity_LOG_dialogConfirm));
						Dialog dialog = (Dialog) dialogInterface;
						Context context = dialog.getContext();
						
						CarManager mCarManager = new CarManager(getBaseContext());
						if (mCarManager != null) {
							mCarManager.deleteCar(mCar);
						}
						
						Toast.makeText(context, getString(R.string.updateCarActivity_Toast_carDeleted01) + " \""
								+ mCar.getNick() + "\" "+ getString(R.string.updateCarActivity_Toast_carDeleted02), Toast.LENGTH_LONG).show();
						dialog.dismiss();
						setResult(RESULT_OK);
						finish();
					}
				});

		// set neutral button OK
		alertDialogBuilder.setNeutralButton(android.R.string.no,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Dismiss the dialog
						Log.d(TAG, getString(R.string.updateCarActivity_LOG_dialogDecline));
						dialog.dismiss();
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();
		// show alert
		alertDialog.show();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(mCarManager != null){
			mCarManager.close();
		}
	}
	
	@Override
	public Intent getParentActivityIntent() {
		Intent intent = new Intent(this, ListCarsActivity.class);
		intent.putExtra(ListCarsActivity.FORCE_SHOW_LIST_CARS, true);
		return intent;
	}
}
