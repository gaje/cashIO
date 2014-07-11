package com.gajetastic.cashio;

import java.text.NumberFormat;

import com.gajetastic.cashio.R;
import com.gajetastic.cashio.database.SQLController;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
 
public class DashboardFragment extends Fragment {

	SQLController sqlcon;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	sqlcon = new SQLController(getActivity());
    	NumberFormat format = NumberFormat.getCurrencyInstance();
    	
        View rootView = inflater.inflate(R.layout.dashboard, container, false);
        TextView value = (TextView) rootView.findViewById(R.id.dashboard_value);
        
        sqlcon.open();
        value.setText(format.format(sqlcon.getTotalSaved()));
        sqlcon.close();
        return rootView;
    }
}