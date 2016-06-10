package com.agiledge.keocdcomet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.agiledge.keocdcomet.tasks.TripSheetTask;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class Select_tripActivity extends Activity {

	public Spinner l;
	private EditText dateText;
//	 DatePicker dp;
	 @Override
	 
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_trip);
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
			 Log.d("***", "select Trip onCreate");
			 Log.d("***", "select Trip onCreate indent:"+savedInstanceState);
			 
			 Toast.makeText(this, "after activity_select_trip being set", Toast.LENGTH_LONG);
			 /*String s[]={""};
				l = (Spinner) findViewById(R.id.spinnerTripLabels);
				  ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, s);
				 
				  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				  l.setAdapter(adapter);
				 
				  l.setAdapter(adapter);*/
			 
			  
			 l = (Spinner) findViewById(R.id.spinnerTripLabels);
			 String s[]={""};
			  ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, s);
			  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			 
			 Calendar cal = Calendar.getInstance();
			 cal.setTime(new Date());

				IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			     registerReceiver(batteryLevelReceiver, batteryLevelFilter);
			     TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					tm.listen(signalStrengthListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
				
			 dateText= (EditText) findViewById(R.id.dateText);
			 dateText.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
						showDialog(999);
					
				}
			});

				 
	/*			 
DatePicker.OnDateChangedListener dateSetListener = new DatePicker.OnDateChangedListener() {

    public void onDateChanged(DatePicker view, int year, int monthOfYear,
            int dayOfMonth) {
       
       
    	TripSheetTask task= new TripSheetTask(getApplicationContext());
		 task.setCurrentActivity(Select_tripActivity.this);
		 task.getTripLabels();
		 
		 
         

    }
    
  
};

	         
	    dp = (DatePicker) findViewById(R.id.DatePickerTripDate);
	     dp.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)
	    		 , cal.get(Calendar.DATE), dateSetListener);*/
	       
			 ImageButton showTripTimes=(ImageButton) findViewById(R.id.showtriptimes);
	     showTripTimes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				if(l.getSelectedItem()==null)
				{
					Toast.makeText(getApplicationContext(), "No Trips In The List", Toast.LENGTH_SHORT).show();
				}else
				{
				   TripSheetTask task= new TripSheetTask(Select_tripActivity.this.getApplicationContext(),true);
				   
					 task.setCurrentActivity(Select_tripActivity.this);
					 Log.d("***", "On Click of Download button "+l.getSelectedItem().toString()); 
					
					 String date=dateText.getText().toString();
					 String datepart[]=date.split("/");
					   date =new StringBuffer().append(datepart[2].trim()).append("-").append(datepart[1]
							 ).append("-").append(datepart[0]).toString();
					 task.getTrip(l.getSelectedItem().toString(),date);
					 //Log.d("***_",datepart[2]+"-"+ datepart[1]+"-"+ datepart[0]);
					 Log.d("***_",date);
					 Log.d("***",datepart[2].trim()+"sdfdsf");
					 
				}
				 
			}
		});
	    		 }catch (Exception e) {
 
			 Log.d("***","Exception in In select_tripActivity"+e);
		}
		 
	/*	 ImageButton b1=(ImageButton) findViewById(R.id.button1);
		  //b1.setText("Panic Alarm");
		  b1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				TripSheetTask task= new TripSheetTask(Select_tripActivity.this.getApplicationContext());
				   
				 task.setCurrentActivity(Select_tripActivity.this);
				 
				 task.alarm();
				
			}
		} );*/
		  
		  ImageButton b3=(ImageButton) findViewById(R.id.button2);
		  b3.setVisibility(View.GONE);
		  ImageButton b2=(ImageButton) findViewById(R.id.button3);
		  
		 // b2.setText("Logout");
		//b2.setBackgroundResource(R.drawable.btn_logout_bg);
	  b2.setImageResource(R.drawable.logout_button);
		 
		  b2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				TripSheetTask task= new TripSheetTask(Select_tripActivity.this.getApplicationContext(),true);
				   
				 task.setCurrentActivity(Select_tripActivity.this);
				 
				 task.logout();
				 
				 Select_tripActivity.this.finish();
				
			}
		} );
		  
		  
	  
		 
	}
	 
	 
	 
	 public static class DatePikerFragment   extends DialogFragment
	 implements DatePickerDialog.OnDateSetListener {
	 	public int year;
	 	public int month;
	 	public int day; 
	 	Activity currentActivity;
	 
	 	 
	 @Override
	 public Dialog onCreateDialog(Bundle savedInstanceState) {
	 // Use the current date as the default date in the picker
		 currentActivity=(Activity) savedInstanceState.get("activity");
	 final Calendar c = Calendar.getInstance();
	   year = c.get(Calendar.YEAR);
	   month = c.get(Calendar.MONTH);
	   day = c.get(Calendar.DAY_OF_MONTH);

	 // Create a new instance of DatePickerDialog and return it
	 return new DatePickerDialog(getActivity(), this, year, month, day);
	 }

	 public void onDateSet(DatePicker view, int year, int month, int day) {
	 // Do something with the date chosen by the user
	 	final Calendar c = Calendar.getInstance();
	 	  this.year = year;
	 	  this.month = month;
	 	  this.day = day;
	 }
	 	 

	 }
	 
	 // this method is to prevent from run time exception. 
 public void showDatePickerDialog(View v)
 {
	/* Bundle b = new Bundle();
	
	 
	 
	 DialogFragment newFragment = new DatePikerFragment();
	    newFragment.show(getFragmentManager(), "datePicker");
	    */
	 ;
	    
 }
	  
 @Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 999:
		   // set date picker as current date
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
		   return new DatePickerDialog(this, datePickerListener, 
                      cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener datePickerListener 
             = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			
			dateText.setText(new StringBuilder().append(selectedDay )
			   .append("/").append(selectedMonth+1).append("/").append(selectedYear)
			   .append(" "));
			// set selected date into textview
		/*	tvDisplayDate.setText(new StringBuilder().append(selectedDay + 1)
			   .append("/").append(selectedMonth).append("-").append(selectedYear)
			   .append(" "));
*/
			// set selected date into datepicker also
			//dpResult.init(year, month, day, null);
			 
	    	TripSheetTask task= new TripSheetTask(getApplicationContext(),true);
			 task.setCurrentActivity(Select_tripActivity.this);
			 task.getTripLabels(selectedDay,selectedMonth+1,selectedYear);
			 

		}
	};

	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	 /*
	        Log.d("Focus debug", "Focus changed !");
	 
		if(!hasFocus) {
			Log.d("Focus debug", "Lost focus !");
	 
			Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
			sendBroadcast(closeDialog);
		}*/
		
		//super.onWindowFocusChanged(hasFocus);
		if( !hasFocus )
		{
		ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		am.moveTaskToFront(getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME );
		sendBroadcast( new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS) );
		}
	}
	
	@Override
	public void onBackPressed() {
	    // do nothing.
	}
	

	@Override
	protected void onDestroy() {
		
		try{
			Log.d("***", "Select_tripActivity is being destroyed..");
			 unregisterReceiver(batteryLevelReceiver);
		 
		}catch (Exception e) {

				Log.d("***","Error in onDestroy Select_tripActivity  :"+e);
		}
		super.onDestroy();
		
	}
	

	
	private   BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
     
            int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int level = -1;
            if (currentLevel >= 0 && scale > 0) {
                level = (currentLevel * 100) / scale;
            }
            TextView txtBatteryPercent= (TextView) findViewById(R.id.textViewBatteryLevel);
            txtBatteryPercent.setText("" + level + "%");
        }
    }; 
    
    
    private PhoneStateListener signalStrengthListener = new PhoneStateListener(){
	   	public void onSignalStrengthsChanged(android.telephony.SignalStrength signalStrength) {
	   		
	   		TextView nwLabel = (TextView) findViewById(R.id.nwLabel);
	   		
	   		String signal =" ";
	   		if(signalStrength.getGsmSignalStrength()<=31 && signalStrength.getGsmSignalStrength()>=0) {
	   			
	   			signal = String.valueOf((signalStrength.getGsmSignalStrength() * 100 ) / 31) + "%";
	   			nwLabel.setText(signal);
	   		}
	   	};
   };


    


	 
}
