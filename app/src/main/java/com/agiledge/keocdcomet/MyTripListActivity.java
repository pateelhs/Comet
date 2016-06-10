package com.agiledge.keocdcomet;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MyTripListActivity extends ListActivity {

	private View viewContainer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try{    
		    boolean enabled=true;
		    final ConnectivityManager conman = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		    final Class<?> conmanClass = Class.forName(conman.getClass().getName());
		    final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
		    iConnectivityManagerField.setAccessible(true);
		    final Object iConnectivityManager = iConnectivityManagerField.get(conman);
		    final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
		    final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
		    setMobileDataEnabledMethod.setAccessible(true);
		    setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
		    }catch(Exception e)
		    		{e.printStackTrace();
		    	}
		
		 try{
			 Spinner l = (Spinner) findViewById(R.id.spinnerTripLabels);
				//ListView l = (ListView) findViewById(R.id.spinnerTripLabels);
			    String[] values = new String[] { "12:5", "09:00", "14:00",
			        "20:31", "21:30", "10:00", "2", "8:00" };
			    
			    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , values);
			   
			 //   TripListAdapter adapter = new TripListAdapter (this, values);
			       
			    //activity_select_trip
			    //android.R.layout.simple_list_item_1
			    
			   // viewContainer = findViewById(R.id.undobar);
			    Log.d("***","In select_tripActivity");
			    l.setAdapter(adapter);
			     
			    		 }catch (Exception e) {
		 
					 Log.d("***","Exception in In select_tripActivity"+e);
				}
	}
	

	 @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		 Log.d("***"," in onListItemClick ");
		super.onListItemClick(l, v, position, id);
		String item = (String) getListAdapter().getItem(position);
	    Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
	}
}
