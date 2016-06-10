package com.agiledge.keocdcomet;

 

 
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;

public class HomeActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
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
		
		/*Button getTrip= (Button)findViewById(R.id.button1);
		getTrip.setText("GetTrip");
		ImageButton logoutButton=(ImageButton)findViewById(R.id.button2);
		//logoutButton.setText("Logout");
		getTrip.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			
				//Intent tripsWithDate= new Intent(HomeActivity.this,Select_tripActivity.class);
				Intent tripsWithDate= new Intent(HomeActivity.this,Select_tripActivity.class);
				HomeActivity.this.startActivity(tripsWithDate);
				
			}
		});
		logoutButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				LoginTask task = new LoginTask(getApplicationContext()	);
				task.setCurrentActivity(HomeActivity.this);
				task.logout();
				
			}
		});
		
		Button invisibleButton=(Button)findViewById(R.id.button3);
		invisibleButton.setVisibility(View.GONE);*/
		
	}
}
