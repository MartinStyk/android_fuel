package sk.momosi.fuelapp.activities;

import java.util.List;

import sk.momosi.fuel.R;
import sk.momosi.fuelapp.adapters.ListExpensesAdapter;
import sk.momosi.fuelapp.dbaccess.ExpenseManager;
import sk.momosi.fuelapp.entities.Car;
import sk.momosi.fuelapp.entities.Expense;
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
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class ListExpensesFragment extends ListFragment {
	
	public static final String TAG = "ListExpensesFragment";
	public static final int REQUEST_CODE_UPDATE_EXPENSE = 31;
	
	private Car mCar;
	private ExpenseManager mExpenseManager;
	private List<Expense> mListExpenses;
	private ListExpensesAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_list_expenses,
				container, false);

		Bundle args = getArguments();
		if (args != null) {
			mCar = (Car) args.getSerializable(CarDataActivity.CAR_CODE);
			mExpenseManager = new ExpenseManager(getActivity());
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
		if(mExpenseManager != null)
			mExpenseManager.close();
	}
/*	  //zakomentovane z dovodu, ze onResume sa vola aj pri vytvoreni
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		mListExpenses = mExpenseManager.getExpensesOfCar(mCar.getId());
		mAdapter = new ListExpensesAdapter(getActivity(), mListExpenses);
		setListAdapter(mAdapter);
	}*/

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Expense clickedExpense = mAdapter.getItem(position);

		Intent i = new Intent(getActivity(), AddExpenseActivity.class);
		i.putExtra(AddExpenseActivity.EXTRA_EXPENSE, clickedExpense);
		startActivityForResult(i, REQUEST_CODE_UPDATE_EXPENSE);
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		OnItemLongClickListener listener = new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id) {
                Expense clickedExpense = mAdapter.getItem(position);
        		Log.d(TAG, "longClickedItem : " + clickedExpense.getId());

        		showDeleteDialogConfirmation(clickedExpense, arg1);
        		return true;
            }
        };
        getListView().setOnItemLongClickListener(listener);
	}
	
	private void showDeleteDialogConfirmation(final Expense toDelete, View v) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());

		alertDialogBuilder.setTitle(getString(R.string.listExpense_dialog_title));
		alertDialogBuilder.setMessage(getString(R.string.listExpense_dialog_msg) + " \""
				+ toDelete.getInfo() + "\"?");
		
		// set positive button YES message
		alertDialogBuilder.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// delete the employee and refresh the list
						if (mExpenseManager != null) {
							// delete Expense
							mExpenseManager.deleteExpense(toDelete);
						}
						dialog.dismiss();
						Toast.makeText(getActivity(), R.string.listExpense_Toast_deleted, Toast.LENGTH_SHORT).show();
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
		if (mCar != null && mExpenseManager != null) {
			mListExpenses = mExpenseManager.getExpensesOfCar(mCar.getId());
			mAdapter = new ListExpensesAdapter(getActivity(), mListExpenses);
			setListAdapter(mAdapter);
		}
	}
}