package com.dbHelpers;

public class Tools
{
	private Tools()
	{
		
	}
	
	public static String removeQuotesFromString(String rawString)
	{
		return rawString.replace("'", "''");
	}
}
