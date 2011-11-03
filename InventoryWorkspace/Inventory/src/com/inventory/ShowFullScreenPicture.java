package com.inventory;

import com.dbHelpers.Product;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class ShowFullScreenPicture extends Activity
{
	private ImageView ivFullScreenPicture;
	
	private Product product;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showfullscreenpicture);
		
		//Get current barcode
		Bundle b = getIntent().getExtras();
		String currentBarcode = b.getString("CurrentProductBarcode"); 
		
		product = Product.getProductFromDB(currentBarcode, getApplicationContext());
		
		getViewComponents();
		updateView();
	}

	private void updateView()
	{
		Bitmap picture = product.getPicture();
		if(picture != null)
		{
			ivFullScreenPicture.setImageBitmap(picture);
		}
	}

	private void getViewComponents()
	{
		this.ivFullScreenPicture = (ImageView)findViewById(R.id.sfspFullScreenPicture);
	}

}
