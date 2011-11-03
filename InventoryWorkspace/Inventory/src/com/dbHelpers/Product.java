package com.dbHelpers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

import com.inventory.ListProduct;
import com.inventory.R;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.os.Environment;
import android.text.InputFilter;
import android.widget.TextView;

public class Product 
{
	/*----------------------------------------------------------------------------------------------*\
	|										   	Attributes											 |
	\*----------------------------------------------------------------------------------------------*/

	private String name;
	private String description;
	private String barCode;
	private double price;
	private int typeID;
	private double capacity;
	private int stock;
	private String picturePath;
	
	public final static String PICTURES_DIRECTORY = Environment.getExternalStorageDirectory() + "/Inventory/Pictures/";;
	
	//DB attributes
	private ProductDbOpenHelper dbOpener;
	private SQLiteDatabase db;

	/*----------------------------------------------------------------------------------------------*\
	|									 	  Public methods										 |
	\*----------------------------------------------------------------------------------------------*/

	public long save() throws SQLException
	{	
		SQLiteStatement insertStmt = db.compileStatement("INSERT INTO " + DbTables.PRODUCT.toString() + " (Barcode, Name, Price, TypeID, Description, Capacity, Stock) values ('"
				+ Tools.removeQuotesFromString(barCode) +"','" + Tools.removeQuotesFromString(name) + "'," + price + "," + typeID + ",'" + Tools.removeQuotesFromString(description) + "'," + capacity + "," + stock +")");
		
		delete(false);//delete potential existing item
		return insertStmt.executeInsert();
	}
	
	public int delete(boolean deletePicture)
	{
		int res;
		
		if(deletePicture)
		{
			File f = new File(this.picturePath);
			f.delete();
		}

		//Delete product
		res = db.delete(DbTables.PRODUCT.toString(), "barcode=" + this.barCode, null);
		
		return res;
	}
	
	@Override
	public String toString()
	{
		return this.name;
	}
	
	public Bitmap getPicture()
	{	
		return BitmapFactory.decodeFile(this.picturePath);
	}
	
	public boolean hasPicture()
	{
		return new File(this.picturePath).exists();
	}
	
	/*----------------------------------------------------------------------------------------------*\
	|									 	  Static methods										 |
	\*----------------------------------------------------------------------------------------------*/
	
	public static int deleteAll(Context appContext)
	{
		ProductDbOpenHelper dbOpener = new ProductDbOpenHelper(appContext);
		SQLiteDatabase db = dbOpener.getWritableDatabase();
		
		int nbAffectedRows = db.delete(DbTables.PRODUCT.toString(), null, null);
		
		db.close();
		dbOpener.close();
		
		return nbAffectedRows;
	}

	public static Product getProductFromDB(String barcode, Context appContext) throws SQLException
	{
		Product product = null;
		
		ProductDbOpenHelper dbOpener = new ProductDbOpenHelper(appContext);
		SQLiteDatabase db = dbOpener.getReadableDatabase();
		
		String query = "SELECT * FROM " + DbTables.PRODUCT.toString() + " WHERE Barcode='" + barcode + "'";
		
		Cursor cursor = db.rawQuery(query, null);
		
		if(cursor.moveToFirst())
		{
			product = new Product(cursor.getString(cursor.getColumnIndex("Name")), cursor.getString(cursor.getColumnIndex("Barcode")), appContext);
			
			product.description = cursor.getString(cursor.getColumnIndex("Description"));
			product.price = cursor.getDouble(cursor.getColumnIndex("Price"));
			product.typeID = cursor.getInt(cursor.getColumnIndex("TypeID"));
			product.capacity = cursor.getDouble(cursor.getColumnIndex("Capacity"));
			product.stock = cursor.getInt(cursor.getColumnIndex("Stock"));
		}
		
		db.close();
		dbOpener.close();
				
		return product;
	}
	
	public static ArrayList<Product> getProductsByType(int typeID, Context appContext)
	{
		String query = "SELECT * FROM " + DbTables.PRODUCT.toString() + " WHERE TypeID=" + typeID;
		return Product.getProductsFromDB(query, appContext);
	}
	
	public static ArrayList<Product> getAllProducts(Context appContext)
	{
		String query = "SELECT * FROM " + DbTables.PRODUCT.toString();
		return Product.getProductsFromDB(query, appContext);
	}
	
	private static ArrayList<Product> getProductsFromDB(String query, Context appContext)
	{
		ArrayList<Product> products = new ArrayList<Product>();
		
		ProductDbOpenHelper dbOpener = new ProductDbOpenHelper(appContext);
		SQLiteDatabase db = dbOpener.getReadableDatabase();
		
		Cursor cursor = db.rawQuery(query, null);
		
		if(cursor.moveToFirst())
		{
			do
			{
				Product product = new Product(cursor.getString(cursor.getColumnIndex("Name")), cursor.getString(cursor.getColumnIndex("Barcode")), appContext);
				
				product.description = cursor.getString(cursor.getColumnIndex("Description"));
				product.price = cursor.getDouble(cursor.getColumnIndex("Price"));
				product.typeID = cursor.getInt(cursor.getColumnIndex("TypeID"));
				product.capacity = cursor.getDouble(cursor.getColumnIndex("Capacity"));
				product.stock = cursor.getInt(cursor.getColumnIndex("Stock"));
				
				products.add(product);
			}while(cursor.moveToNext());
		}
		
		return products;
	}
	
	/*----------------------------------------------------------------------------------------------*\
	|									 	  Private methods										 |
	\*----------------------------------------------------------------------------------------------*/


	/*----------------------------------------------------------------------------------------------*\
	|											Constructor											 |
	\*----------------------------------------------------------------------------------------------*/

	public Product(String name, String barCode, Context appContext)
	{
		this(name, null, barCode, 0, 0, 0, 0, appContext);
	}
	
	public Product(String name, String description, String barCode, double price,
			int typeID, double capacity, int stock,
			Context appContext)
	{
		this.name = name;
		this.description = description;
		this.barCode = barCode;
		this.price = price;
		this.typeID = typeID;
		this.capacity = capacity;
		this.stock = stock;
		
		//Open DB
		this.dbOpener = new ProductDbOpenHelper(appContext);		
		this.db = dbOpener.getWritableDatabase();
		
		//update DB
//		dbOpener.onUpgrade(db, 1, 2);
		
		this.picturePath =  PICTURES_DIRECTORY + "product" + barCode + ".png";
	}
	
	@Override
	public void finalize()
	{
		db.close();
		dbOpener.close();
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

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getBarCode()
	{
		return barCode;
	}

	public void setBarCode(String barCode)
	{
		this.barCode = barCode;
	}

	public double getPrice()
	{
		return price;
	}

	public void setPrice(double price)
	{
		this.price = price;
	}

	public int getTypeID()
	{
		return typeID;
	}

	public void setTypeID(int typeID)
	{
		this.typeID = typeID;
	}

	public double getCapacity()
	{
		return capacity;
	}

	public void setCapacity(double capacity)
	{
		this.capacity = capacity;
	}
	
	public int getStock()
	{
		return stock;
	}

	public void setStock(int stock)
	{
		this.stock = stock;
	}
	
	public String getPicturePath()
	{
		return this.picturePath;
	}
}
