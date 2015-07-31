package sk.momosi.fuelapp.adapters;

import java.text.DecimalFormat;
import java.util.List;

import sk.momosi.fuel.R;
import sk.momosi.fuelapp.entities.entitiesImpl.Car;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListCarsAdapter extends BaseAdapter {

	public static final String TAG = "ListCarsAdapter";

	private List<Car> mItems;
	private LayoutInflater mInflater;

	public ListCarsAdapter(Context context, List<Car> listCars) {
		this.setItems(listCars);
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return (getItems() != null && !getItems().isEmpty()) ? getItems()
				.size() : 0;
	}

	@Override
	public Car getItem(int position) {
		return (getItems() != null && !getItems().isEmpty()) ? getItems().get(
				position) : null;
	}

	@Override
	public long getItemId(int position) {
		return (getItems() != null && !getItems().isEmpty()) ? getItems().get(
				position).getId() : position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		if (v == null) {
			v = mInflater.inflate(R.layout.list_item_car, parent, false);
			holder = new ViewHolder();

			holder.imgType = (ImageView) v.findViewById(R.id.img_itemcar_type);
			holder.txtNick = (TextView) v.findViewById(R.id.txt_itemcar_nick);
			holder.txtCarTypeName = (TextView) v.findViewById(R.id.txt_itemcar_type_name);
			holder.txtMileage = (TextView) v.findViewById(R.id.txt_itemcar_mileage);
			holder.txtConsumption = (TextView) v.findViewById(R.id.txt_itemcar_consumption);
			holder.txtConsumptionUnit = (TextView) v.findViewById(R.id.txt_itemcar_consumption_unit);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		// fill row data
		Car currentItem = getItem(position);
		if (currentItem != null) {
			holder.imgType.setImageResource(getImage(currentItem));
			holder.txtNick.setText(currentItem.getNick());
			holder.txtCarTypeName.setText(currentItem.getTypeName());
			
			String mileage = currentItem.getActualMileage().toString() + " ";
			if(currentItem.getDistanceUnit() == Car.CarDistanceUnit.kilometres){
				mileage += "km";
				holder.txtConsumptionUnit.setText("l/100km");
			}
			else{
				mileage += "mi";
				holder.txtConsumptionUnit.setText("l/100mi");
			}
			holder.txtMileage.setText(mileage);
			if (!currentItem.getActualMileage().equals(currentItem.getStartMileage())) {
				holder.txtConsumption.setText(new DecimalFormat("##.00").format(
						currentItem.getAvgFuelConsumption()).toString()+" ");
			} else {
				holder.txtConsumption.setText("N/A"+" ");
			}
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
		ImageView imgType;
		TextView txtNick;
		TextView txtCarTypeName;
		TextView txtMileage;
		TextView txtConsumption;
		TextView txtConsumptionUnit;
	}

	private int getImage(Car mCar) {
		switch (mCar.getCarType()) {
		case Sedan:
			return (int) R.drawable.ic_type_sedan;
		case Hatchback:
			return(int) R.drawable.ic_type_hatchback;
		case Combi:
			return(int) R.drawable.ic_type_combi;
		case Van:
			return (int)R.drawable.ic_type_van;
		case Motocycle:
			return (int)R.drawable.ic_type_moto;
		case Pickup:
			return(int) R.drawable.ic_type_pickup;
		case Quad:
			return (int)R.drawable.ic_type_quad;
		case Sport:
			return(int) R.drawable.ic_type_sport;
		case SUV:
			return(int) R.drawable.ic_type_suv;

		default:
			break;
		}
		return 0;
	}

}
