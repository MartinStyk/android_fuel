package sk.momosi.fuelapp.adapters;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import sk.momosi.fuel.R;
import sk.momosi.fuelapp.entities.entitiesImpl.FillUp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class ListFillUpsAdapter extends BaseAdapter {

    public static final String TAG = "ListFillUpsAdapter";

    private List<FillUp> mItems;
    private LayoutInflater mInflater;

    public ListFillUpsAdapter(Context context, List<FillUp> listFillUps) {
        this.setItems(listFillUps);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().size() : 0;
    }

    @Override
    public FillUp getItem(int position) {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position).getId() : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            v = mInflater.inflate(R.layout.list_item_fillup, parent, false);
            holder = new ViewHolder();
            //init views

            holder.txtDistanceFromLastFillUp = (TextView) v.findViewById(R.id.txt_itemfillup_distance);
            holder.txtIsFullFillUp = (TextView) v.findViewById(R.id.txt_itemfillup_isfullfillup);
            holder.txtFuelVolume = (TextView) v.findViewById(R.id.txt_itemfillup_fuel_volume);
            holder.txtPriceTotal = (TextView) v.findViewById(R.id.txt_itemfillup_price_total);
            holder.txtPricePerLitre = (TextView) v.findViewById(R.id.txt_itemfillup_price_per_litre);
            holder.txtConsumptionSymbol = (TextView) v.findViewById(R.id.txt_itemfillup_consumption_symbol);
            holder.txtAvgSymbol = (TextView) v.findViewById(R.id.txt_itemfillup_avg_symbol);
            holder.txtConsumption = (TextView) v.findViewById(R.id.txt_itemfillup_consumption);
            holder.txtDriven = (TextView) v.findViewById(R.id.txt_itemfillup_driven);
            holder.txtPricePerLitreSymbol = (TextView) v.findViewById(R.id.txt_itemfillup_price_per_liter_symbol);
            holder.txtPriceTotalSymbol = (TextView) v.findViewById(R.id.txt_itemfillup_price_total_symbol);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        // fill row data
        FillUp currentItem = getItem(position);
        if (currentItem != null) {
            //set views
            holder.txtDistanceFromLastFillUp.setText(currentItem.getDistanceFromLastFillUp().toString());
            holder.txtDriven.setText(currentItem.getCar().getDistanceUnitString());
            holder.txtConsumptionSymbol.setText("l/100" + currentItem.getCar().getDistanceUnitString());

            if (currentItem.isFullFillUp()) {
                holder.txtIsFullFillUp.setText(R.string.listFillups_isFull);
            } else {
                holder.txtIsFullFillUp.setText(R.string.listFillUps_isNotFull);
            }

            String currency = " " + currentItem.getCar().getCurrencyFormatted();
            holder.txtPriceTotalSymbol.setText(currency);
            currency += "/l";
            holder.txtPricePerLitreSymbol.setText(currency);

            DecimalFormat bddf = new DecimalFormat();
            bddf.setMaximumFractionDigits(2);
            bddf.setMinimumFractionDigits(0);
            bddf.setGroupingUsed(false);

            holder.txtFuelVolume.setText(bddf.format(currentItem.getFuelVolume()));

            bddf.setMinimumFractionDigits(2);
            String priceTotal = bddf.format(currentItem.getPrice().setScale(2, BigDecimal.ROUND_DOWN));
            holder.txtPriceTotal.setText(priceTotal);

            bddf.setMaximumFractionDigits(3);
            bddf.setMinimumFractionDigits(3);
            holder.txtPricePerLitre.setText(bddf.format(currentItem.getFuelPricePerLitre()));
            if (currentItem.isFullFillUp()) {
                Log.w(TAG, currentItem.getFuelVolume() + " * 100 / " + currentItem.getDistanceFromLastFillUp());
                Double consumption = currentItem.getFuelVolume() * 100 / (currentItem.getDistanceFromLastFillUp());
                bddf.setMinimumFractionDigits(2);
                bddf.setMaximumFractionDigits(2);
                holder.txtConsumption.setText(bddf.format(consumption));
            } else {
                holder.txtConsumption.setText("N/A");
            }
        }

        return v;
    }

    public List<FillUp> getItems() {
        return mItems;
    }

    public void setItems(List<FillUp> mItems) {
        Collections.sort(mItems, new CustomExpenseEntryComparator());
        this.mItems = mItems;
    }

    class ViewHolder {
        TextView txtDistanceFromLastFillUp;
        TextView txtIsFullFillUp;
        TextView txtFuelVolume;
        TextView txtPriceTotal;
        TextView txtPricePerLitre;
        TextView txtConsumption;
        TextView txtConsumptionSymbol;
        TextView txtAvgSymbol;
        TextView txtDriven;
        TextView txtPricePerLitreSymbol;
        TextView txtPriceTotalSymbol;
    }

}
