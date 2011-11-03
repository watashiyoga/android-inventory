package com.dbHelpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ProductDbOpenHelper extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "GeneriqueCollections.db";
    private static final String CREATE_TABLE_PRODUCTS =
                "CREATE TABLE "+ DbTables.PRODUCT.toString() +" (" +
                "Barcode TEXT primary key, " +
                "Name TEXT, " +
                "Description TEXT, " +
                "Price REAL, " +
                "Capacity REAL, " +
                "Stock INTEGER, " +
                "Picture BLOB, " +
                "TypeID INTEGER" +
                ");";
    private static final String CREATE_TABLE_TYPE =
    			"CREATE TABLE " + DbTables.TYPE.toString() + " (" +
    			"ROWID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Name TEXT, " +
                "Description TEXT" +
                ");";
    private static final String CREATE_TABLE_BEER = 
    			"CREATE TABLE " + DbTables.BEER.toString() + " (" +
    			"ROWID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Capacity REAL, " +
                "Alcohol REAL, " +
                "Origin TEXT" +
                ");";
    
    private static final String DICTIONARY_DROP_DB = 
    			"DROP TABLE IF EXISTS " + DbTables.PRODUCT.toString() + ";" +
    			"DROP TABLE IF EXISTS " + DbTables.TYPE.toString() + ";" +
    			"DROP TABLE IF EXISTS " + DbTables.BEER.toString() + ";";

	public ProductDbOpenHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(CREATE_TABLE_PRODUCTS);
		db.execSQL(CREATE_TABLE_TYPE);
		db.execSQL(CREATE_TABLE_BEER);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL(DICTIONARY_DROP_DB);
		switch(newVersion)
		{
		case 1:
			db.execSQL(CREATE_TABLE_PRODUCTS);
			break;
		default:
			db.execSQL(CREATE_TABLE_PRODUCTS);
		}
	}

}
