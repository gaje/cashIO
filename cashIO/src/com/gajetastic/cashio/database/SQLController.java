package com.gajetastic.cashio.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SQLController {
	private DatabaseHelper dbhelper;
	private Context ourcontext;
	private SQLiteDatabase database;

	public SQLController(Context c) {
		ourcontext = c;
	}

	public SQLController open() throws SQLException {
		dbhelper = new DatabaseHelper(ourcontext);
		database = dbhelper.getWritableDatabase();
		return this;

	}

	public void close() {
		dbhelper.close();
	}

	public void insertData(String name, String type, Float amount) {

		ContentValues cv = new ContentValues();
		cv.put(DatabaseHelper.NAME, name);
		cv.put(DatabaseHelper.TYPE, type);
		cv.put(DatabaseHelper.AMOUNT, amount);
		database.insert(DatabaseHelper.TABLE_MAIN, null, cv);
		System.out.println("Finished executing insert!");
	}

	public Cursor readEntry() {

		String[] allColumns = new String[] { DatabaseHelper.KEY_ID,
				DatabaseHelper.NAME, DatabaseHelper.TYPE, DatabaseHelper.AMOUNT };

		Cursor c = database.query(DatabaseHelper.TABLE_MAIN, allColumns, null,
				null, null, null, null);

		if (c != null) {
			c.moveToFirst();
		}
		return c;

	}
	
	public float getTotalSumByType(String type){
		float amount;
		Cursor c = database.rawQuery("SELECT sum(amount) FROM main WHERE type = '"+ type +"' ;", null);
		if(c.moveToFirst())
		    amount = c.getFloat(0);
		else
		    amount = 0;
		return amount;
		
	}

}
