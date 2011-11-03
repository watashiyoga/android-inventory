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

public class ListType extends Activity
{
	private ListView typeListView;
	
	private ArrayList<Type> typeList;
	
	private final int INTENT_RESULT_EDIT = 0;
	private final int INTENT_RESULT_SHOW = 1; //could result as an edit => must update view afterward
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listtype);
		
		getViewComponents();
		registerForContextMenu(typeListView);
		
		addListeners();
		updateView();
	}


	private void addListeners()
	{
		typeListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int itemIndex,
					long arg3)
			{
				Type t = (Type)typeListView.getItemAtPosition(itemIndex);
//				showType(t);
			}               
		  });
	}

	private void getViewComponents()
	{
		this.typeListView = (ListView)findViewById(R.id.ltTypeList);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		Type selectedType = (Type)typeListView.getAdapter().getItem(info.position);
		
		switch(item.getItemId())
		{
			case R.string.lpContextMenuShowProduct:
//				showProduct(selectedProduct);
				break;
			case R.string.lpContextMenuEditProduct:
//				editProduct(selectedProduct);
				break;
			case R.string.lpContextMenuDeleteFromDB:
				selectedType.delete();
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
			
			Product p = (Product)typeListView.getAdapter().getItem(info.position);
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
		typeList = Type.getTypesFromDB(getApplicationContext());
		
		ArrayAdapter<Type> arrayAdapter = new ArrayAdapter<Type>(getApplicationContext(), android.R.layout.simple_list_item_1, typeList);
		typeListView.setAdapter(arrayAdapter);
	}

}
