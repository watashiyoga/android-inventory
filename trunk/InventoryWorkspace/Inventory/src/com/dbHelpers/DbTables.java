package com.dbHelpers;

public enum DbTables
{
	PRODUCT
	{
		public String toString()
		{
			return "Product";
		}
	},
	
	TYPE
	{
		public String toString()
		{
			return "Categroy";
		}
	},
	
	BEER
	{
		public String toString()
		{
			return "Beer";
		}
	}
}
