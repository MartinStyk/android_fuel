package sk.momosi.fuelapp.activities;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;

import sk.momosi.fuel.R;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import sk.momosi.fuelapp.dbaccess.FillUpManager;
import sk.momosi.fuelapp.entities.Car;
import sk.momosi.fuelapp.entities.FillUp;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class StatisticsFragment extends Fragment{
	//modifikacia
	
	public static final String TAG = "StatisticsFragment";
	
	private Car mCar;
	private FillUpManager mFillUpManager;
	private List<FillUp> listFillUps;
	
	private DataPoint[] values11;
	private DataPoint[] values12;
	private DataPoint[] values2;
	private int count;
	private double maxY1 = 0;
	private double minY1 = 1000;
	private double maxY2 = 0;
	private double minY2 = 1000;
	
	private GraphView mGraph01;
	private GraphView mGraph02;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		
		Bundle args = getArguments();
		if(args != null){
			mCar = (Car) args.getSerializable(CarDataActivity.CAR_CODE);
			mFillUpManager = new FillUpManager(getActivity());
			//mExpenseManager = new ExpenseManager(getActivity());
		}
		listFillUps = mFillUpManager.getFillUpsOfCar(mCar.getId());
		Collections.reverse(listFillUps);
		count = listFillUps.size();
		
		View rootView;
		if(count != 0){
			rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
			initViews(rootView);
		}
		else{
			rootView = inflater.inflate(R.layout.fragment_statistics_empty,  container, false);
		}
		return rootView;
	}
	
	/*private static Locale getLocale(String strCode) {
	     
	    for (Locale locale : NumberFormat.getAvailableLocales()) {
	        String code = NumberFormat.getCurrencyInstance(locale).getCurrency().getCurrencyCode();
	        if (strCode.equals(code)) {
	            return locale;
	        }
	    }  
	    return null;
	}*/
	
	private void initViews(View v){
		if(count != 0){
			//mJeden = (TextView) v.findViewById(R.id.txt_statistics_01);
			mGraph01 = (GraphView) v.findViewById(R.id.graph_01);
			mGraph02 = (GraphView) v.findViewById(R.id.graph_02);
			
			//mJeden.setText(mCar.getNick());
			generateData();
			
			Long distance = mCar.getActualMileage() - mCar.getStartMileage();
			distance /= 20;
			double difference;
			
			mGraph01.getViewport().setYAxisBoundsManual(true);
			mGraph01.getViewport().setXAxisBoundsManual(true);
			mGraph01.getViewport().setMinX(mCar.getStartMileage() + listFillUps.get(0).getDistanceFromLastFillUp() - distance);
			mGraph01.getViewport().setMaxX(mCar.getActualMileage() + distance);
			NumberFormat nfx1 = NumberFormat.getInstance();
			NumberFormat nfy1 = NumberFormat.getInstance();
			nfy1.setMinimumFractionDigits(2);
			nfy1.setMaximumFractionDigits(2);
			nfx1.setMaximumFractionDigits(0);
			mGraph01.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nfx1, nfy1){
				@Override
				public String formatLabel(double value, boolean isValueX){
					if(isValueX)
						return super.formatLabel(value, isValueX) + mCar.getDistanceUnitString();
					else
						return super.formatLabel(value, isValueX) + "l/100" + mCar.getDistanceUnitString();
				}
			});
			
			mGraph02.getViewport().setYAxisBoundsManual(true);
			mGraph02.getViewport().setXAxisBoundsManual(true);
			mGraph02.getViewport().setMinX(mCar.getStartMileage() + listFillUps.get(0).getDistanceFromLastFillUp() - distance);
			mGraph02.getViewport().setMaxX(mCar.getActualMileage() + distance);
			NumberFormat nfx2 = NumberFormat.getInstance();
			NumberFormat nfy2 = NumberFormat.getInstance();
			nfy2.setMinimumFractionDigits(3);
			nfy2.setMaximumFractionDigits(3);
			nfx2.setMaximumFractionDigits(0);
			mGraph02.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nfx2, nfy2){
				@Override
				public String formatLabel(double value, boolean isValueX){
					if(isValueX)
						return super.formatLabel(value, isValueX) + mCar.getDistanceUnitString();
					else
						return super.formatLabel(value, isValueX) + " " + mCar.getCurrencyFormatted() + "/l";
				}
			});
			
			difference = maxY1 - minY1;
			difference /= 10;
			if(difference == 0)
				difference = Double.valueOf(1);
			mGraph01.getViewport().setMinY(minY1 - difference);
			mGraph01.getViewport().setMaxY(maxY1 + difference);
		
			difference = maxY2 - minY2;
			difference /=10;
			if(difference == 0)
				difference = Double.valueOf(0.3);
			mGraph02.getViewport().setMinY(minY2 - difference);
			mGraph02.getViewport().setMaxY(maxY2 + difference);
			
			LineGraphSeries<DataPoint> series11 = new LineGraphSeries<DataPoint>(values11);
			LineGraphSeries<DataPoint> series12 = new LineGraphSeries<DataPoint>(values12);
			LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(values2);
			
			series12.setColor(Color.GREEN);
			mGraph01.addSeries(series11);
			mGraph01.addSeries(series12);
			mGraph02.addSeries(series2);
		}
	}
	
	private void generateData() {
		int i = 0;
        Long mileage = mCar.getStartMileage();
        values11 = new DataPoint[count];
        values12 = new DataPoint[count];
        values2 = new DataPoint[count];
        double fuel = 0;
        
        for (FillUp current : listFillUps) {
        	mileage += current.getDistanceFromLastFillUp();
        	fuel += current.getFuelVolume();
            double x = mileage.doubleValue();
            Double consumption = current.getFuelVolume() * 100 / (current.getDistanceFromLastFillUp()) ;
            double fx = consumption.doubleValue();
            DataPoint v = new DataPoint(x, fx);
            DataPoint w = new DataPoint(x, current.getFuelPricePerLitre().doubleValue());
            DataPoint z = new DataPoint(x, fuel / ((mileage - mCar.getStartMileage()) / Double.valueOf(100.0)));
            if(consumption < minY1)
            	minY1 = consumption;
            if(consumption > maxY1)
            	maxY1 = consumption;
            if(current.getFuelPricePerLitre().doubleValue() > maxY2)
            	maxY2 = current.getFuelPricePerLitre().doubleValue();
            if(current.getFuelPricePerLitre().doubleValue() < minY2)
            	minY2 = current.getFuelPricePerLitre().doubleValue();
            values11[i] = v;
            values2[i] = w;
            values12[i] = z;
            i++;
        }
    }
}
