package com.inventory;

import java.util.ArrayList;

import com.dbHelpers.Product;
import com.dbHelpers.Type;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class ListProduct extends Activity
{
	private ListView productsListView;
	private Spinner typeSpinner;
	
	private ArrayList<Product> productList;
	
	private final int INTENT_RESULT_EDIT = 0;
	private final int INTENT_RESULT_SHOW = 1; //could result as an edit => must update view afterward
	private ArrayList<Type> typeList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listproduct);
		
		getViewComponents();
		registerForContextMenu(productsListView);
		
		addListeners();
		updateTypeList();
		updateView();
	}

	private void updateTypeList()
	{
		typeList = Type.getTypesFromDB(getApplicationContext());
		ArrayAdapter<Type> spinnerAdapter = new ArrayAdapter<Type>(getApplicationContext(), android.R.layout.simple_spinner_item, typeList);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeSpinner.setAdapter(spinnerAdapter);
	}

	private void addListeners()
	{
		productsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int itemIndex,
					long arg3)
			{
				Product p = (Product)productsListView.getItemAtPosition(itemIndex);

				showProduct(p);
			}               
		  });
		
		typeSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int selectedItem, long arg3)
			{
				updateView();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{
//				doNothing
			}
		});
	}

	private void getViewComponents()
	{
		this.productsListView = (ListView)findViewById(R.id.lpListViewProducts);
		this.typeSpinner = (Spinner)findViewById(R.id.lpTypeSpinner);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		Product selectedProduct = (Product)productsListView.getAdapter().getItem(info.position);
		
		switch(item.getItemId())
		{
			case R.string.lpContextMenuShowProduct:
				showProduct(selectedProduct);
				break;
			case R.string.lpContextMenuEditProduct:
				editProduct(selectedProduct);
				break;
			case R.string.lpContextMenuDeleteFromDB:
				selectedProduct.delete(true);
				break;
		}
		
		updateView();
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo)
	{
		if(v.getId() == R.id.lpListViewProducts)
		{
			AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
			
			Product p = (Product)productsListView.getAdapter().getItem(info.position);
			menu.setHeaderTitle(p.getName());
			menu.add(0, R.string.lpContextMenuShowProduct, 0, R.string.lpContextMenuShowProduct);
			menu.add(0, R.string.lpContextMenuEditProduct, 1, R.string.lpContextMenuEditProduct);
			menu.add(0, R.string.lpContextMenuDeleteFromDB, 2, R.string.lpContextMenuDeleteFromDB);
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == INTENT_RESULT_EDIT && resultCode == RESULT_OK)//End of edit
		{
			updateView();
		}
		if(requestCode == INTENT_RESULT_SHOW)//could result as an edit => must update view afterward
		{
			updateView();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.productlistmenu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.plmFilter:
	        
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private void showProduct(Product p)
	{
		Intent intentShowProduct = new Intent(getApplicationContext(), ShowProduct.class);
		Bundle b = new Bundle();
		b.putString("CurrentProductBarcode", p.getBarCode());
		intentShowProduct.putExtras(b);
		
		startActivityForResult(intentShowProduct, INTENT_RESULT_SHOW);
	} 
	
	private void editProduct(Product selectedProduct)
	{
		Intent intentEditProduct = new Intent(getApplicationContext(), EditProduct.class);
		Bundle b = new Bundle();
		b.putString("CurrentProductBarcode", selectedProduct.getBarCode());
		intentEditProduct.putExtras(b);
		
		startActivityForResult(intentEditProduct, INTENT_RESULT_EDIT);
	}
	
	private void updateView()
	{
		Type selectedType = (Type)(typeSpinner.getSelectedItem());
		if(selectedType != null)
		{
			int typeID = selectedType.getId();
			//Load productList by selectedTypeID
			productList = Product.getProductsByType(typeID, getApplicationContext());
		}
		else
		{
			productList = Product.getAllProducts(getApplicationContext());
		}
		
		ArrayAdapter<Product> arrayAdapter = new ArrayAdapter<Product>(getApplicationContext(), android.R.layout.simple_list_item_1, productList);
		productsListView.setAdapter(arrayAdapter);
	}

}
