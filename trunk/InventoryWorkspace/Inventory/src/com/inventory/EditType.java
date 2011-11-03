package com.inventory;

import com.dbHelpers.Type;
import com.inventory.R.id;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class EditType extends Activity
{
	private Type type;
	
	private EditText etName;
	
	private Button etSaveBtn;
	private Button etCancelBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edittype);
		getViewComponents();
		
		type = new Type(getApplicationContext());
		
		setListeners();
	}
	
	private void getViewComponents()
	{
		etName = (EditText)findViewById(R.id.etNameLabel);
		
		etSaveBtn = (Button)findViewById(R.id.etSaveBtn);
		etCancelBtn = (Button)findViewById(R.id.etCancelBtn);
	}
	
	private void setListeners()
	{
		etSaveBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				type.setName(etName.getText().toString());
				type.save();
				
				setResult(RESULT_OK);
				
				EditType.this.finish();
			}
		});
		
		etCancelBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				setResult(RESULT_CANCELED);
				EditType.this.finish();
			}
		});
	}

}
