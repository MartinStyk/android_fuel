package sk.momosi.fuelapp.activities;

import java.util.List;

import sk.momosi.fuel.R;
import sk.momosi.fuelapp.adapters.ListFillUpsAdapter;
import sk.momosi.fuelapp.dbaccess.CarManager;
import sk.momosi.fuelapp.dbaccess.FillUpManager;
import sk.momosi.fuelapp.entities.Car;
import sk.momosi.fuelapp.entities.FillUp;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class ListFillUpsFragment extends ListFragment {

	public static final String TAG = "ListFillUpsFragment";
	public static final int REQUEST_CODE_UPDATE_FILLUP = 30;
	
	private Bundle args;
	
	private Car mCar;
	private FillUpManager mFillUpManager;
	private List<FillUp> listFillUps;
	private ListFillUpsAdapter mAdapter;
	private CarManager mCarManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_list_fillups,
				container, false);

		args = getArguments();
		if (args != null) {
			mCar = (Car) args.getSerializable(CarDataActivity.CAR_CODE);
			mFillUpManager = new FillUpManager(getActivity());
			mCarManager = new CarManager(getActivity());
		}
		return rootView;
	}

	@Override
	public void onResume() {
		refreshList();
		super.onResume();
	}
	@Override
	public void onDestroyView(){
		super.onDestroyView();
		
		if(mFillUpManager != null)
			mFillUpManager.close();
		
		if(mCarManager != null)
			mCarManager.close();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		listFillUps = mFillUpManager.getFillUpsOfCar(mCar.getId());
		mAdapter = new ListFillUpsAdapter(getActivity(), listFillUps);
		setListAdapter(mAdapter);
	}
	
	@Override
	public void onListItemClick(ListView list, View v, int position, long id) {
		super.onListItemClick(list, v, position, id);
		FillUp clickedFillUp = mAdapter.getItem(position);

		Intent i = new Intent(getActivity(), AddFillUpActivity.class);
		i.putExtra(AddFillUpActivity.EXTRA_FILLUP, clickedFillUp);
		startActivityForResult(i, REQUEST_CODE_UPDATE_FILLUP);
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		OnItemLongClickListener listener = new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id) {
                FillUp clickedFillUp = mAdapter.getItem(position);
        		Log.d(TAG, "longClickedItem : " + clickedFillUp.getId());

        		showDeleteDialogConfirmation(clickedFillUp, arg1);
        		return true;
            }
        };
        getListView().setOnItemLongClickListener(listener);
	}
	
	private void showDeleteDialogConfirmation(final FillUp toDelete, View v) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());

		alertDialogBuilder.setTitle(getString(R.string.listFillUps_Dialog_title));
		alertDialogBuilder.setMessage(getString(R.string.listFillUps_Dialog_msg) + "" + mCar.getStartMileage() + " -> " + mCar.getActualMileage());
		
		// set positive button YES message
		alertDialogBuilder.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// delete the employee and refresh the list
						if (mFillUpManager != null) {
							// actualize car mileage
							mCar.setActualMileage(mCar.getActualMileage() - toDelete.getDistanceFromLastFillUp());
							// delete FillUp
							mFillUpManager.deleteFillUp(toDelete);
							// actualize car average consumption
							if(!mCar.getActualMileage().equals(mCar.getStartMileage())){
								double avgAdding = mFillUpManager.getFuel(mCar)
										/ ((mCar.getActualMileage() - mCar
												.getStartMileage()) / Double
												.valueOf(100.0));
								mCar.setAvgFuelConsumption(avgAdding);
							}
							else{
								mCar.setAvgFuelConsumption(0.0);
							}
							// update Car
							mCarManager.updateCar(mCar);
						}

						dialog.dismiss();
						Toast.makeText(getActivity(), R.string.listFillUps_Toast_deleted, Toast.LENGTH_SHORT).show();
						onResume();
					}
				});

		// set neutral button OK
		alertDialogBuilder.setNeutralButton(android.R.string.no,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Dismiss the dialog
						dialog.dismiss();
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();
		// show alert
		alertDialog.show();
	}
	
	private void refreshList(){
		if (args != null) {
			mCar = (Car) args.getSerializable(CarDataActivity.CAR_CODE);
			mFillUpManager = new FillUpManager(getActivity());
			mCarManager = new CarManager(getActivity());
			mCar = mCarManager.getCarById(mCar.getId());
		}
		if(mCar != null && mFillUpManager != null){
			listFillUps = mFillUpManager.getFillUpsOfCar(mCar.getId());
			mAdapter = new ListFillUpsAdapter(getActivity(), listFillUps);
			setListAdapter(mAdapter);
		}
	}
}