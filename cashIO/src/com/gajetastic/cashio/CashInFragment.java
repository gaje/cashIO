package com.gajetastic.cashio;

import java.text.NumberFormat;

import com.gajetastic.cashio.R;
import com.gajetastic.cashio.database.SQLController;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class CashInFragment extends Fragment {
	View rootView;
	NumberFormat format = NumberFormat.getCurrencyInstance();
	EditText incomeValue;
	EditText totalValueBox;
	SQLController sqlcon;
	TableLayout table_layout;
	EditText name_et, amount_et;
	ProgressDialog PD;
	int curPage;
	String type;
	int hint;

	public CashInFragment(int curPage) {
		this.curPage = curPage;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		sqlcon = new SQLController(getActivity());

		rootView = inflater.inflate(R.layout.cash_in, container, false);
		table_layout = (TableLayout) rootView.findViewById(R.id.cashInTableScrollView);
		TextView tv = (TextView) rootView.findViewById(R.id.tvPageTitle);
		Button btnAddCashIn = (Button) rootView.findViewById(R.id.btnAddCashIn);
		
		if (curPage == 1) {
			tv.setText(R.string.incomePageTitle);
			type = "income";
			hint = R.string.hintCashInName;
			btnAddCashIn.setText(R.string.btnAddCashIn);
		} else {
			tv.setText(R.string.billsPageTitle);
			type = "bill";
			hint = R.string.hintBillName;
			btnAddCashIn.setText(R.string.btnAddBill);
		}
		
		btnAddCashIn.setOnClickListener(addCashInBtnListener);
		totalValueBox = (EditText) rootView.findViewById(R.id.cashInTotalValue);
		BuildTable();

		return rootView;
	}

	public void insertRow(View v) {
		LayoutInflater inflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View newStockRow = inflator.inflate(R.layout.cash_in_row, null);
		EditText nameField = (EditText) newStockRow.findViewById(R.id.editTextCashInName);
		nameField.setHint(hint);
		incomeValue = (EditText) newStockRow.findViewById(R.id.editTextCashInValue);
		incomeValue.addTextChangedListener(new CurrencyTextWatcher(this.incomeValue));
		incomeValue.setOnEditorActionListener(onEditorActionListener);
		table_layout.addView(newStockRow);
	}
	
	public void hideInputRow(View v){
		
	}

	public void deleteRow(final View v) {

		new AlertDialog.Builder(getActivity())
				.setMessage("Are you sure you want to delete this row?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								String row = v.getTag().toString();
								sqlcon.open();
								boolean isDeleted = sqlcon.deleteRow(Integer
										.parseInt(row));
								sqlcon.close();
								if (isDeleted) {
									table_layout.removeView((View) v.getParent().getParent());
									table_layout.invalidate();
									updateRowColors();
									updateTotal();
								}
							}
						}).setNegativeButton("No", null).show();
	}

	public void updateTotal() {
		sqlcon.open();
		Float f = sqlcon.getTotalSumByType(type);
		totalValueBox.setText(format.format(f));
		sqlcon.close();
	}

	public void updateRowColors() {
		for (int i = 0; i < table_layout.getChildCount(); i++) {
			TableRow row = (TableRow) table_layout.getChildAt(i);
			row.setBackgroundColor(Color.parseColor("#d9d9d9"));
			if (i % 2 == 0) {
				row.setBackgroundColor(Color.parseColor("#cccccc"));
			}
		}
	}

	public View.OnClickListener addCashInBtnListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			insertRow(v);
		}

	};

	public Button.OnClickListener deleteCashInBtnListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			deleteRow(v);
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
					task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
							(Void[]) null);
				else
					task.execute((Void[]) null);
				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
			}
			return false;
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
		Cursor c = sqlcon.readEntry(type);

		int rows = c.getCount();
		c.moveToFirst();

		for (int i = 0; i < rows; i++) {

			TableRow row = new TableRow(getActivity());
			row.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT,
					TableRow.LayoutParams.WRAP_CONTENT));
			row.setGravity(Gravity.CENTER_VERTICAL);
			
			FrameLayout fl = new FrameLayout(getActivity());
			TableRow.LayoutParams params = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, 100);
			params.gravity=Gravity.CENTER_VERTICAL;
			fl.setLayoutParams(params);
			fl.setPadding(10, 30, 10, 20);
			
			Button button = new Button(getActivity());
			button.setLayoutParams(new TableRow.LayoutParams(45, 45, 0));
			button.setBackgroundResource(R.drawable.round_button);
			button.setTextColor(Color.parseColor("#eeeeee"));
			button.setText("\u2014");
			button.setTextSize(16);
			button.setGravity(Gravity.TOP);
			button.setPadding(10, 0, 0, 0);
			button.setTypeface(Typeface.DEFAULT_BOLD);
			button.setOnClickListener(deleteCashInBtnListener);
			button.setTag(Integer.valueOf(c.getString(0)));
			fl.addView(button);
			row.addView(fl);

			EditText etName = new EditText(getActivity());
			EditText etValue = new EditText(getActivity());

			etName.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.WRAP_CONTENT,
					TableRow.LayoutParams.WRAP_CONTENT, 0.3f));
			etName.setGravity(Gravity.LEFT);
			etName.setPadding(10, 5, 10, 5);
			etName.setTextSize(18);
			etName.setMaxWidth(350);
			etName.setBackgroundColor(Color.TRANSPARENT);
			etName.setFocusable(false);
			etName.setClickable(false);
			etName.setTextColor(Color.parseColor("#333333"));
			etName.setText(c.getString(1));
			row.addView(etName);

			etValue.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.WRAP_CONTENT,
					TableRow.LayoutParams.WRAP_CONTENT, 0.4f));
			etValue.setGravity(Gravity.RIGHT);
			etValue.setPadding(10, 5, 15, 5);
			etValue.setTextSize(18);
			etValue.setBackgroundColor(Color.TRANSPARENT);
			etValue.setFocusable(false);
			etValue.setClickable(false);
			etValue.setTextColor(Color.parseColor("#333333"));
			etValue.setText(c.getString(1));
			Float val = Float.parseFloat(c.getString(3));
			etValue.setText(format.format(val));
			row.addView(etValue);

			c.moveToNext();

			table_layout.addView(row, new TableLayout.LayoutParams(
					TableLayout.LayoutParams.MATCH_PARENT,
					TableLayout.LayoutParams.WRAP_CONTENT));
		}

		sqlcon.close();

		updateRowColors();
		updateTotal();

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
			Float amount = Float.parseFloat(amount_et.getText().toString()
					.substring(1));
			System.out.println(amount);
			// inserting data
			sqlcon.open();
			try {
				sqlcon.insertData(name, type, amount);
			} catch (SQLException e) {
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