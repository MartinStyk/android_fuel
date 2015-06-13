package sk.momosi.fuelapp.activities;

import sk.momosi.fuel.R;
import sk.momosi.fuelapp.entities.Car;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.app.ActionBar;

import android.app.FragmentTransaction;
import android.content.Intent;

@SuppressWarnings("deprecation")
public class CarDataActivity extends FragmentActivity implements
		ActionBar.TabListener {

	public static final String CAR_CODE = "car_to_fragment";
	public static final int REQUEST_CODE_ADD_FILLUP = 40;

	private Car mCar;
	private CollectionPagerAdapter mCollectionPagerAdapter;
	private ViewPager mViewPager;
	private int currentFragmentIndex = 0;

	private Menu menu;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_car_data);

		Intent intent = getIntent();
		if (intent != null) {
			mCar = (Car) intent
					.getSerializableExtra(ListCarsActivity.EXTRA_ADDED_CAR);
			getActionBar().setTitle(mCar.getNick());
		}

		mCollectionPagerAdapter = new CollectionPagerAdapter(
				getSupportFragmentManager());

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mCollectionPagerAdapter);
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}

				});
		for (int i = 0; i < mCollectionPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mCollectionPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		currentFragmentIndex = tab.getPosition();
		mViewPager.setCurrentItem(tab.getPosition());
		//show/hide add button in actionbar menu
		if (menu != null) {
			MenuItem item = menu.findItem(R.id.action_add);
			if (currentFragmentIndex == 2) {
				item.setVisible(false);
			} else {
				item.setVisible(true);
			}
		}
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		this.menu = menu;
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_menu_list_fillups, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_add:
			if (currentFragmentIndex == 0) {
				Intent intent = new Intent(this, AddFillUpActivity.class);
				intent.putExtra(AddFillUpActivity.EXTRA_CAR, mCar);
				startActivityForResult(intent, REQUEST_CODE_ADD_FILLUP);
				return true;
			}
			if (currentFragmentIndex == 1) {
				Intent intent = new Intent(this, AddExpenseActivity.class);
				intent.putExtra(AddFillUpActivity.EXTRA_CAR, mCar);
				startActivity(intent);
				return true;
			}
			return false;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public Intent getParentActivityIntent() {
		Intent intent = new Intent(this, AddCarActivity.class);
		intent.putExtra(ListCarsActivity.FORCE_SHOW_LIST_CARS, true);
		return intent;
	}
	
	public class CollectionPagerAdapter extends FragmentPagerAdapter {

		final int NUM_ITEMS = 3; // number of tabs

		public CollectionPagerAdapter(FragmentManager fm) {
			super(fm);

		}

		@Override
		public Fragment getItem(int i) {

			Bundle args = new Bundle();
			args.putSerializable(CAR_CODE, mCar);

			switch (i) {
			case 0:
				ListFillUpsFragment fragment = new ListFillUpsFragment();
				fragment.setArguments(args);
				return fragment;
			case 1:
				ListExpensesFragment fragment1 = new ListExpensesFragment();
				fragment1.setArguments(args);
				return fragment1;
			case 2:
				StatisticsFragment fragment2 = new StatisticsFragment();
				fragment2.setArguments(args);
				return fragment2;
			}
			return null;
		}

		@Override
		public int getCount() {
			return NUM_ITEMS;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			String tabLabel = null;
			switch (position) {
			case 0:
				tabLabel = getString(R.string.fragmentTitle_fillups);
				break;
			case 1:
				tabLabel = getString(R.string.fragmentTitle_expenses);
				break;
			case 2:
				tabLabel = getString(R.string.fragmentTitle_statistics);
				break;
			}

			return tabLabel;
		}
	}
}