package sk.momosi.fuelapp.adapters;

import java.util.List;

import sk.momosi.fuel.R;
import sk.momosi.fuelapp.entities.Car;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class SpinnerCarsAdapter extends BaseAdapter {
	
	public static final String TAG = "SpinnerCarsAdapter";
	
	private List<Car> mItems;
	private LayoutInflater mInflater;
	
	public SpinnerCarsAdapter(Context context, List<Car> listCars) {
		this.setItems(listCars);
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return (getItems() != null && !getItems().isEmpty()) ? getItems().size() : 0 ;
	}

	@Override
	public Car getItem(int position) {
		return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position) : null ;
	}

	@Override
	public long getItemId(int position) {
		return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position).getId() : position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		if(v == null) {
			v = mInflater.inflate(R.layout.fragment_statistics, parent, false);
			holder = new ViewHolder();
			holder.txtCarManufacturerName = (TextView) v.findViewById(R.id.txt_statistics_jeden);
			holder.txtCarTypeName = (TextView) v.findViewById(R.id.txt_statistics_dva);
			v.setTag(holder);
		}
		else {
			holder = (ViewHolder) v.getTag();
		}
		
		// fill row data
		Car currentItem = getItem(position);
		if(currentItem != null) {
			holder.txtCarManufacturerName.setText(currentItem.getNick());
			holder.txtCarTypeName.setText(currentItem.getTypeName());
		}
		
		return v;
	}
	
	public List<Car> getItems() {
		return mItems;
	}

	public void setItems(List<Car> mItems) {
		this.mItems = mItems;
	}

	class ViewHolder {
		TextView txtCarManufacturerName;
		TextView txtCarTypeName;
	}
}
