package com.agiledge.keocdcomet.gss.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

public class OtherFunctions {

	public static String[] JSONArrayToStringArray(JSONArray array	)
	{
		String[] ret= new String[array.length()];
		try {
			for (int i=0;i<array.length();i++){

				ret[i]= (array.get(i).toString());


			}
			return ret;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}


	}

	public static String monthName(int month)
	{
		String monthNameLabel[]={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
		if(month<=12&&month>=1)
		{
			return monthNameLabel[month-1];
		}else
		{
			return ""+month;
		}
	}

	public static String[] arrayInsert(String a[], String value, int position)
	{
		String[] r=new String[a.length+1];
		for(int i=0,j=0;j<r.length;i++)
		{
			if(position==j)
			{
				r[j++]=value;
				Log.d("***",""+j+": "+ value);
			}
			r[j++]=a[i];
			Log.d("***",""+j+": "+ a[i]);
		}
		return r;

	}

	public  static void alertDailog (Activity activity, String title, String message)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(activity);
		alert.setTitle(title);
		alert.setMessage(message);

		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});
		alert.show();
	}



	public static  String getSharedValue(Context ctx, String label){
		String value="";
		try {
			SharedPreferences pr = ctx.getSharedPreferences("abc",Context.MODE_PRIVATE);
			value=pr.getString(label,value);

		} catch(Exception e) {
			Log.d("***","Label :" + e.getMessage());
		}
		return value;
	}

	public static void insertSharedValue(Context context, String label, String value) {
		SharedPreferences pr = context.getSharedPreferences("abc",Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pr.edit();
		editor.putString(label, value );
		editor.commit();
	}




}
