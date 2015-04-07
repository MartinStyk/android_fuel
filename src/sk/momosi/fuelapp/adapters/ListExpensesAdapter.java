package sk.momosi.fuelapp.adapters;

import java.text.DecimalFormat;
import java.util.List;

import sk.momosi.fuel.R;

import sk.momosi.fuelapp.entities.Car;
import sk.momosi.fuelapp.entities.Expense;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListExpensesAdapter extends BaseAdapter{
public static final String TAG = "ListExpensesAdapter";
	
	private List<Expense> mItems;
	private LayoutInflater mInflater;
	
	public ListExpensesAdapter(Context context, List<Expense> listExpenses) {
		this.setItems(listExpenses);
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return (getItems() != null && !getItems().isEmpty()) ? getItems().size() : 0 ;
	}

	@Override
	public Expense getItem(int position) {
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
			v = mInflater.inflate(R.layout.list_item_expense, parent, false);
			holder = new ViewHolder();
			
			//init views
			holder.txtPriceSymbol = (TextView) v.findViewById(R.id.txt_itemexpense_price_currency);
			holder.txtInfo = (TextView) v.findViewById(R.id.txt_itemexpense_title);
			holder.txtPrice = (TextView) v.findViewById(R.id.txt_itemexpense_price);
			holder.txtDate = (TextView) v.findViewById(R.id.txt_itemexpense_date);
			
			v.setTag(holder);
		}
		else {
			holder = (ViewHolder) v.getTag();
		}
		
		Expense currentItem = getItem(position);
		if(currentItem != null) {
			DecimalFormat bddf = new DecimalFormat();
			//set views
			if(mItems.get(0).getCar().getCarCurrency() == Car.CarCurrency.EUR){
				holder.txtPriceSymbol.setText(" �");
			}
			else{
				holder.txtPriceSymbol.setText(" Kc");
			}
			holder.txtInfo.setText(currentItem.getInfo());
			holder.txtPrice.setText(bddf.format(currentItem.getPrice()));
			holder.txtDate.setText(currentItem.getDate().getDate() + "."
					+ (currentItem.getDate().getMonth() + 1) + "."
					+ (currentItem.getDate().getYear() + 1900));
		}
		return v;
	}
	
	public List<Expense> getItems() {
		return mItems;
	}

	public void setItems(List<Expense> mItems) {
		this.mItems = mItems;
	}

	class ViewHolder {
		TextView txtInfo;
		TextView txtPrice;
		TextView txtPriceSymbol;
		TextView txtDate;
	}

}