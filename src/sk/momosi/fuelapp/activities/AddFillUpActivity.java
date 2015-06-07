package sk.momosi.fuelapp.activities;

import java.math.BigDecimal;

import sk.momosi.fuel.R;
import sk.momosi.fuelapp.dbaccess.CarManager;
import sk.momosi.fuelapp.dbaccess.FillUpManager;
import sk.momosi.fuelapp.entities.Car;
import sk.momosi.fuelapp.entities.FillUp;
import sk.momosi.models.SwitchDistance;
import sk.momosi.models.SwitchPrice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class AddFillUpActivity extends Activity implements OnClickListener {

	public static final String TAG = "AddFillUpActivity";

	public static final String EXTRA_CAR = "extra_car_to_add_fillup";
	public static final String EXTRA_SELECTED_CAR = "extra_key_selected_car";
	public static final String EXTRA_FILLUP = "extra_fillup_to_update";

	private EditText mTxtDistance;
	private EditText mTxtFuelVolume;
	private EditText mTxtPrice;
	private Button mBtnAdd;
	private Button mBtnSwitchDistance;
	private Button mBtnSwitchPrice;
	private CheckBox mCheckBoxIsFullFill;

	private FillUpManager mFillUpManager;
	private CarManager mCarManager;

	private Car mSelectedCar;
	private FillUp mSelectedFillUp;
	private Mode mode;
	private SwitchDistance distanceMode = SwitchDistance.fromLast;
	private SwitchPrice priceMode = SwitchPrice.perLitre;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_fillup);
		Intent intent = getIntent();

		// pri prvom spusteni activity pozreme ci sme tam dali auto(Creating),
		// alebo tankovanie(updating)

		if (intent != null) {
			mSelectedCar = (Car) intent.getSerializableExtra(EXTRA_CAR);
			mSelectedFillUp = (FillUp) intent
					.getSerializableExtra(EXTRA_FILLUP);
			getInstanceMode();

			if (mode == Mode.UPDATING) {
				mSelectedCar = mSelectedFillUp.getFilledCar();
			}
		}
		
		this.mFillUpManager = new FillUpManager(this);
		this.mCarManager = new CarManager(this);
		
		if(mSelectedCar != null){
			mSelectedCar = mCarManager.getCarById(mSelectedCar.getId());
		}

		initViews();
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		
		// updatujeme a chceme natiahnut to co bolo vo fillupe, nie pri otoceni
		// obrazovky len prvy krat pri spusteni activity
	    
		if (mode == Mode.UPDATING && savedInstanceState == null) {
			populateFields();
		}
	}

	private void initViews() {
		
		mTxtDistance = (EditText) findViewById(R.id.txt_addfillup_distance_from_last_fillup_adding);
		mTxtFuelVolume = (EditText) findViewById(R.id.txt_addfillup_fuel_volume);
		mTxtPrice = (EditText) findViewById(R.id.txt_addfillup_price_total);
		mCheckBoxIsFullFill = (CheckBox) findViewById(R.id.checkBox_fullFillUp);
		mBtnAdd = (Button) findViewById(R.id.btn_add);
		mBtnSwitchDistance = (Button) findViewById(R.id.btn_switch_distance);
		mBtnSwitchPrice = (Button) findViewById(R.id.btn_switch_price);
		
		mBtnAdd.setOnClickListener(this);
		mBtnSwitchDistance.setOnClickListener(this);
		mBtnSwitchPrice.setOnClickListener(this);
		
		if(priceMode == SwitchPrice.perLitre){
	    	mBtnSwitchPrice.setText(R.string.addFillUpActivity_BtnTxt_pricePerLitre);
	    }
	    else{
	    	mBtnSwitchPrice.setText(R.string.addFillUpActivity_BtnTxt_priceTotal);
	    }
	}

	private void populateFields() {
		if (mSelectedFillUp != null) {
			mTxtDistance.setText(mSelectedFillUp.getDistanceFromLastFillUp().toString());
			mTxtFuelVolume.setText(mSelectedFillUp.getFuelVolume().toString());
			//mTxtPrice.setText(mSelectedFillUp.getFuelPriceTotal().toString());
			if(priceMode == SwitchPrice.perLitre){
				mTxtPrice.setText(mSelectedFillUp.getFuelPricePerLitre().toString());
			}
			else{
				mTxtPrice.setText(mSelectedFillUp.getFuelPriceTotal().toString());
			}
			if(mSelectedFillUp.isFullFillUp()){
				mCheckBoxIsFullFill.setChecked(true);
			}else{
				mCheckBoxIsFullFill.setChecked(false);
			}
			mBtnAdd.setText(R.string.add_fillup_activity_btn_update);
		}
	}

	private void getInstanceMode() {
		if (mSelectedFillUp != null) {
			mode = Mode.UPDATING;
		} else {
			mode = Mode.CREATING;
		}
	}

	@Override
	public void onClick(View v) {
		String msg = null;
		switch (v.getId()) {
		case R.id.btn_add:
			Editable distanceFromLastFillUp = mTxtDistance.getText();
			Editable fuelVol = mTxtFuelVolume.getText();
			Editable totalPrice = mTxtPrice.getText();

			if (TextUtils.isEmpty(distanceFromLastFillUp)
					|| TextUtils.isEmpty(fuelVol)
					|| TextUtils.isEmpty(totalPrice) && mSelectedCar != null) {
				
				Toast.makeText(this, R.string.addFillUpActivity_Toast_emptyFields, Toast.LENGTH_LONG).show();
			}
			else{
				// add the FillUp to database
				//musi byt aby boli spokojni v eclipse - init try-catch
				Long createdDistance = 0L;
				Double createdFuelVol = 0.0;
				BigDecimal createdPrice = new BigDecimal(222);
				
				try{
					createdDistance = Long.parseLong(distanceFromLastFillUp.toString().replace(",", "."));
				}
				catch(NumberFormatException ex){
					Log.d(TAG, "tried bad number format of Distance travelled");
					msg = "distance travelled";
				}
				try{
					createdFuelVol = Double.parseDouble(fuelVol.toString().replace(",", "."));
				}
				catch(NumberFormatException ex){
					Log.d(TAG, "tried bad number format of fuel volume");
					msg = "fuel volume";
				}
				try{
					createdPrice = new BigDecimal(totalPrice.toString());
				}
				catch(NumberFormatException ex){
					Log.d(TAG, "tried bad number format of price per litre");
					msg = "price per litre";
				}
				
				if(msg != null){
					Toast.makeText(this, R.string.addFillUpActivity_Toast_mistake,Toast.LENGTH_LONG).show();
				}
				else{
					switch (mode) {
					case CREATING:
						FillUp createdFillUp = new FillUp();
						createdFillUp.setFullFillUp(mCheckBoxIsFullFill.isChecked());
						if(distanceMode == SwitchDistance.fromLast){
							createdFillUp.setDistanceFromLastFillUp(createdDistance);
						}
						else{
							createdFillUp.setDistanceFromLastFillUp(createdDistance - mSelectedCar.getActualMileage());
						}
						
						createdFillUp.setFuelVolume(createdFuelVol);
						if(priceMode == SwitchPrice.perLitre){
							createdFillUp.setFuelPricePerLitre(createdPrice);
							double _price = Double.valueOf(totalPrice.toString());
							createdFillUp.setFuelPriceTotal(new BigDecimal(createdFuelVol * _price));
						}
						else{
							createdFillUp.setFuelPriceTotal(createdPrice);
							double _price = Double.valueOf(totalPrice.toString());
							createdFillUp.setFuelPricePerLitre(new BigDecimal(_price / createdFuelVol));
						}
						//pridanie FillUpu
						createdFillUp = mFillUpManager.createFillUp(createdFillUp, mSelectedCar);
						//zmena parametrov auta
						mSelectedCar.setActualMileage(mSelectedCar.getActualMileage() + createdFillUp.getDistanceFromLastFillUp());
						double avgAdding = mFillUpManager.getFuel(mSelectedCar) / ((mSelectedCar.getActualMileage() - mSelectedCar.getStartMileage())/ Double.valueOf(100.0));
						mSelectedCar.setAvgFuelConsumption(avgAdding);
						//update auta
						mCarManager.updateCar(mSelectedCar);
						
						Toast.makeText(this, R.string.addFillUpActivity_Toast_created, Toast.LENGTH_LONG).show();

						break;
					case UPDATING:
						Long oldDistance = mSelectedFillUp.getDistanceFromLastFillUp();
						Long oldCarODO = mSelectedCar.getActualMileage();
						//change fillUp values
						mSelectedFillUp.setFullFillUp(mCheckBoxIsFullFill.isChecked());
						mSelectedFillUp.setDistanceFromLastFillUp(createdDistance);
						
						mSelectedFillUp.setFuelVolume(Double.parseDouble(fuelVol.toString()));
						if(priceMode == SwitchPrice.perLitre){
							mSelectedFillUp.setFuelPricePerLitre(createdPrice);
							double _price = Double.valueOf(totalPrice.toString());
							mSelectedFillUp.setFuelPriceTotal(new BigDecimal(createdFuelVol * _price));
						}
						else{
							mSelectedFillUp.setFuelPriceTotal(createdPrice);
							double _price = Double.valueOf(totalPrice.toString());
							mSelectedFillUp.setFuelPricePerLitre(new BigDecimal(_price / createdFuelVol));
						}
						//update fillUp
						mFillUpManager.updateFillUp(mSelectedFillUp);
						double avgUpd = mFillUpManager.getFuel(mSelectedCar) / ((oldCarODO + createdDistance - oldDistance - mSelectedCar.getStartMileage())/ Double.valueOf(100.0));
						Log.w(TAG, "getFuel je " + mFillUpManager.getFuel(mSelectedCar) +", Mileage je " + (mSelectedCar.getActualMileage() - mSelectedCar.getStartMileage()));
						mSelectedCar.setActualMileage(oldCarODO + createdDistance - oldDistance);
						mSelectedCar.setAvgFuelConsumption(avgUpd);
						//update car
						mCarManager.updateCar(mSelectedCar);
						
						Toast.makeText(this, R.string.addFillUpActivity_Toast_updated, Toast.LENGTH_LONG).show();
						break;
					default:
						break;
					}
					
					setResult(RESULT_OK);
					finish();
				}
			}
			
			break;
		case R.id.btn_switch_distance:
			if(mode == Mode.UPDATING){
				Toast.makeText(this, R.string.addFillUpActivity_Toast_cannotUpdateByWholeDistance,
						Toast.LENGTH_LONG).show();
			}
			else{
				if(distanceMode == SwitchDistance.fromLast){
					distanceMode = SwitchDistance.whole;
					mBtnSwitchDistance.setText(R.string.addFillUpActivity_BtnTxt_distanceWhole);
				}
				else{
					distanceMode = SwitchDistance.fromLast;
					mBtnSwitchDistance.setText(R.string.addFillUpActivity_BtnTxt_distFromLastFillUp);
				}
			}
			break;
		case R.id.btn_switch_price:
			if(mode == Mode.UPDATING){
				if(priceMode == SwitchPrice.perLitre){
					priceMode = SwitchPrice.total;
					mBtnSwitchPrice.setText(R.string.addFillUpActivity_BtnTxt_priceTotal);
					mTxtPrice.setText(mSelectedFillUp.getFuelPriceTotal().toString());
				}
				else{
					priceMode = SwitchPrice.perLitre;
					mBtnSwitchPrice.setText(R.string.addFillUpActivity_BtnTxt_pricePerLitre);
					mTxtPrice.setText(mSelectedFillUp.getFuelPricePerLitre().toString());
				}
			}
			else{
				if(priceMode == SwitchPrice.perLitre){
					priceMode = SwitchPrice.total;
					mBtnSwitchPrice.setText(R.string.addFillUpActivity_BtnTxt_priceTotal);
				}
				else{
					priceMode = SwitchPrice.perLitre;
					mBtnSwitchPrice.setText(R.string.addFillUpActivity_BtnTxt_pricePerLitre);
				}
			}
		default:
			break;
		}
	}
	@Override
	public Intent getParentActivityIntent() {
		Intent intent = new Intent(this, ListFillUpsFragment.class);
		intent.putExtra(EXTRA_SELECTED_CAR, mSelectedCar);
		return intent;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mFillUpManager.close();
		mCarManager.close();
	}

	enum Mode {
		UPDATING, CREATING
	}

}
