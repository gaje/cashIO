package com.gajetastic.cashio;

import java.text.NumberFormat;
import java.util.Locale;

import com.gajetastic.cashio.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
 
public class CashInFragment extends Fragment {
	private View rootView;
	private TableLayout tl;
	TextView totalValue;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
        rootView = inflater.inflate(R.layout.cash_in, container, false);
        tl = (TableLayout) rootView.findViewById(R.id.cashInTableScrollView);
        
        Button btnAddCashIn = (Button) rootView.findViewById(R.id.btnAddCashIn);
        
        
        
		btnAddCashIn.setOnClickListener(new View.OnClickListener() {
		  @Override
		  public void onClick(View v) {
		  	insertRow(v);
		  }
		});
		
		
		
		
        return rootView;
    }
    
    public void insertRow(View v) {
    	LayoutInflater inflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View newStockRow = inflator.inflate(R.layout.cash_in_row, null);
    	EditText incomeValue = (EditText) newStockRow.findViewById(R.id.editTextCashInValue);
    	incomeValue.addTextChangedListener(new CurrencyTextWatcher());
    	incomeValue.setOnFocusChangeListener(onValueInputChange);
    	incomeValue.setOnEditorActionListener(onEditorActionListener);
    	tl.addView(newStockRow);
    }
    
 
    public OnEditorActionListener onEditorActionListener = new OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if(actionId==EditorInfo.IME_ACTION_DONE){
                 v.clearFocus();
                 InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                 imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
            }
        return false;
        }
    };
    
    public OnFocusChangeListener onValueInputChange = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

          if (!hasFocus) {
        	totalValue = (TextView) rootView.findViewById(R.id.cashInTotalValue);
          	Float tempTotal = Float.parseFloat(totalValue.getText().toString());
          	TextView field = (TextView) v;
          	Float rowValue = Float.parseFloat(field.getText().toString());
          	TextView newTotal = (TextView) rootView.findViewById(R.id.cashInTotalValue);
          	newTotal.setText(Float.toString(tempTotal + rowValue));
          }
        }
     };
    
    
    class CurrencyTextWatcher implements TextWatcher {

        boolean mEditing;

        public CurrencyTextWatcher() {
            mEditing = false;
        }

        public synchronized void afterTextChanged(Editable s) {
            if(!mEditing) {
                mEditing = true;

                String digits = s.toString().replaceAll("\\D", "");
                NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
                try{
                    String formatted = nf.format(Double.parseDouble(digits)/100);
                    s.replace(0, s.length(), formatted);
                } catch (NumberFormatException nfe) {
                    s.clear();
                }
   
                mEditing = false;
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        public void onTextChanged(CharSequence s, int start, int before, int count) { }

    }
   
}