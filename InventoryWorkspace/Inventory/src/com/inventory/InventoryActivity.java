package com.inventory;

import java.io.File;
import java.io.IOException;

import com.dbHelpers.Product;
import com.dbHelpers.ProductDbOpenHelper;
import com.dbHelpers.Tools;

import android.R.anim;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class InventoryActivity extends Activity
{

	private static final int CAMERA_PIC_REQUEST = 1337;
	
	private Button listProductButton;
	private Button scanButton;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//Create pictures folder if doesn't exists
		File pictureDirectory = new File(Product.PICTURES_DIRECTORY);
		if(!pictureDirectory.exists())
		{
			pictureDirectory.mkdirs();
		}
					
		//-------------------------  Scan button section  ----------------------------
		scanButton = (Button) findViewById(R.id.ScanButton);
		scanButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				try
				{
					Intent intent = new Intent(
							"com.google.zxing.client.android.SCAN");
					intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
					startActivityForResult(intent, 0);
				} catch (Exception e)
				{
					//TODO handle exception
					e.printStackTrace();
					
				}
			}
		});
		
		listProductButton = (Button) findViewById(R.id.listProductBtn);
		listProductButton.setOnClickListener(new View.OnClickListener()
		{	
			@Override
			public void onClick(View v)
			{
				Intent intentListProducts = new Intent(getApplicationContext(), ListProduct.class);
				startActivity(intentListProducts);
			}
		});
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		if (requestCode == 0)//Result of barcode scan
		{
			if (resultCode == RESULT_OK)
			{			
				try
				{
					String barcode = intent.getStringExtra("SCAN_RESULT");
					String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
					
					if(Product.getProductFromDB(barcode, getApplicationContext()) != null)
					{
						showProduct(barcode);
					}
					else
					{
						Intent intentEditProduct = new Intent(this, EditProduct.class);
						Bundle b = new Bundle();
						b.putString("CurrentProductBarcode", barcode);
						intentEditProduct.putExtras(b);
						
						startActivityForResult(intentEditProduct, 1);
					}
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
			} else if (resultCode == RESULT_CANCELED)
			{
				// Handle cancel
				// Nothing
			}
		}
		else if(requestCode == 1) //End edit new product
		{
			if(resultCode == RESULT_OK)
			{
				String newBarcode = intent.getStringExtra("NewBarcode");
				showProduct(newBarcode);
			}
		}
	}

	private void showProduct(String barcode)
	{
		Intent intentShowProduct = new Intent(this, ShowProduct.class);
		Bundle b = new Bundle();
		b.putString("CurrentProductBarcode", barcode);
		intentShowProduct.putExtras(b);
		
		startActivity(intentShowProduct);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.inventoryactivitymenu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.iamTypeListMenuItem:
	        showTypeList();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	private void showTypeList()
	{
		Intent intentShowListType = new Intent(this, ListType.class);
		startActivity(intentShowListType);
	}

}