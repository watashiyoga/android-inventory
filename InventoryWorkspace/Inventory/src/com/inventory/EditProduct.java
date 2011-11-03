package com.inventory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.dbHelpers.Product;
import com.dbHelpers.Type;
import com.inventory.R.id;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Picture;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditProduct extends Activity
{
	protected static final int PICTURE_RESULT = 0;
	protected static final int NEW_TYPE_EDIT = 1;

	private Product product;
	private Bitmap picture = null;
	
	private EditText etName;
	private EditText etDescription;
	private Spinner etType;
	private EditText etPrice;
	private EditText etCapacity;
	private EditText etStock;
	private EditText etBarcode;
	private TextView etPicturePath;
	
	private Button etSaveBtn;
	private Button etCancelBtn;
	private Button etTakePictureBtn;
	private Button etNewTypeBtn;
	
	private List<Type> typeList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editproduct);
		
		getViewComponents();
		
		//Get current barcode
		Bundle b = getIntent().getExtras();
		String currentBarcode = b.getString("CurrentProductBarcode"); 
		
		product = Product.getProductFromDB(currentBarcode, getApplicationContext());
		
		if(product == null)
		{
			product = new Product("", currentBarcode, getApplicationContext());
		}
		
		setListeners();
	    updateView();
	}

	private void setListeners()
	{
		etSaveBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				product.setName(etName.getText().toString());
				product.setDescription(etDescription.getText().toString());
				product.setTypeID(((Type)etType.getSelectedItem()).getId());
				product.setPrice(Double.parseDouble(etPrice.getText().toString()));
				product.setCapacity(Double.parseDouble(etCapacity.getText().toString()));
				product.setStock(Integer.parseInt(etStock.getText().toString()));
				product.setBarCode(etBarcode.getText().toString());
				product.save();
				
				setResult(RESULT_OK, getIntent().putExtra("NewBarcode", product.getBarCode()));
				
				EditProduct.this.finish();
			}
		});
		
		etCancelBtn.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				setResult(RESULT_CANCELED);
				EditProduct.this.finish();
			}
		});
		
		etTakePictureBtn.setOnClickListener(new View.OnClickListener()
		{	
			@Override
			public void onClick(View v)
			{
				Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);   
		        startActivityForResult(camera, PICTURE_RESULT);
			}
		});
		
		etNewTypeBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent newEditTypeIntent = new Intent(getApplicationContext(), EditType.class);
				startActivityForResult(newEditTypeIntent, NEW_TYPE_EDIT);
			}
		});
	}

	private void getViewComponents()
	{
		etName = (EditText)findViewById(R.id.epName);
		etDescription = (EditText)findViewById(R.id.epDescription);
		etType = (Spinner)findViewById(R.id.epTypeSpinner);
		etPrice = (EditText)findViewById(R.id.epPrice);
		etCapacity = (EditText)findViewById(R.id.epCapacity);
		etStock = (EditText)findViewById(R.id.epStock);
		etBarcode = (EditText)findViewById(R.id.epBarcode);
		etPicturePath = (TextView)findViewById(R.id.epPictureLink);
		
		etSaveBtn = (Button)findViewById(R.id.epSaveBtn);
		etCancelBtn = (Button)findViewById(R.id.epCancelBtn);
		etTakePictureBtn = (Button)findViewById(R.id.epTakePictureButton);
		etNewTypeBtn = (Button)findViewById(id.epNewTypeBtn);
	}

	private void updateView()
	{
		etName.setText(product.getName());
		etDescription.setText(product.getDescription());
		etPrice.setText(""+product.getPrice());
		etCapacity.setText(""+product.getCapacity());
		etStock.setText(""+product.getStock());
		etBarcode.setText(product.getBarCode());
		
		upatePicturePath();
		
		//Load category to spinner
		updateTypeList();
		
	}

	private void updateTypeList()
	{
		typeList = Type.getTypesFromDB(getApplicationContext());
		ArrayAdapter<Type> spinnerAdapter = new ArrayAdapter<Type>(getApplicationContext(), android.R.layout.simple_spinner_item, typeList);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		etType.setAdapter(spinnerAdapter);
		
		//Set selecte type
		int itemPos = typeList.indexOf(Type.getTypeFromDB(product.getTypeID(), getApplicationContext()));
		etType.setSelection(itemPos);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == PICTURE_RESULT && resultCode == Activity.RESULT_OK)
		{
            OutputStream fOut;
            
            Bundle b = data.getExtras();
            this.picture = (Bitmap) b.get("data");
            
			try
			{
                File file = new File(this.product.getPicturePath());
                
				fOut = new FileOutputStream(file);
				picture.compress(Bitmap.CompressFormat.PNG, 100, fOut); //bm is the bitmap object   
        		fOut.flush();
        		fOut.close();
			} catch (FileNotFoundException e)
			{
				Toast.makeText(getApplicationContext(), e.toString(), 1).show();
				e.printStackTrace();
			} catch (IOException e)
			{
				Toast.makeText(getApplicationContext(), e.toString(), 1).show();
				e.printStackTrace();
			} 
			
			upatePicturePath();
		}
		else if(requestCode == NEW_TYPE_EDIT && resultCode == Activity.RESULT_OK)
		{
			updateTypeList();
		}
	}

	private void upatePicturePath()
	{
		if(this.product.hasPicture())
		{
			File f = new File(this.product.getPicturePath());
			etPicturePath.setText(f.getName());
		}
	}
}
