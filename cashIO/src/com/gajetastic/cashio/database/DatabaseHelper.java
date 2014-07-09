package com.gajetastic.cashio.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "cashio";
    
    public static final String TABLE_MAIN = "main";
    public static final String KEY_ID = "row_id";
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String AMOUNT = "amount";
 
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    private static final String CREATE_TABLE =  "CREATE TABLE " + TABLE_MAIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        		+ NAME + " TEXT,"
                + TYPE + " TEXT,"
        		+ AMOUNT + " DECIMAL(10,2));";
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        System.out.println("TABLE CREATED!");
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAIN);
 
        // Create tables again
        onCreate(db);
    }
    

}
