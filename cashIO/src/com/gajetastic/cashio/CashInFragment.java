package com.gajetastic.cashio;

import java.text.NumberFormat;
import java.util.Locale;

import com.gajetastic.cashio.R;
import com.gajetastic.cashio.database.SQLController;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class CashInFragment extends Fragment {
	View rootView;
	NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
	EditText incomeValue;
	EditText totalValueBox;
	SQLController sqlcon;
	TableLayout table_layout;
	EditText name_et, amount_et;
	ProgressDialog PD;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		sqlcon = new SQLController(getActivity());
		
		rootView = inflater.inflate(R.layout.cash_in, container, false);
		table_layout = (TableLayout) rootView.findViewById(R.id.cashInTableScrollView);
		
		Button btnAddCashIn = (Button) rootView.findViewById(R.id.btnAddCashIn);
		btnAddCashIn.setOnClickListener(addCashInBtnListener);
		totalValueBox = (EditText) rootView.findViewById(R.id.cashInTotalValue);
		totalValueBox.addTextChangedListener(new CurrencyTextWatcher(this.totalValueBox));
		
		BuildTable();
		
		return rootView;
	}

	public void insertRow(View v) {
		LayoutInflater inflator = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View newStockRow = inflator.inflate(R.layout.cash_in_row, null);
		incomeValue = (EditText) newStockRow
				.findViewById(R.id.editTextCashInValue);
		incomeValue.addTextChangedListener(new CurrencyTextWatcher(
				this.incomeValue));
		incomeValue.setOnFocusChangeListener(onValueInputChange);
		incomeValue.setOnEditorActionListener(onEditorActionListener);
		table_layout.addView(newStockRow);
	}

	public View.OnClickListener addCashInBtnListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			insertRow(v);
		}

	};

	public OnEditorActionListener onEditorActionListener = new OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				v.clearFocus();
				name_et = (EditText) rootView
						.findViewById(R.id.editTextCashInName);
				amount_et = (EditText) rootView
						.findViewById(R.id.editTextCashInValue);

				MyAsync task = new MyAsync();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
					task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
				else
					task.execute((Void[])null);
				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
			}
			return false;
		}
	};

	public OnFocusChangeListener onValueInputChange = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {

			if (!hasFocus) {
				// Float tempTotal =
				// Float.parseFloat(totalValueBox.getText().toString().substring(1));
				// TextView field = (TextView) v;
				// Float rowValue =
				// Float.parseFloat(field.getText().toString().substring(1));
				// totalValueBox.setText(Float.toString(tempTotal + rowValue));
			}
		}
	};

	class CurrencyTextWatcher implements TextWatcher {

		boolean mEditing;
		EditText thisInstance;

		public CurrencyTextWatcher(EditText instanceValue) {
			mEditing = false;
			thisInstance = instanceValue;
		}

		public synchronized void afterTextChanged(Editable s) {
			if (!mEditing) {
				mEditing = true;

				String digits = s.toString().replaceAll("\\D", "");

				try {
					String formatted = nf
							.format(Double.parseDouble(digits) / 100);
					s.replace(0, s.length(), formatted);
				} catch (NumberFormatException nfe) {
					s.clear();
				}

				mEditing = false;
			}
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (!s.toString().matches(
					"^\\$(\\d{1,3}(\\,\\d{3})*|(\\d+))(\\.\\d{2})?$")) {
				String userInput = "" + s.toString().replaceAll("[^\\d]", "");
				StringBuilder cashAmountBuilder = new StringBuilder(userInput);

				while (cashAmountBuilder.length() > 3
						&& cashAmountBuilder.charAt(0) == '0') {
					cashAmountBuilder.deleteCharAt(0);
				}
				while (cashAmountBuilder.length() < 3) {
					cashAmountBuilder.insert(0, '0');
				}
				cashAmountBuilder.insert(cashAmountBuilder.length() - 2, '.');
				cashAmountBuilder.insert(0, '$');

				thisInstance.setText(cashAmountBuilder.toString());
				// keeps the cursor always to the right
				Selection.setSelection(thisInstance.getText(),
						cashAmountBuilder.toString().length());

			}
		}

	}

	private void BuildTable() {

		sqlcon.open();
		Cursor c = sqlcon.readEntry();

		int rows = c.getCount();
		int cols = c.getColumnCount();

		c.moveToFirst();

		// outer for loop
		for (int i = 0; i < rows; i++) {

			TableRow row = new TableRow(getActivity());
			row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1.0f));

			// inner for loop
			for (int j = 0; j < cols; j++) {
					
				if(j==0){
					Button button = new Button(getActivity());
					button.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT));
					button.setBackgroundResource(R.drawable.round_button);
					button.setTextColor(Color.parseColor("#FFFFFF"));
					button.setText("Delete");
					row.addView(button);
				} else if(j!=2){
					TextView tv = new TextView(getActivity());
					tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT));
					if(j==1){
						tv.setGravity(Gravity.LEFT);
						tv.setPadding(30,5,10,5);
					} else {
						tv.setGravity(Gravity.RIGHT);
						tv.setPadding(10,5,10,5);
					}
					tv.setTextSize(18);
					tv.setTextColor(Color.parseColor("#FFFFFF"));
					if(j==3){
						tv.addTextChangedListener(new CurrencyTextWatcher((EditText) tv));;
					}
					tv.setText(c.getString(j));
	
					row.addView(tv);
				}

			}

			c.moveToNext();

			table_layout.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));
		}
		Float f = sqlcon.getTotalSumByType("income");
		
		totalValueBox.setText(f.toString());
		sqlcon.close();
	}
	
	
	private class MyAsync extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();

			table_layout.removeAllViews();

			PD = new ProgressDialog(getActivity());
			PD.setTitle("Please Wait..");
			PD.setMessage("Loading...");
			PD.setCancelable(false);
			PD.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			String name = name_et.getText().toString();
			String type = "income";
			Float amount = Float.parseFloat(amount_et.getText().toString()
					.substring(1));
			System.out.println(amount);
			// inserting data
			sqlcon.open();
			try{
			sqlcon.insertData(name, type, amount);
			} catch(SQLException e){
				System.out.println("Error inserting data: " + e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			BuildTable();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			PD.dismiss();
		}
	}

}