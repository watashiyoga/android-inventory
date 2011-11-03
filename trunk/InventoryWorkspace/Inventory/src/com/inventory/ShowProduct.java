package com.inventory;

import com.dbHelpers.Product;
import com.dbHelpers.Type;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowProduct extends Activity
{
	private Product product;
	
	private TextView tvName;
	private TextView tvPrice;
	private TextView tvCapacity;
	private TextView tvStock;
	private TextView tvDescription;
	private TextView tvType;
	private TextView tvBarcode;
	
	private ImageButton btnAdd;
	private ImageButton btnDelete;
	private Button btnEdit;
	private Button btnBack;
	
	private ImageView ivPicture;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showproduct);
		
		getViewComponents();
		
		//Get current barcode
		Bundle b = getIntent().getExtras();
		String currentBarcode = b.getString("CurrentProductBarcode"); 
		
		product = Product.getProductFromDB(currentBarcode, getApplicationContext());
		
		setButtonListeners();
		
		updateView();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == 0 && resultCode == RESULT_OK)//End of edit
		{
			String newBarcode = data.getStringExtra("NewBarcode");
			product = Product.getProductFromDB(newBarcode, getApplicationContext());
			updateView();
		}
	}

	private void setButtonListeners()
	{
		btnAdd.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				product.setStock(product.getStock() + 1);
				product.save();
				updateView();
			}
		});	
		
		btnDelete.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//Check that stock is positive
				if(product.getStock() > 0)
				{
					product.setStock(product.getStock() - 1);
					product.save();
					updateView();
				}
			}
		});	
		
		btnEdit.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intentEditProduct = new Intent(ShowProduct.this, EditProduct.class);
				Bundle b = new Bundle();
				b.putString("CurrentProductBarcode", product.getBarCode());
				intentEditProduct.putExtras(b);
				
				startActivityForResult(intentEditProduct, 0);
			}
		});
		
		btnBack.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				setResult(RESULT_CANCELED);
				ShowProduct.this.finish();
			}
		});
		
		ivPicture.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intentShowFullScreenPicture = new Intent(ShowProduct.this, ShowFullScreenPicture.class);
				Bundle b = new Bundle();
				b.putString("CurrentProductBarcode", product.getBarCode());
				intentShowFullScreenPicture.putExtras(b);
				
				startActivity(intentShowFullScreenPicture);
			}
		});
	}

	private void getViewComponents()
	{
		tvName = (TextView) findViewById(R.id.spName);
		tvPrice = (TextView) findViewById(R.id.spPrice);
		tvCapacity = (TextView) findViewById(R.id.spCapacity);
		tvStock = (TextView) findViewById(R.id.spStock);
		tvDescription = (TextView) findViewById(R.id.spDescription);
		tvType = (TextView) findViewById(R.id.spType);
		tvBarcode = (TextView) findViewById(R.id.spBarcode);
		
		btnAdd = (ImageButton) findViewById(R.id.spBtnAdd);
		btnDelete = (ImageButton) findViewById(R.id.spDeleteBtn);
		btnEdit = (Button) findViewById(R.id.spBtnEdit);
		btnBack = (Button) findViewById(R.id.spBackBtn);
		
		ivPicture = (ImageView)findViewById(R.id.spPicture);
	}

	private void updateView()
	{
		tvName.setText(product.getName());
		tvPrice.setText("" + product.getPrice());
		tvCapacity.setText("" + product.getCapacity());
		tvStock.setText("" + product.getStock());
		tvDescription.setText(product.getDescription());
		tvBarcode.setText(product.getBarCode());
		
		//Getting type
		Type t = Type.getTypeFromDB(product.getTypeID(), getApplicationContext());
		tvType.setText(t.getName());//TODO get real type from DB not only the ID
		
		Bitmap picture = product.getPicture();
		if(picture != null)
		{
			ivPicture.setImageBitmap(picture);
		}
	}
}
