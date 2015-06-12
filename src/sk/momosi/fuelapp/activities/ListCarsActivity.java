package sk.momosi.fuelapp.activities;

import java.util.ArrayList;
import java.util.List;

import sk.momosi.fuel.R;
import sk.momosi.fuelapp.adapters.ListCarsAdapter;
import sk.momosi.fuelapp.dbaccess.CarManager;
import sk.momosi.fuelapp.entities.Car;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;


public class ListCarsActivity extends Activity implements
		OnItemLongClickListener, OnItemClickListener {

	public static final String TAG = "ListCarsActivity";

	public static final int REQUEST_CODE_ADD_CAR = 40;
	public static final int REQUEST_CODE_DELETED_CAR = 666;
	public static final int REQUEST_CODE_REFRESH_CARS = 18;
	public static final String EXTRA_ADDED_CAR = "extra_key_added_car";
	public static final String FORCE_SHOW_LIST_CARS = "force_list_cars";

	public static final String PREFS_IS_SHOW_LAST_CAR_ENABLED = "last_car_enabled";
	public static final String PREFS_CAR_ID_KEY = "car_id";
	public static final String SHARED_PREFERENCES_NAME = "sk.momosi.fuelapp.preferences";

	private SharedPreferences mSharedPreferences;
	private boolean mForceThisActivity = false;
	private boolean isShowLastViewedCarEnabled;

	private ListView mListviewCars;
	private TextView mTxtEmptyListCars;

	private ListCarsAdapter mAdapter;
	private List<Car> mListCars;
	private CarManager mCarManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_cars);
		mSharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME,
				MODE_PRIVATE);
		isShowLastViewedCarEnabled = mSharedPreferences.getBoolean(
				PREFS_IS_SHOW_LAST_CAR_ENABLED, false);

		openLastViewedCarIfPossible(savedInstanceState);

		initViews();
	}

	private void initViews() {
		this.mListviewCars = (ListView) findViewById(R.id.list_cars);
		this.mTxtEmptyListCars = (TextView) findViewById(R.id.txt_empty_list_cars);
		this.mListviewCars.setOnItemClickListener(this);
		this.mListviewCars.setOnItemLongClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case REQUEST_CODE_ADD_CAR:
			if (resultCode == RESULT_OK) {
				// add the added car to the listCars and refresh the listView
				if (data != null) {
					Car createdCar = (Car) data
							.getSerializableExtra(EXTRA_ADDED_CAR);
					if (createdCar != null) {
						if (mListCars == null)
							mListCars = new ArrayList<Car>();
						mListCars.add(createdCar);

						if (mListviewCars.getVisibility() != View.VISIBLE) {
							mListviewCars.setVisibility(View.VISIBLE);
							mTxtEmptyListCars.setVisibility(View.GONE);
						}

						if (mAdapter == null) {
							mAdapter = new ListCarsAdapter(this, mListCars);
							mListviewCars.setAdapter(mAdapter);
						} else {
							mAdapter.setItems(mListCars);
							mAdapter.notifyDataSetChanged();
						}
					}
				}
			}

			break;
		case REQUEST_CODE_DELETED_CAR:
			if (resultCode == RESULT_OK) {
				mListCars = mCarManager.getAllCars();
				mAdapter.setItems(mListCars);
				mAdapter.notifyDataSetChanged();
			}
			break;
		case REQUEST_CODE_REFRESH_CARS:
			mListCars = mCarManager.getAllCars();
			mAdapter.setItems(mListCars);
			mAdapter.notifyDataSetChanged();

			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mCarManager.close();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		Car clickedCar = mAdapter.getItem(position);
		Log.d(TAG,
				"shortClickedItem : " + clickedCar.getNick()
						+ clickedCar.getTypeName());

		// save id of last opened car to shared preferences, on next start
		// activity of this car will be started
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putLong(PREFS_CAR_ID_KEY, clickedCar.getId());
		editor.commit();

		Intent i = new Intent(this, CarDataActivity.class);
		i.putExtra(EXTRA_ADDED_CAR, clickedCar);
		startActivityForResult(i, REQUEST_CODE_DELETED_CAR);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		Car clickedCar = mAdapter.getItem(position);
		// showDeleteDialogConfirmation(clickedCar);
		Log.d(TAG,
				"longClickedItem : " + clickedCar.getNick()
						+ clickedCar.getTypeName());

		Intent i = new Intent(this, UpdateCarActivity.class);
		i.putExtra(UpdateCarActivity.EXTRA_CAR, clickedCar);
		startActivityForResult(i, REQUEST_CODE_DELETED_CAR);

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_menu_list_cars, menu);
		
		MenuItem item = menu.findItem(R.id.action_options_show_last);
		if(isShowLastViewedCarEnabled){		
			item.setChecked(true);
		}
		else{
			item.setChecked(false);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_add:
			Intent intent = new Intent(this, AddCarActivity.class);
			startActivityForResult(intent, REQUEST_CODE_ADD_CAR);
			return true;
		case R.id.action_options_show_last:
			SharedPreferences.Editor editor = mSharedPreferences.edit();
			if(item.isChecked()){
				editor.putBoolean(PREFS_IS_SHOW_LAST_CAR_ENABLED, false);
				editor.commit();
				isShowLastViewedCarEnabled=false;
				item.setChecked(false);
			}else{
				editor.putBoolean(PREFS_IS_SHOW_LAST_CAR_ENABLED, true);
				editor.commit();
				isShowLastViewedCarEnabled=true;
				item.setChecked(true);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onResume() {
		refreshCarsList();
		super.onResume();
	}

	private void refreshCarsList() {
		// fill the listView
		mCarManager = new CarManager(this);
		mListCars = mCarManager.getAllCars();

		// set views
		if (mListCars != null && !mListCars.isEmpty()) {
			mAdapter = new ListCarsAdapter(this, mListCars);
			mListviewCars.setAdapter(mAdapter);
		} else {
			mTxtEmptyListCars.setVisibility(View.VISIBLE);
			mListviewCars.setVisibility(View.GONE);
		}
	}

	private void openLastViewedCarIfPossible(Bundle savedInstanceState) {

		if (!isShowLastViewedCarEnabled)
			return;

		// workaround when using actionbar up navagiation and starting this
		// activity
		Intent intent = getIntent();
		if (intent != null) {
			mForceThisActivity = intent.getBooleanExtra(FORCE_SHOW_LIST_CARS,
					false);
		}

		if (savedInstanceState != null || mForceThisActivity)
			return;

		Long carId = mSharedPreferences.getLong(PREFS_CAR_ID_KEY, Long.MAX_VALUE);
		Car lastViewedCar;

		if (carId == Long.MAX_VALUE)
			return;

		if (mCarManager == null) {
			mCarManager = new CarManager(this);
		}
		try {
			lastViewedCar = mCarManager.getCarById(carId);
		} catch (Exception e) {
			Log.w(TAG, "Last viewed car was already deleted from database");
			return;
		}

		if (lastViewedCar == null) {
			Log.w(TAG, "Last viewed car is null ?!");
			return;
		}
		Intent i = new Intent(this, CarDataActivity.class);
		i.putExtra(EXTRA_ADDED_CAR, lastViewedCar);
		startActivityForResult(i, REQUEST_CODE_DELETED_CAR);

	}
	/*
	 * @Override public void onRestart(){ super.onRestart(); finish();
	 * startActivity(getIntent()); }
	 */
}
