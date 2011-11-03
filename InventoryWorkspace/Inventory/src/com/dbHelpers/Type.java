package com.dbHelpers;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class Type
{	
	/*----------------------------------------------------------------------------------------------*\
	|									 	  Static methods										 |
	\*----------------------------------------------------------------------------------------------*/
	
	public static int deleteAll(Context appContext)
	{
		ProductDbOpenHelper dbOpener = new ProductDbOpenHelper(appContext);
		SQLiteDatabase db = dbOpener.getWritableDatabase();
		
		int nbAffectedRows = db.delete(DbTables.TYPE.toString(), null, null);
		
		db.close();
		dbOpener.close();
		
		return nbAffectedRows;
	}

	public static Type getTypeFromDB(int id, Context appContext) throws SQLException
	{
		Type type = null;
		
		ProductDbOpenHelper dbOpener = new ProductDbOpenHelper(appContext);
		SQLiteDatabase db = dbOpener.getReadableDatabase();
		
		String query = "SELECT ROWID, Name FROM " + DbTables.TYPE.toString() + " WHERE ROWID='" + id + "'";
		
		Cursor cursor = db.rawQuery(query, null);
		
		if(cursor.moveToFirst())
		{
			type = new Type(appContext);
			
			type.id = cursor.getInt(cursor.getColumnIndex("ROWID"));
			type.name = cursor.getString(cursor.getColumnIndex("Name"));
		}
		
		db.close();
		dbOpener.close();
				
		return type;
	}
	
	public static ArrayList<Type> getTypesFromDB(Context appContext)
	{
		ArrayList<Type> types = new ArrayList<Type>();
		
		ProductDbOpenHelper dbOpener = new ProductDbOpenHelper(appContext);
		SQLiteDatabase db = dbOpener.getReadableDatabase();
		
		String query = "SELECT ROWID, Name FROM " + DbTables.TYPE.toString();
		
		Cursor cursor = db.rawQuery(query, null);
		
		if(cursor.moveToFirst())
		{
			do
			{
				Type type = new Type(appContext);
				
				type.id = cursor.getInt(cursor.getColumnIndex("ROWID"));
				type.name = cursor.getString(cursor.getColumnIndex("Name"));
				
				types.add(type);
			}while(cursor.moveToNext());
		}
		
		return types;
	}
	
	/*----------------------------------------------------------------------------------------------*\
	|									 	  Public methods										 |
	\*----------------------------------------------------------------------------------------------*/
	
	public long save() throws SQLException
	{	
		if(id >= 0)//existing
		{
			SQLiteStatement insertStmt = db.compileStatement("INSERT INTO " + DbTables.TYPE.toString() + " (ID, Name) values ("
					+ this.id +",'" + Tools.removeQuotesFromString(name) + "')");
			
			delete();//delete potential existing item
			return insertStmt.executeInsert();
		}
		else//new item
		{
			SQLiteStatement insertStmt = db.compileStatement("INSERT INTO " + DbTables.TYPE.toString() + " (Name) values ('" + Tools.removeQuotesFromString(name) + "')");
			
			return insertStmt.executeInsert();
		}
	}
	
	public int delete()
	{
		int res;

		//Delete product
		res = db.delete(DbTables.TYPE.toString(), "ROWID=" + this.id, null);
		
		return res;
	}
	
	@Override
	public String toString()
	{
		return this.name;
	}
	
	@Override
	public boolean equals(Object o)
	{
		Type toCompare = (Type)o;
		
		if(toCompare == null)
			return false;
		
		boolean res = true;
		
		res = res && (this.id == toCompare.id);
		res = res && (this.name.compareTo(toCompare.name) == 0);
		
		return res;
	}
	
	/*----------------------------------------------------------------------------------------------*\
	|											Constructor											 |
	\*----------------------------------------------------------------------------------------------*/

	public Type(int id, String text, Context appContext)
	{
		this.id =id;
		this.name = text;
		
		//Open DB
		this.dbOpener = new ProductDbOpenHelper(appContext);		
		this.db = dbOpener.getWritableDatabase();
	}
	
	public Type(Context appContext)
	{
		this(-1, null, appContext);
	}
	
	/*----------------------------------------------------------------------------------------------*\
	|								 		Getters and Setters										 |
	\*----------------------------------------------------------------------------------------------*/

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}
	
	/*----------------------------------------------------------------------------------------------*\
	|										   	Attributes											 |
	\*----------------------------------------------------------------------------------------------*/

	private int id;
	private String name;
	
	private ProductDbOpenHelper dbOpener;
	private SQLiteDatabase db;
	
	
}
