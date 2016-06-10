package com.agiledge.keocdcomet;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.agiledge.keocdcomet.gss.common.CometSettings;
import com.agiledge.keocdcomet.gss.common.OtherFunctions;
import com.agiledge.keocdcomet.network.DBCheckAndSend;
import com.agiledge.keocdcomet.network.Listener;
import com.agiledge.keocdcomet.network.NetworkUtil;
import com.agiledge.keocdcomet.tasks.CompleteGPSLocatorService;
import com.agiledge.keocdcomet.tasks.GPSLocatorService;
import com.agiledge.keocdcomet.tasks.TimerService;
import com.agiledge.keocdcomet.tasks.TripSheetTask;
import com.agiledge.keocdcomet.view.adapter.TripListAdapter;
import com.nullwire.trace.ExceptionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
public class TripDetailsActivity extends Activity implements Listener   {
	boolean nwAvailable=true;
	TelephonyManager tel;
	String IMEI;
	private Bundle extras;
	boolean hasResponse=true;
	private String timerId;
	long lastFineNuance;
	long currentNaunce;
	private String stopPassword="";
	String[] empName;
	public String []empPasswords;
	public String []escortPasswords;
	public String [] personnalNos;
	private String[] secUserName;
	private String[] getInStatus;
	private String[] showStatus;
	private String[] gender;
	private String forceStopPin;
	/* configurable parameters from server starts */
	private boolean doubleAuthenticationForEmpPickup=true;
	private boolean doubleAuthenticationForEmpDrop=false;
	private boolean doubleAuthenticationForEscortPickup=false;
	private boolean doubleAuthenticationForEscortDrop=false;
	private boolean firstEscortAuthBeforeEmpAuthPickup = false;
	private boolean secondEscortAuthAfterEmpAuthPickup = false;
	private boolean firstEscortAuthBeforeEmpAuthDrop = false;
	private boolean secondEscortAuthAfterEmpAuthDrop = false;

	private NetworkUtil networkUtil;

	private int empAuthCount=0;
	TextView statusText;
	private int selectedIndex;
	private CheckBox selectedCheckBox;
	private  ListView l;
	private  double latitude=0.0;
	private double longitude=0.0;
	private double distanceCovered;
	private long timeElapsed;

	private String tripCode;
	private String tripDate;
	private String tripLog;
	private String tripId;
	private String escort;
	private ImageView selectedImage;
	public TripSheetTask tripTask;
	private  String tripStatus="initial";
	private boolean isSecurity=false;
	/*private GPSLocatorService mService;*/
	/*public Location location;*/
	public android.app.Dialog escortAuthDailog;
	private int count=0;
	private  Intent GPS;
	private Intent gpsIntent;
	private Intent timerIntent;

	public static String INTENT_ACTION="TRIP_DETAILS_ACTION";
	public String StationaryServiceStopAction =CompleteGPSLocatorService.STOP_BROADCAST_ACTION;
	private HashMap <String, ParamObj>requestMap=new HashMap<String, ParamObj>();



	class ParamObj{
		private String type;
		private Object param;
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public Object getParam() {
			return param;
		}
		public void setParam(Object param) {
			this.param = param;
		}
	}

	public String getTripLog() {
		return tripLog;
	}


	public void setTripLog(String tripLog) {
		this.tripLog = tripLog;
	}


	public String getTripTime() {
		return tripTime;
	}


	public void setTripTime(String tripTime) {
		this.tripTime = tripTime;
	}


	private String tripName;
	private String tripTime;

	/*private ServiceConnection mConnection = new ServiceConnection() {

	    public void onServiceConnected(ComponentName className, IBinder binder) {
	    	mService = ((GPSLocatorService.MyBinder) binder).getService();
	      Toast.makeText(TripDetailsActivity.this, "Connected", Toast.LENGTH_SHORT)
	          .show();
	    }

	    public void onServiceDisconnected(ComponentName className) {
	    	mService = null;
	    }
	  };
*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d("***", "Inside TripDetailActivity onCreate");
		/* access service object */
		registerReceiver (responseBcr, new IntentFilter( GPSLocatorService.NORESPONSE_ACTION));
		registerReceiver(bcr, new IntentFilter( GPSLocatorService.BROADCAST_ACTION));
		registerReceiver(timerBcr, new IntentFilter(TimerService.BROADCAST_ACTION));
		IntentFilter intentFilter= new IntentFilter();
		intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE" );
		intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
		registerReceiver(dbbcr, intentFilter);

		IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryLevelReceiver, batteryLevelFilter);
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		tm.listen(signalStrengthListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		gpsIntent=new Intent(INTENT_ACTION);



		//registerReceiver(receiver, filter)

		/*----*/
		statusText = (TextView) findViewById(R.id.statusText);
		try{
			setLastGPSCoordinate();
			String logAddress =  CometSettings. ServerAddress.substring(0,CometSettings.ServerAddress.lastIndexOf("/"));
			logAddress = logAddress.substring(0,logAddress.lastIndexOf("/")) + "/Tetslb/ReceiveLog";

			ExceptionHandler.register(this, logAddress);
			statusText.setText("");



		}catch (Exception e) {

			//	Toast.makeText(this, "erOr :"+e, Toast.LENGTH_LONG).show();
		}
//		turnGPSOn();

		Log.d("***","TripDetailsActivity Action >>" );
		setContentView(R.layout.activity_trip_details);
		Log.d("***","TripDetailsActivity Action " );
		Log.d("***","TripDetailsActivity Action Intent "+ getIntent() );
		Log.d("***","TripDetailsActivity Action  extract"+getIntent().getExtras() );
		Bundle extras = getIntent().getExtras();
		Log.d("***","TripDetailsActivity Action  @" );
		String newString;
		if (savedInstanceState == null) {
			Log.d("***","before taking extras" );
			extras = getIntent().getExtras();
			if(extras == null) {
				Log.d("***"," extras is null" );
				newString= null;
			} else {
				Log.d("***","Some this is in extras" );
				newString= extras.getString("trip");
				stopPassword=extras.getString("stopPassword");
			}
		} else {
			Log.d("***","savedInstanceState is not null Action " );

			newString= (String) savedInstanceState.getSerializable("trip");

		}

		//	JSONDBAdapter adp= new JSONDBAdapter(this);
		//	adp.deleteAll();

		Log.d("***",""+ newString);

		try {
			JSONObject jsonTrip= new JSONObject(newString);
			empName= OtherFunctions. JSONArrayToStringArray( jsonTrip.getJSONArray("empName"));
			personnalNos=OtherFunctions. JSONArrayToStringArray(jsonTrip.getJSONArray("empCode"));
			secUserName=OtherFunctions. JSONArrayToStringArray(jsonTrip.getJSONArray("secUserName"));
			String tripDate=jsonTrip.getString("tripDate");
			String[] apls=OtherFunctions. JSONArrayToStringArray(jsonTrip.getJSONArray("apls"));
			showStatus=OtherFunctions.JSONArrayToStringArray(jsonTrip.getJSONArray("showStatus"));
			getInStatus=OtherFunctions.JSONArrayToStringArray(jsonTrip.getJSONArray("getInStatus"));
			gender=OtherFunctions.JSONArrayToStringArray(jsonTrip.getJSONArray("gender"));
			empPasswords = OtherFunctions.JSONArrayToStringArray(jsonTrip.getJSONArray("employeeKeyPins"));

		   /* getting some config start*/
		/*	try {
				String cometAuthType=jsonTrip.getString("cometAuthType");
				setCometAuthType(cometAuthType);
			}catch(Exception e) {
				;
			}*/
			//getting config

			tripCode=jsonTrip.getString("tripCode");
			tripTime= jsonTrip.getString("tripTime");
			tripLog=  jsonTrip.getString("tripLog");
			tripId=jsonTrip.getString("tripId");
			escort=jsonTrip.getString("escort");

			forceStopPin = jsonTrip.getString("forceStopPin");

			 /* Setting configurable parameters */
			setConfigurableParameters(jsonTrip);

			try{
				distanceCovered=jsonTrip.getDouble("distanceCovered");
			}catch(JSONException je) {
				distanceCovered=0;
			}
			try {
				timeElapsed=jsonTrip.getLong("timeElapsed");
			} catch(JSONException je) {
				timeElapsed=0;
			}

			escort=escort.equalsIgnoreCase("YES")?"Show":"No Show";
			Log.d("**.........","Escor t :" + escort);
			isSecurity=jsonTrip.getString("isSecurity").equalsIgnoreCase("YES")?true:false;

			TripSheetTask tripTask = new TripSheetTask(getApplicationContext(),nwAvailable);
			tripTask.setCurrentActivity(this);
			setTripStatus(jsonTrip.getString("status"));
			Log.d("***",jsonTrip.getString("status"));

			int authConterIndex =0;
			if(isSecurity)
			{
				authConterIndex = 1;
				personnalNos=  OtherFunctions.arrayInsert(personnalNos, "ESCORT", 0);
				empName=  OtherFunctions.arrayInsert(empName, "ESCORT", 0);
				secUserName=  OtherFunctions.arrayInsert(secUserName, " ", 0);
				apls=  OtherFunctions.arrayInsert(apls, " ", 0);
				showStatus= OtherFunctions.arrayInsert(showStatus, escort, 0);
				getInStatus= OtherFunctions.arrayInsert(getInStatus, escort, 0);
				gender= OtherFunctions.arrayInsert(gender, "ESCORT", 0);


			}

			/*
			 * increment count if already logined
			 */

			if(showStatus!=null && showStatus.length>0) {
				for( ;authConterIndex < showStatus.length ; authConterIndex++ ) {
					if(showStatus[authConterIndex].equalsIgnoreCase("show")) {
						empAuthCount++;
					}
					if(getInStatus[authConterIndex].equalsIgnoreCase("OUT")) {
						empAuthCount--;
					}
				}
			}



			Log.d("**.....",jsonTrip.getString("status"));

			TextView tripCodeTxt=(TextView) findViewById(R.id.textViewTripCode);
			TextView tripLogTxt=(TextView) findViewById(R.id.tripLog);
			TextView tripDateTxt=(TextView) findViewById(R.id.txtTripTime);


			tripLogTxt.setText(tripTime+" " +tripLog);


			TextView tripTitle = (TextView)findViewById(R.id.textViewTripTitle);

			tripTitle.setText("Trip Sheet ");
			String datepart[]=tripDate.split("-");
			tripDateTxt.setText(datepart[2]+"/"+OtherFunctions.monthName(Integer.parseInt( datepart[1]))+"/"+datepart[0] );
			//tripTitle.setText("Trip Sheet On "+datepart[2]+"/"+OtherFunctions.monthName(Integer.parseInt( datepart[1]))+"/"+datepart[0] +" "+ tripTime+ " ("+tripLog+")");
			tripCodeTxt.setText( tripCode);





			//tripCodeTxt.setText(tripCode);


			Log.d("***"," before adapter");

			TripListAdapter adapter = new TripListAdapter(getApplicationContext(), empName, apls,showStatus,getInStatus,gender);

			l = (ListView) findViewById( R.id.listViewTripItemList);
			Log.d("***","After list view");
			l.setVisibility(View.VISIBLE);

			l.setAdapter(adapter);


			l.setOnTouchListener(new ListView.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int action = event.getAction();
					switch (action) {
						case MotionEvent.ACTION_DOWN:
							// Disallow ScrollView to intercept touch events.
							v.getParent().requestDisallowInterceptTouchEvent(true);
							break;

						case MotionEvent.ACTION_UP:
							// Allow ScrollView to intercept touch events.
							v.getParent().requestDisallowInterceptTouchEvent(false);
							break;
					}

					// Handle ListView touch events.
					v.onTouchEvent(event);
					return true;
				}
			});
			l.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {
					Log.d("***","Starting of on Item Click at : " +position );
					Log.d("***","security: " +isSecurity);
					// TODO Auto-generated method stub
					//	ImageView img= (ImageView) view.findViewById(R.id.imageViewGetInStatus);
					selectedCheckBox=(CheckBox) view.findViewById(R.id.checkBox1);
					//img.setVisibility(View.VISIBLE);
					selectedIndex=position;
					currentNaunce = Calendar.getInstance().getTimeInMillis();
					ParamObj param = new ParamObj();
					param.setType("listitem");
					param.setParam(position);
					requestMap.put( String.valueOf(currentNaunce) ,param);
					if(tripStatus.equals("started"))
					{
						if(isSecurity&&position==0)
						{

							authenticateEscort( );

						}else {
							if(showStatus[selectedIndex]!=null&&showStatus[selectedIndex].equalsIgnoreCase("Show"))
							{
								// drop authentication for drop trip
								if(tripLog.equalsIgnoreCase("OUT"))
								{

									if( getInStatus[selectedIndex].equalsIgnoreCase("OUT")==false)
									{
										if(doubleAuthenticationForEmpDrop) {

											alertDailog(personnalNos[position],position,selectedCheckBox,"OUT");
										} else {
											alertOk("", "No double authentication please");
										}
									}
								}
								// drop authentication for pick up trip
								else if( tripLog.equalsIgnoreCase("IN")) {

									if( getInStatus[selectedIndex].equalsIgnoreCase("OUT")==false)
									{
										if(doubleAuthenticationForEmpPickup) {

											alertDailog(personnalNos[position], position,selectedCheckBox,"OUT");
										} else {
											alertOk("", "No double authentication please");
										}



									}


								}
							}else {

								if(isSecurity) {
									if( tripLog.equalsIgnoreCase("IN")) {
										if(firstEscortAuthBeforeEmpAuthPickup ) {
											if( showStatus[0].equals( "Show")) {
												alertDailog(personnalNos[position], position,selectedCheckBox,"IN");
											} else {
												alertOk("","Escort is not authenticated.");
											}
										} else {
											alertDailog(personnalNos[position], position,selectedCheckBox,"IN");
										}
									} else if( tripLog.equalsIgnoreCase("OUT")) {
										if(firstEscortAuthBeforeEmpAuthDrop ) {
											if( showStatus[0].equals( "Show")) {
												alertDailog(personnalNos[position], position,selectedCheckBox,"IN");
											} else {
												alertOk("","Escort is not authenticated.");
											}
										} else {
											alertDailog(personnalNos[position], position,selectedCheckBox,"IN");
										}
									}


								} else {
									alertDailog(personnalNos[position], position,selectedCheckBox,"IN");
								}
							}
						}
					}

				}
			});


			ImageButton tripCtrl=(ImageButton) findViewById(R.id.button2);

			if( getTripStatus().equalsIgnoreCase("started"))
			{
				Bundle b = new Bundle();
				b.putString("TRIPID",this.tripId);
				b.putDouble("distanceCovered", distanceCovered);
				b.putLong("timeElapsed", timeElapsed );

				if(GPS!=null)
				{
					try{
						stopService(GPS);
					}catch (Exception e) {
						// TODO: handle exception
						Log.e("***", e.getMessage());
					}
				}
				GPS=new Intent( TripDetailsActivity.this,com.agiledge.keocdcomet.tasks.GPSLocatorService.class);

				GPS.putExtras(b);


				//	 Log.d("***","Timer started");
				startService(GPS);
				stopStationaryLocationService();

				if(timerIntent==null)
				{

					try {
						stopService(new Intent(TripDetailsActivity.this,TimerService.class));
					}catch(Exception e) {
						;
					}
					timerIntent=new Intent(TripDetailsActivity.this,TimerService.class);
					setTimerId(String.valueOf(new Random().nextLong()));
					timerIntent.putExtra("timerId", getTimerId());
					timerIntent.putExtra("timeElapsed", timeElapsed);
					startService(timerIntent);
				}
				//startService(new Intent( TripDetailsActivity.this,com.agiledge.comet.tasks.GPSLocatorService.class));
				//tripCtrl.setBackgroundResource(R.drawable.btn_stop_bg);

				tripCtrl.setImageResource(R.drawable.stop);
			}else
			{

				//tripCtrl.setBackgroundResource(R.drawable.btn_start_bg);
				tripCtrl.setImageResource(R.drawable.start);
			}
			tripCtrl.setOnClickListener(new OnClickListener() {

				public void onClick(View view) {

			/*TripSheetTask tripTask;
			tripTask= new TripSheetTask(getApplicationContext(),nwAvailable);
			tripTask.setCurrentActivity(TripDetailsActivity.this);

			 currentNaunce=Calendar.getInstance().getTimeInMillis();
			*/
					if( getTripStatus().equalsIgnoreCase("started"))
					{
						if(validateStop()) {
							alertStop();
						}


					}else
					{
						alertStart();


					}


				}
			});

			final  ImageButton b1=(ImageButton) findViewById(R.id.button1);


			b1.setOnLongClickListener(new View.OnLongClickListener() {

				@Override
				public boolean onLongClick(View arg0) {
//					// TODO Auto-generated method stub
//
//					boolean flag=false;
//					for(String status:showStatus)
//					{
//						if(status.equalsIgnoreCase("Show")){
//							flag=true;
//							break;
//						}
//					}
//					if(flag){
//						TripSheetTask task= new TripSheetTask(TripDetailsActivity.this.getApplicationContext(),nwAvailable);
//						b1.setSelected(true);
//						task.setCurrentActivity(TripDetailsActivity.this);
//						currentNaunce=Calendar.getInstance().getTimeInMillis();
//						Log.d("***","Naunce in alarm :"+ currentNaunce);
//						task.alarm(String.valueOf( currentNaunce));
//					}else{
//
//						OtherFunctions.alertDailog(TripDetailsActivity.this, "Panic Alarm Denied", "Atleast One Person Should Be In The Vehicle");
//
//					}
					panicalert();
					return true;

				}
			});


			Button nwButton=(Button) findViewById(R.id.nwButton);
			nwButton.setVisibility(View.GONE);
			nwButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					Button b =(Button)arg0;
					if(b.getText().toString().equals("OFF"))
					{
				 /*CheckBox selectedCheckBox =(CheckBox) l.getChildAt(1).findViewById(R.id.checkBox1);
				 selectedCheckBox.setBackgroundColor(Color.GRAY);*/
						b.setText("ON");
			/*	 nwAvailable=false;
				 Log.d("***","Sending nwAvailable status :" + nwAvailable + " to GPSLocatorService");
				 gpsIntent.putExtra("nwAvailable", nwAvailable);
				 sendBroadcast(gpsIntent);*/
					}else
					{
				/* CheckBox selectedCheckBox =(CheckBox) l.getChildAt(1).findViewById(R.id.checkBox1);
				 selectedCheckBox.setBackgroundColor(Color.TRANSPARENT);*/
						b.setText("OFF");
				 /*
				 DBCheckAndSend ds = new DBCheckAndSend(TripDetailsActivity.this);

	        		 ds.check();

				 b.setText("OFF");
				 nwAvailable=true;
				 gpsIntent.putExtra("nwAvailable", nwAvailable);
				 sendBroadcast(gpsIntent);*/
					}


				}
			});


			ImageButton secFlagButton = (ImageButton) findViewById(R.id.imgFlag);
			//secFlagButton.setVisibility(View.VISIBLE);
			if(tripLog.equalsIgnoreCase("IN"))
			{

				//  secFlagButton.setBackgroundResource(R.drawable.stop_trip_security_flag);
				secFlagButton.setImageResource(R.drawable.authentication);
			}else
			{
				//secFlagButton.setBackgroundResource(R.drawable.start_trip_security_flag);
				secFlagButton.setImageResource(R.drawable.authentication);
			}
			secFlagButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Log.d("***", "*** Clicked"+ getTripStatus());



					Log.d("***", "Before dailog");
					authenticateTripAD();




				}
			});


		} catch (Exception e) {
			// TODO Auto-generated catch block
			//	e.printStackTrace();
			Log.d("***","Exception TripDetailActivity "+ e);
			Intent intent = getIntentFromCfg();
					/*new Intent(this,LoginActivity.class);
			try {
			String authType = getCongfigurationType();
			Log.d("***", authType);
			if(authType.equals("username and password type")) {
				intent = new Intent(this,LoginActivity.class);
				Log.d("***", "LoginActivity starts"  );

			} else {
				intent = new Intent(this,LoginOTPOnlyActivity.class);
				Log.d("***", "LoginOTPActivity starts"  );
			}
			}catch(Exception e1) {
				 Log.d("***", "Error :" + e1);
			}
			*/
			startActivity(intent);
			finish();
		}


			/* logout button handler starts */
		ImageButton b2=(ImageButton ) findViewById(R.id.button3);

		//b2.setBackgroundResource(R.drawable.log_out);
		//b2.setBackground(R.drawable.log_out);
		//b2.setBackgroundResource(R.drawable.btn_logout_bg);
		b2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(getTripStatus().equalsIgnoreCase("started")) {

					alertOk("Error", "Log out denied");
				} else {
					alertLogout();
				}


			}
		} );

		  /* logout button handler ends */


	}

	private void setCometAuthType(String cometAuthType) {
		// TODO Auto-generated method stub
		try {
			SharedPreferences pr = this.getSharedPreferences("abc",Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = pr.edit();

			String oldCometAuthType=pr.getString("configurationType", "");

			if(oldCometAuthType != null && oldCometAuthType.equals("") == false) {
				Log.d("***","Old configurationType "+ oldCometAuthType);
				editor.remove("configurationType");

			}
			editor.putString("configurationType", cometAuthType );

			editor.commit();
			Log.d("***", "configurationType is stored. Value : "+ cometAuthType);


		}catch (Exception e) {
			Log.d("***", "unable to delete configurationType");
			Log.d("***","Error :"+ e);

		}
	}


	/* to stopStationaryLocationService */
	private void stopStationaryLocationService() {
		// TODO Auto-generated method stub
		if(isCompleteGPSLocatorServiceRunning())
		{
			Log.d("***", "staitionarylocation is running TripDetailsActivity");
		} else {
			Log.d("***", "staitionarylocation is not running for stoping  at TripDetailsActivity");
		}

		Log.d("***", "stoping staitionarylocation at TripDetailsActivity");
		 /* stopService(stationaryServiceIntent);*/
		Intent stationaryServiceIntent = new Intent( StationaryServiceStopAction);
		stationaryServiceIntent.putExtra("stop", true);
		stationaryServiceIntent.putExtra("action", "stop");

		sendBroadcast(stationaryServiceIntent);

	}




	/* Setting configurable paramters */
	private void setConfigurableParameters(JSONObject jsonTrip) {
		// TODO Auto-generated method stub


		//doubleAuthenticationForEmpPickup = getParam(jsonTrip,"doubleAuthenticationForEmpPickup");
		doubleAuthenticationForEmpDrop = getParam(jsonTrip,"doubleAuthenticationForEmpDrop");
		doubleAuthenticationForEscortPickup = getParam(jsonTrip,"doubleAuthenticationForEscortPickup");
		doubleAuthenticationForEscortDrop = getParam(jsonTrip,"doubleAuthenticationForEscortDrop");
		firstEscortAuthBeforeEmpAuthPickup = getParam(jsonTrip,"firstEscortAuthBeforeEmpAuthPickup");
		secondEscortAuthAfterEmpAuthPickup = getParam(jsonTrip,"secondEscortAuthAfterEmpAuthPickup");
		firstEscortAuthBeforeEmpAuthDrop = getParam(jsonTrip,"firstEscortAuthBeforeEmpAuthDrop");
		secondEscortAuthAfterEmpAuthDrop = getParam(jsonTrip,"secondEscortAuthAfterEmpAuthDrop");


	}


	private void alertDailog(String empCode,  int index, CheckBox checkBox, final String checkType )
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		if(checkType.equalsIgnoreCase("IN")) {
			alert.setTitle("Login");
		} else {
			alert.setTitle("Logout");
		}
		alert.setMessage("Password");
		final int position = index;
		final String empCode1=empCode;
		final   CheckBox chk=checkBox;
		final EditText txtPswd=new EditText(this);
		txtPswd.setTransformationMethod(
				PasswordTransformationMethod.getInstance());
		txtPswd.setRawInputType(Configuration.KEYBOARD_12KEY);
		// txtPswd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)   f;

		// Set an EditText view to get user input
		//final EditText input = new EditText(this);
		alert.setView(txtPswd);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Log.d( "***", "Pin Value : " + txtPswd);
				String value = txtPswd.getText().toString();
				TripSheetTask t= new TripSheetTask(getApplicationContext(),nwAvailable);
				t.setCurrentActivity(TripDetailsActivity.this);

				if(checkType.equals("OUT"))
				{


					if(empPasswords[position].equalsIgnoreCase(value)) {
						employeeGetOut(true);
						t.employeeGetOut(empCode1,value,chk,TripDetailsActivity.this.tripId,latitude,longitude,String.valueOf(currentNaunce) );
					} else {
						employeeGetOut(false);


					}


				}else if(checkType.equals("IN")){

					if(empPasswords[position].equalsIgnoreCase(value)) {
						employeeGetIn(true);
						t.employeeGetIn(empCode1,value,chk,TripDetailsActivity.this.tripId,latitude,longitude,String.valueOf(currentNaunce));
					} else {
						employeeGetIn(false);
					}
				}

				return;
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

				// img1.setVisibility(View.GONE);
				return;
			}
		});
		alert.show();
	}

	private void authenticateTripAD()
	{


		final android.app.Dialog dailog = new Dialog(this);
		dailog.setTitle("Authenticate");

		dailog.setContentView(R.layout.authetication_layout);

		final Spinner l1 = (Spinner)dailog. findViewById(R.id.userNameSpinner);
		final EditText passwordtxt=(EditText)dailog.findViewById(R.id.editText1);
		passwordtxt.setHint("Password");
		passwordtxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, secUserName);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		l1.setAdapter(adapter);

		Button okButton = (Button) dailog.findViewById(R.id.okButton);
		Button cancelButton = (Button) dailog.findViewById(R.id.button2);


		try{

			dailog.show();
			cancelButton .setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dailog.dismiss();

				}
			});

			okButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					dailog.setTitle("dismissing" );
					dailog.dismiss();
					if(l1.getSelectedItem()==null)
					{
						Toast.makeText(getApplicationContext(), "No UserName In The List", Toast.LENGTH_SHORT).show();
					}else
					{


						TripSheetTask t = new TripSheetTask(getApplicationContext(),nwAvailable);
						t.setCurrentActivity(TripDetailsActivity.this);
						selectedIndex=l1.getSelectedItemPosition();
						currentNaunce=Calendar.getInstance().getTimeInMillis();

						t.updateTripTime(tripId, l1.getSelectedItem().toString(), passwordtxt.getText().toString(), "",String.valueOf(currentNaunce));
					}

				}
			});
		}catch(NullPointerException e)
		{
			Log.d("***","Error :"+ e);
		}
	}


	private void authenticateEscort( )
	{


		escortAuthDailog = new Dialog(this);
		escortAuthDailog.setTitle("Escort Authentication");

		escortAuthDailog.setContentView(R.layout.escort_authentication_layout);

		final Spinner l1 = (Spinner)escortAuthDailog. findViewById(R.id.userNameSpinner);
		final EditText passwordtxt=(EditText)escortAuthDailog.findViewById(R.id.escortPassword);
		passwordtxt.setRawInputType(Configuration.KEYBOARD_12KEY);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, secUserName);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		l1.setAdapter(adapter);

		Button okButton = (Button) escortAuthDailog.findViewById(R.id.signInBtn);
		Button cancelButton = (Button) escortAuthDailog.findViewById(R.id.cancelBtn);


		try{
			escortAuthDailog.show();
			cancelButton .setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					escortAuthDailog.dismiss();

				}
			});

			okButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					//	dailog.setTitle("dismissing" );
					//	escortAuthDailog.dismiss();
					if(l1.getSelectedItem()==null)
					{
						Toast.makeText(getApplicationContext(), "No UserName In The List", Toast.LENGTH_SHORT).show();
					}else
					{


						TripSheetTask t = new TripSheetTask(getApplicationContext(),nwAvailable);
						t.setCurrentActivity(TripDetailsActivity.this);
						Log.d("***", showStatus[l1.getSelectedItemPosition()]+ " is show status of escort");
						Log.d("***", getInStatus[l1.getSelectedItemPosition()]+ " is get in status status of escort");
						String username = "" + l1.getSelectedItem();
						if(showStatus[selectedIndex]!=null&&showStatus[selectedIndex].equalsIgnoreCase("Show"))
						{

							if(tripLog.equals("IN")&&doubleAuthenticationForEscortPickup) {
								Log.d("***", " secondEscortAuthAfterEmpAuthPickup " +secondEscortAuthAfterEmpAuthPickup);

								if(secondEscortAuthAfterEmpAuthPickup) {
									Log.d("***", "Authcount :" + empAuthCount);
									if(empAuthCount==0) {
										escortAuthDailog.dismiss();

										if( (username.equals("")||username.equalsIgnoreCase("null") ) ==false && empPasswords[0].equals(passwordtxt.getText())) {
											escortGetOut(true);
											t.authenticateEscort(tripId, username, passwordtxt.getText().toString(),String.valueOf(currentNaunce), latitude, longitude, "OUT" );
										} else {
											alertOk("","Authentication Failed..");
										}

										// auth

									} else {
										alertOk("","Some Employees Forgot to logout");
									}

								} else {
									escortAuthDailog.dismiss();
									t.authenticateEscort(tripId, l1.getSelectedItem().toString(), passwordtxt.getText().toString(),String.valueOf(currentNaunce), latitude, longitude, "OUT" );
									// auth
								}

							}else if( tripLog.equals("IN")&&doubleAuthenticationForEscortPickup ==false) {
								alertOk("No need","No double authentication required.");
							}else if(tripLog.equals("OUT")&&doubleAuthenticationForEscortDrop) {
								Log.d("***", " doubleAuthenticationForEscortDrop " +doubleAuthenticationForEscortDrop);
								if(secondEscortAuthAfterEmpAuthDrop) {
									Log.d("***", " empAuthCount " +empAuthCount);
									if(empAuthCount==0) {
										escortAuthDailog.dismiss();
										escortGetOut(true);
										t.authenticateEscort(tripId, l1.getSelectedItem().toString(), passwordtxt.getText().toString(),String.valueOf(currentNaunce), latitude, longitude, "OUT" );
										// auth
									} else {
										alertOk("","Some Employees Forgot to logout");
									}

								} else {
									escortAuthDailog.dismiss();
									escortGetOut(true);
									t.authenticateEscort(tripId, l1.getSelectedItem().toString(), passwordtxt.getText().toString(),String.valueOf(currentNaunce), latitude, longitude, "OUT" );
									// auth
								}

							}else if(tripLog.equals("OUT")&&doubleAuthenticationForEscortDrop==false) {
								alertOk("No need", "No double authentication required");
							}

						}else
						{
							escortAuthDailog.dismiss();

							Log.d("***" , "actual password :"+ empPasswords[0].trim() + " entered :"  +  passwordtxt.getText().toString().trim());
							Log.d("***" , "(username.equals(\"\")||username.equalsIgnoreCase(\"null\") )  "+ (username.equals("")||username.equalsIgnoreCase("null"))   + " empPasswords[0].equals(passwordtxt.getText() : "+ empPasswords[0].trim().equals(passwordtxt.getText().toString().trim()) + "  "   );

							if( (username.equals("")||username.equalsIgnoreCase("null") ) ==false && empPasswords[0].trim().equals(passwordtxt.getText().toString().trim())) {
								escortGetIn(true);
								t.authenticateEscort(tripId, l1.getSelectedItem().toString(), passwordtxt.getText().toString(),String.valueOf(currentNaunce), latitude, longitude, "IN" );
							} else {
								alertOk("","Authentication Failed..");
							}
						}

					}

				}
			});
		}catch(NullPointerException e)
		{
			Log.d("***","Error :"+ e);
		}
	}

	public String getTripCode() {
		return tripCode;
	}


	public void setTripCode(String tripCode) {
		this.tripCode = tripCode;
	}


	public String getTripDate() {
		return tripDate;
	}


	public void setTripDate(String tripDate) {
		this.tripDate = tripDate;
	}



	public String getTripName() {
		return tripName;
	}


	public void setTripName(String tripName) {
		this.tripName = tripName;
	}



	/*public void callBack(String type,Object sender, JSONObject obj)
	{
		try{
		if(type.equals("escortAutenticationResultError"))
		{
			TextView errorMessage = (TextView) escortAuthDailog.findViewById(R.id.errorMessage);
			errorMessage.setVisibility(View.VISIBLE);
			errorMessage.setText(obj.getString("message"));
		}else if(type.equals("escortAutenticationResultTrue"))
		{
			showStatus[selectedIndex]="Show";
			getInStatus[selectedIndex]="IN";
			selectedCheckBox.setChecked(true);

		   escortAuthDailog.dismiss();
		}
		}catch(Exception e)
		{
			Toast.makeText(this.getApplicationContext(), "Error : "+e, Toast.LENGTH_LONG).show();
		}
	}*/


	@Override
	protected void onDestroy() {

		try{
			Log.d("***", "TripDetailActivity is being destroyed..");
			try {
				unregisterReceiver(responseBcr);
				unregisterReceiver(timerBcr);
				unregisterReceiver(bcr);
				unregisterReceiver(dbbcr);
				unregisterReceiver(batteryLevelReceiver);
			}catch(Exception e) {
				Log.d("***", "error (unregister)  "+ e);
			}
			Log.d("***", "TripDetailActivity is being destroyed..");
			try {
				stopService(new Intent( this,com.agiledge.keocdcomet.tasks.GPSLocatorService.class));

				startService(new Intent(this,CompleteGPSLocatorService.class));
			}catch(Exception e) {
				Log.d("***", "error (stopService GPSLocatorService)  "+ e);
			}
			try {

				stopService(new Intent(TripDetailsActivity.this,TimerService.class));

			}catch(Exception e) {
				Log.d("***", "error (stopService timerService)  "+ e);
			}


		}catch (Exception e) {

			Log.d("***","Error in onDestroy o fTripDetailsActivity :"+e);
		}
		super.onDestroy();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		/*IntentFilter intentFilter= new IntentFilter();
		intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE" );
		intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
		registerReceiver(dbbcr, intentFilter);
		registerReceiver(bcr, new IntentFilter( GPSLocatorService.BROADCAST_ACTION));
		*/Log.d("***","on Pause");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		/*unregisterReceiver(bcr);
		unregisterReceiver(dbbcr);*/
		Log.d("***","on Pause");
	}

	public void triggerLateEvent(JSONObject obj)
	{

		try{
			String action=obj.getString("ACTION");
			if(action.equalsIgnoreCase("startTrip"))
			{
				startTripLate(obj);
			}else if(action.equalsIgnoreCase("stopTrip"))
			{
				stopTripLate(obj);
			}
			else if(action.equalsIgnoreCase("alarm"))
			{
				alarmActivated(obj);

			}else if (action.equalsIgnoreCase("updateTime") )
			{
				updateTimeOutputLate(obj);
			}else if (action.equalsIgnoreCase("vehiclePosition") )
			{
				;
			}

		}catch (Exception e) {
			Toast.makeText(getApplicationContext(),"Error in Trip details activity update() :"+e, Toast.LENGTH_LONG ).show();
		}

	}
	private void updateTimeOutputLate(JSONObject obj)
	{
		try {
			if(obj.getString("result").equalsIgnoreCase("true")==false)
			{
				ImageButton imgb=(ImageButton) findViewById(R.id.imgFlag);
				imgb.setVisibility(View.VISIBLE);
			}
		} catch (JSONException e) {
			Toast.makeText(getApplicationContext(),"Error :"+e, Toast.LENGTH_LONG ).show();

		}
	}

	private void stopTripLate(JSONObject obj) {

		String result;
		try {
			result = obj.getString("result");

			if(result.equalsIgnoreCase("true")==false)
			{
				setTripStatus("started");
				Bundle bundle = new Bundle();
				bundle.putString("TRIPID",this.tripId);

				GPS=new Intent( this,com.agiledge.keocdcomet.tasks.GPSLocatorService.class);
				GPS.putExtras(bundle);
				startService(GPS);
				stopStationaryLocationService();
				try {
					stopService(new Intent(TripDetailsActivity.this,TimerService.class));
				}catch(Exception e) {
					;
				}
				timerIntent = new Intent(this,TimerService.class);
				timerIntent.putExtra("timeElapsed", timeElapsed);
				setTimerId(String.valueOf(new Random().nextLong()));
				timerIntent.putExtra("timerId", getTimerId());
				startService(timerIntent);
				setTripStatus("started");
				ImageButton b= (ImageButton) findViewById(R.id.button2);

				//b.setBackgroundResource(R.drawable.btn_stop_bg);
				b.setImageResource(R.drawable.stop);
			}
		} catch (JSONException e) {
			statusText.setText("Late Response: Error: "+e);
		}

	}


	private void startTripLate(JSONObject obj) {
		String result;
		try {
			result = obj.getString("result");

			if(result.equalsIgnoreCase("true")==false)
			{


				stopService(new Intent( this,com.agiledge.keocdcomet.tasks.GPSLocatorService.class));
				startService(new Intent(this,CompleteGPSLocatorService.class));

				if(timerIntent==null) {
					Log.d("***", "Timer is null while stopping timerIntent");
					try {
						stopService(new Intent(TripDetailsActivity.this,TimerService.class));
					}catch (Exception e) {

						;
					}
				} else {
					stopService(timerIntent);
				}
				setTripStatus("stopped");
				ImageButton b= (ImageButton)  findViewById(R.id.button2);

				//				b.setBackgroundResource(R.drawable.btn_start_bg);
				b.setImageResource(R.drawable.start);
				statusText.setText("Late Response: Trips are not started !");
				lastFineNuance=Calendar.getInstance().getTimeInMillis();

			}
		} catch (JSONException e) {
			statusText.setText("Late Response: Error: "+e);
		}

	}


	public void triggerDBEvent(JSONObject obj)
	{
		TextView statusText = (TextView) findViewById(R.id.statusText);

		statusText.setText("N/w is not available ");
		statusText.setMarqueeRepeatLimit(100);

		triggerEvent(obj);

	}





	public void triggerEvent(JSONObject obj)
	{
		try{
			String action=obj.getString("ACTION");
			if(action.equalsIgnoreCase("startTrip"))
			{
					 startTrip(obj);
			}else if(action.equalsIgnoreCase("stopTrip"))
			{
					stopTrip(obj);
			}else if(action.equalsIgnoreCase("forceStopTrip"))
			{
				stopTrip(obj);
			} else if(action.equalsIgnoreCase("escortGetIn"))
			{
				//	escortGetIn(obj);

			}else if(action.equalsIgnoreCase("escortGetOut"))
			{
				//	escortGetOut(obj);

			} else if(action.equalsIgnoreCase("employeeGetIn"))
			{
				//	escortGetIn(obj);

			}else if(action.equalsIgnoreCase("employeeGetOut"))
			{
				//	escortGetOut(obj);

			}else if(action.equalsIgnoreCase("alarm"))
			{
				alarmActivated(obj);

			}else if (action.equalsIgnoreCase("updateTime") )
			{
				if(obj.getString("result").equalsIgnoreCase("true"))
				{
					Toast.makeText(this,  "Authenticated", Toast.LENGTH_SHORT).show();
					ImageButton imgb = (ImageButton) findViewById(R.id.imgFlag);
					imgb.setVisibility(View.GONE);

				}else
				{
					Toast.makeText(this,  obj.getString("message"), Toast.LENGTH_SHORT).show();
				}

			} else if(obj.getString("result").equalsIgnoreCase("true")) {
				// logoutAction(obj);
			}
			else
			{
				Toast.makeText(getApplicationContext(),"Error : Invalid Action", Toast.LENGTH_SHORT).show();
			}

		}catch (Exception e) {
			Toast.makeText(getApplicationContext(),"Error :"+e, Toast.LENGTH_LONG ).show();
		}
	}

	private void alarmActivated(JSONObject obj)
	{
		try {
			if(obj.getString("result").equalsIgnoreCase("true")==true)
			{
				Toast.makeText(getApplicationContext(),"Panic Alarm Activated !", Toast.LENGTH_LONG ).show();
				System.out.println("panic alarm activated");


			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getApplicationContext(),"Error :"+e, Toast.LENGTH_LONG ).show();
		}
	}

	private void employeeGetOut(boolean result) {
		if(result )
		{
//			  CheckBox selectedCheckBox =(CheckBox) l.getChildAt(selectedIndex).findViewById(R.id.checkBox1);
			Log.d("***", "employeeGetOut : selectedIndex :"+selectedIndex);
			getInStatus[selectedIndex]="OUT";
			empAuthCount --;
			selectedCheckBox.setBackgroundColor(Color.GRAY);
			selectedCheckBox.setVisibility(View.INVISIBLE);
		}else
		{
			alertOk("","Invalid Pin");
		}
	}


	private void escortGetOut(boolean result) {



		if(result )
		{
			Log.d("***", "employeeGetIn : selectedIndex :"+selectedIndex);
			getInStatus[selectedIndex]="OUT";
			Log.d("***", "employeeGetOut selectedIndex :" + selectedIndex);
			selectedCheckBox.setBackgroundColor(Color.GRAY);
		}else
		{

			alertOk("","Authentication failed");
		}

	}




	private void employeeGetIn (boolean result) {

		if(result)
		{
			showStatus[selectedIndex]="Show";
			getInStatus[selectedIndex]="IN";
			Log.d("***", "employeeGetIn selectedIndex :" + selectedIndex);
			selectedCheckBox.setChecked(true);
			empAuthCount ++;
		}else
		{

			alertOk("","Authentication failed");
		}

	}


	private void escortGetIn (boolean result) {



		if(result)
		{
			showStatus[selectedIndex]="Show";
			getInStatus[selectedIndex]="IN";
			Log.d("***", "escortGetIn selectedIndex :" + selectedIndex);
			selectedCheckBox.setChecked(true);
		}else
		{


			alertOk("","Authenitcation Failed");


		}

	}

	private void startUpdate() {
		setTripStatus("started");
		Bundle bundle = new Bundle();
		bundle.putString("TRIPID",this.tripId);

		GPS=new Intent( this,com.agiledge.keocdcomet.tasks.GPSLocatorService.class);
		GPS.putExtras(bundle);
		Log.d("***", "Starting trip service ");
		startService(GPS);
		stopStationaryLocationService();
		try {
			try {
				stopService(new Intent(TripDetailsActivity.this,TimerService.class));
			}catch(Exception e) {
				;
			}
		}catch (Exception e) {

			;
		}
		timerIntent = new Intent(this,TimerService.class);
		timerIntent.putExtra("timeElapsed", timeElapsed);
		setTimerId(String.valueOf(new Random().nextLong()));
		timerIntent.putExtra("timerId", getTimerId());
		startService(timerIntent);

		setTripStatus("started");
		ImageButton b= (ImageButton) findViewById(R.id.button2);

		//b.setBackgroundResource(R.drawable.btn_stop_bg);
		b.setImageResource(R.drawable.stop);
		//	Toast.makeText(getApplicationContext(),"Trip Started", Toast.LENGTH_LONG ).show();
	}

	private void startTrip(JSONObject obj)
	{
		String result;
		try {
			result = obj.getString("result");

			if(result.equalsIgnoreCase("true"))
			{

				startUpdate();
				try{
					alertOk("",obj.getString("message"));

				}catch (Exception e) {
					// TODO: handle exception
					alertOk("","Trip Started");
				}
				//b.setBackgroundResource(R.drawable.btn_stop_bg);
			}else
			{
				//Toast.makeText(getApplicationContext(),"Trip is not started !", Toast.LENGTH_LONG ).show();
				try{

					alertOk("",obj.getString("message"));
				}catch (Exception e) {
					// TODO: handle exception
					alertOk("","Trip Not Started. ");

				}
			}
		} catch (JSONException e) {
			Toast.makeText(getApplicationContext(),"Error :"+e, Toast.LENGTH_LONG ).show();
		}
	}

	private void stopTripUpdate() {
		stopService(new Intent( this,com.agiledge.keocdcomet.tasks.GPSLocatorService.class));
		startService(new Intent(this,CompleteGPSLocatorService.class));

		if(timerIntent==null) {
			Log.d("***", "Timer is null while stopping timerIntent");
			try {
				stopService(new Intent(TripDetailsActivity.this,TimerService.class));
			}catch (Exception e) {

				;
			}
		}
		stopService(timerIntent);

		setTripStatus("stopped");
		ImageButton b= (ImageButton)  findViewById(R.id.button2);

		//				b.setBackgroundResource(R.drawable.btn_start_bg);
		b.setImageResource(R.drawable.start);
	}

	private void stopTrip(JSONObject obj)
	{
		String result;
		try {
			result = obj.getString("result");

			if(result.equalsIgnoreCase("true"))
			{

				stopTripUpdate();
				try{

					alertOk("",obj.getString("message"));
				}catch (Exception e) {
					// TODO: handle exception
					alertOk("","Trip Stopped");
				}

			}else
			{
				try{

					alertStopValidation("",obj.getString("message"));
				}catch (Exception e) {
					// TODO: handle exception
					alertOk("","Trip Not Started.");

				}
			}
		} catch (JSONException e) {
			Toast.makeText(getApplicationContext(),"Error :"+e, Toast.LENGTH_LONG ).show();
		}
	}


	public String getTripStatus() {
		return tripStatus;
	}


	public void setTripStatus(String tripStatus) {
		this.tripStatus = tripStatus;
	}

	/*	private void connectWithGpsService()
        {
            mConnection = new ServiceConnection() {

                public void onServiceConnected(ComponentName className, IBinder binder) {
                    mService = ((GPSLocatorService.MyBinder) binder).getService();
                  Toast.makeText(TripDetailsActivity.this, "Connected", Toast.LENGTH_SHORT)
                      .show();
                }

                public void onServiceDisconnected(ComponentName className) {
                    mService = null;
                }
              };
        }*/
	private BroadcastReceiver timerBcr = new BroadcastReceiver() {
		public void onReceive(android.content.Context context, Intent intent)
		{

			String timerId = intent.getStringExtra("timerId" );
			if( timerId != null && TripDetailsActivity.this.getTimerId()!=null && timerId.equalsIgnoreCase(TripDetailsActivity.this.getTimerId())) {


				Long time= intent.getLongExtra("timeElapsed", 0 );
				Calendar cal = Calendar.getInstance();

				cal.setTimeInMillis(time);
				SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
				//	SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyy hh:mm:ss");
				df.setTimeZone(TimeZone.getTimeZone("GMT"));

				String timeString=df.format( cal.getTime());
				//String timeString = (cal.get(Calendar.HOUR)<10?"0"+cal.get(Calendar.HOUR):cal.get(Calendar.HOUR))+":"+	(	cal.get(Calendar.MINUTE)<10?"0"+cal.get(Calendar.MINUTE):cal.get(Calendar.MINUTE))+":"+ ( cal.get(Calendar.SECOND)<10?"0"+cal.get(Calendar.SECOND):cal.get(Calendar.SECOND));
				TextView txtTime = (TextView) findViewById(R.id.txtTimeElapsed);
				txtTime.setText("Time : "+timeString);
				// Log.d("***", "Time :" + timeString);
			}

		}
	};
	private BroadcastReceiver bcr = new BroadcastReceiver(){
		public void onReceive(android.content.Context context, Intent intent) {
			Log.d("***", "GpsLocation changed recieved by tripDetailActivity");

			latitude=intent.getDoubleExtra("latitude", latitude);
			longitude= intent.getDoubleExtra("longitude",longitude);
			Log.d("***", "Recieving distanceCovered ..");


			distanceCovered=intent.getDoubleExtra("distanceCovered",distanceCovered);
			DecimalFormat df = new DecimalFormat("##0.000");

			timeElapsed=intent.getLongExtra("timeElapsed", timeElapsed);
			TextView txtDistance = (TextView) findViewById(R.id.txtDistanceCovered);
			txtDistance.setText("Distance :"+df.format( distanceCovered));
			//Log.d("***", "Setting distance covered "+distanceCovered+" in status ..");
			//   TextView st = (TextView) findViewById(R.id.statusText);
			//  st.setText("distance :"+ df.format( distanceCovered));
			//  statusText.setText("distance :"+distanceCovered);
			Log.d("***", "Got latitude and longitude"+latitude+":" + longitude+" distance:" + distanceCovered);
		 /* EditText gpsLabel= (EditText) TripDetailsActivity.this.findViewById(R.id.gpsStatusText);
		  count++;
		  gpsLabel.setText("Gps:"+count);
		 */
			Log.d("***", "Got latitude and longitude");


		}
	};

	private BroadcastReceiver dbbcr=new BroadcastReceiver (){

		@Override
		public void onReceive(final Context context, final Intent intent) {
			Log.d("***","In onReceive function");

			try{

				DBCheckAndSend ds = new DBCheckAndSend(TripDetailsActivity.this);
				if(networkUtil==null ) {
					networkUtil = new NetworkUtil();
				}
				if(networkUtil.getConnectivityStatus(TripDetailsActivity.this)!=0 && nwAvailable )
				{
					ds.check();
					if(statusText==null)
						statusText = (TextView) findViewById(R.id.statusText);
					statusText.setText("");
				}

			}catch (Exception e) {
				Log.d("***","Error in onReceive of N/wChnageReceiver");
			}
		}
	};
	@Override
	public void update(boolean result, JSONObject obj) {

		if(result==true&&obj!=null)
		{
			triggerLateEvent(obj);
		}

	}

	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		Log.d("Focus debug", "Focus changed !");

		if(!hasFocus) {
			Log.d("Focus debug", "Lost focus !");

			Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
			sendBroadcast(closeDialog);
		}
	}

	@Override
	public void onBackPressed() {
		// do nothing.
	}

	public void turnGPSOn()
	{
		Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
		intent.putExtra("enabled", true);
		this.sendBroadcast(intent);

		String provider = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		//   Toast.makeText(this, "Provider :"+provider, Toast.LENGTH_SHORT).show();
		if(!provider.contains("gps")){ //if gps is disabled

			final Intent poke = new Intent();
			poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			this.sendBroadcast(poke);


		}
	}

	private void alertLogout() {
		AlertDialog.Builder alert = new AlertDialog.Builder (this);
		alert.setTitle("Log out");
		alert.setMessage("Do you want to log out ?");
		 /*final EditText txtPswd=new EditText(this);
		 txtPswd.setTransformationMethod(
		PasswordTransformationMethod.getInstance());*/
		Log.d("***", "Logout dailog");
		 /*alert.setView(txtPswd);*/
		alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				/*SharedPreferences pr = TripDetailsActivity.this.getSharedPreferences("abc",Context.MODE_PRIVATE);
				String password=pr.getString("password", "123");
				if(txtPswd.getText().toString().equals(password)) {
				*/TripSheetTask task= new TripSheetTask(TripDetailsActivity.this.getApplicationContext(),nwAvailable);

				task.setCurrentActivity(TripDetailsActivity.this);
				Intent intent = getIntentFromCfg();

				task.logout();
				//finish();
				startActivity(intent);
				TripDetailsActivity.this.finish();
				//}
			}
		});

		alert.setNegativeButton("NO",new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				return;
			}
		});
		alert.show();
	}



	 /*
	  * Authenticate driver to start trip
	  */

	private void alertStart() {
		AlertDialog.Builder alert = new AlertDialog.Builder (this);
		alert.setTitle("Start Trip");
		alert.setMessage("Do you want to start trip ?");
	/*	 final EditText txtPswd=new EditText(this);
		 txtPswd.setTransformationMethod(
		PasswordTransformationMethod.getInstance());
	*/	 Log.d("***", "Start Trip Dailog");
		//	 alert.setView(txtPswd);
		alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
			/*	SharedPreferences pr = TripDetailsActivity.this.getSharedPreferences("abc",Context.MODE_PRIVATE);
				String password=pr.getString("password", "123");
				if(txtPswd.getText().toString().equals(password)) {
			*/		TripSheetTask tripTask;
				tripTask= new TripSheetTask(getApplicationContext(),nwAvailable);
				tripTask.setCurrentActivity(TripDetailsActivity.this);

				currentNaunce=Calendar.getInstance().getTimeInMillis();

				startUpdate();
				alertOk("Trip Started", "Trip Started Successfully");
				tripTask.startTrip(tripId,String.valueOf(currentNaunce));
				//finish();

				//TripDetailsActivity.this.finish();
				//		}
			}
		});

		alert.setNegativeButton("NO",new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				return;
			}
		});
		alert.show();
	}


	 /*
	  * authenticate driver while stops
	  */

	private void alertStop() {
		AlertDialog.Builder alert = new AlertDialog.Builder (this);
		alert.setTitle("Stop Trip");
		alert.setMessage("Type Password to stop the trip");
		final EditText txtPswd=new EditText(this);
		txtPswd.setRawInputType(Configuration.KEYBOARD_12KEY);
		txtPswd.setTransformationMethod(
				PasswordTransformationMethod.getInstance());
		Log.d("***", "Stop Trip Dailog");
		alert.setView(txtPswd);
		boolean validatevar=validateStop();
		if(validatevar) {
			alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					SharedPreferences pr = TripDetailsActivity.this.getSharedPreferences("abc", Context.MODE_PRIVATE);
					String password = pr.getString("password", "123");
					if (txtPswd.getText().toString().equals(stopPassword)) {
						TripSheetTask tripTask;
						tripTask = new TripSheetTask(getApplicationContext(), nwAvailable);
						tripTask.setCurrentActivity(TripDetailsActivity.this);

						currentNaunce = Calendar.getInstance().getTimeInMillis();

						stopTripUpdate();
						alertOk("Trip Stopped", "Trip Stopped successfully");
						tripTask.stopTrip(tripId, String.valueOf(currentNaunce));

						finish();

						TripDetailsActivity.this.finish();
					} else {
						Toast.makeText(getApplicationContext(), "Wrong Security Password!", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
		alert.setNegativeButton("NO",new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				return;
			}
		});
		alert.show();
	}


	 /*
	  * authenticate driver while stops forcefully
	  */

	private void alertForceStop() {
		AlertDialog.Builder alert = new AlertDialog.Builder (this);
		alert.setTitle("Force Stop Trip");
		alert.setMessage("Type OTP to stop the trip forcefully");
		final EditText txtPswd=new EditText(this);
		txtPswd.setTransformationMethod(
				PasswordTransformationMethod.getInstance());
		Log.d("***", "Force Stop Trip Dailog");
		alert.setView(txtPswd);
		alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub

				if(txtPswd.getText().toString().equals(forceStopPin)) {
					TripSheetTask tripTask;
					tripTask= new TripSheetTask(getApplicationContext(),nwAvailable);
					tripTask.setCurrentActivity(TripDetailsActivity.this);

					currentNaunce=Calendar.getInstance().getTimeInMillis();



					tripTask.forceStopTrip(tripId, String.valueOf( currentNaunce));
					//finish();

					//	TripDetailsActivity.this.finish();
				}
			}
		});

		alert.setNegativeButton("NO",new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				return;
			}
		});
		alert.show();
	}

	private void alertStopValidation(String title, String message) {
		AlertDialog.Builder alert = new AlertDialog.Builder (this);
		alert.setTitle(title);
		alert.setMessage(message);



		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				return;
			}
		});

		alert.setNegativeButton("Force Stop",new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				// TODO Auto-generated method stub
				alertForceStop();
				dialog.dismiss();
				return;
			}
		});
		alert.show();
	}

	private void alertOk(String title, String message) {

		AlertDialog.Builder alert = new AlertDialog.Builder (TripDetailsActivity.this);
		alert.setTitle(title);
		alert.setMessage(  message);


		alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		alert.show();
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


	private boolean getParam(JSONObject jsonTrip,String param) {
		boolean value=false;
		try {
			value = jsonTrip.getBoolean(param);
		} catch(JSONException je ) {
			;
		} catch(Exception e) {
			;
		}
		return value;
	}


	private boolean validateStop() {
		boolean valid=true;
		if(tripLog.equals("IN")) {
			valid=true;
//			Log.d("***", "IN : doubleAuthenticationForEmpPickup " + doubleAuthenticationForEmpPickup );
//
//			if(doubleAuthenticationForEmpPickup) {
//				if(empAuthCount!=0) {
//					valid=false;
//					alertStopValidation("Stop denied","Some employees forgot to logout");
//
//				}
//
//			}
//			if(doubleAuthenticationForEscortPickup && isSecurity && valid) {
//				Log.d("***", "escortPickUpStatus " + getInStatus[0] );
//				if(getInStatus[0].equals("IN")) {
//					valid=false;
//					alertStopValidation("Stop denied", "Escort forgot to log out");
//
//				}
//			}
		} else
		if(tripLog.equals("OUT")) {
			valid=true;
			Log.d("***", "escortDropStatus " + getInStatus[0] );
			Log.d("***", "OUT : doubleAuthenticationForEmpPickup " + doubleAuthenticationForEmpPickup );
			Log.d("***", "OUT : doubleAuthenticationForEmpDrop " + doubleAuthenticationForEmpDrop);
			if(doubleAuthenticationForEmpPickup) {

				if(empAuthCount!=0) {
					valid=false;
					alertStopValidation("Stop denied", "Some employees forgot to log out");
				}

			}
			if(doubleAuthenticationForEscortDrop && isSecurity && valid) {

				if(getInStatus[0].equals("IN")) {
					valid=false;
					alertStopValidation("Stop denied", "Escort forgot to log out");

				}
			}
		}

		return valid;
	}

	/*
     *
     *    to check completeGPSLocatorService is running
     */
	private boolean isCompleteGPSLocatorServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			Log.d("***", "In MainACt  "+service.service.getClassName());
			Log.d("***", "In MainACt  com.agiledge.comet.tasks.completeGPSLocatorService");
			if ("com.agiledge.comet.tasks.completeGPSLocatorService".equals(service.service.getClassName())) {

				return true;
			}
		}
		return false;
	}


	private String getCongfigurationType(){
		String configurationType="username and password type";
		try {
			SharedPreferences pr = this.getSharedPreferences("abc",Context.MODE_PRIVATE);
			configurationType=pr.getString("configurationType",configurationType);
			Map<String, String> map =  (Map<String, String>) pr.getAll();
			Set<String> keySet = map.keySet();
			for(String strings: keySet ) {
				Log.d("&&&" , " <> " + strings + " " + map.get(strings) );
			}

			if(configurationType!=null && configurationType.equals("")) {
				configurationType="username and password type";
			}
		} catch(Exception e) {
			Log.d("***","getConfigurationType :" + e.getMessage());
		}
		Log.d("***", "getConfigurationType :  " + configurationType);
		return configurationType;
	}



	private void logoutAction(JSONObject obj)
	{
		try {


			if(obj.getString("result").equalsIgnoreCase("true"))
			{
				Log.d("***","logut result :true");
				Intent intent =new Intent(this, LoginActivity.class);

				//this.startActivity(intent);
				Log.d("***", "Login activity is being started ");

				intent = getIntentFromCfg();

				startActivity(intent);
				this.finish();
				Log.d("***", "TripDetailActivity gonna finish");

			}else
			{
				Toast.makeText(this,  "Action failed...", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(this,  " Error in Logout action : "+e, Toast.LENGTH_LONG).show();
		}
	}


	Intent getIntentFromCfg() {


		String authType = getCongfigurationType();
		Intent intent = new Intent(this, LoginActivity.class);
		try{
			Log.d("***", authType);
			if(authType.equals("username and password type")) {
				intent = new Intent(this,LoginOTPOnlyActivity.class);
				Log.d("***", "LoginOTPOnlyActivity");
			} else {
				intent = new Intent(this,LoginOTPOnlyActivity.class);
				Log.d("***", "LoginActivity");
			}
		}catch(Exception e1) {
			;
		}
		return intent;
	}


	public String getTimerId() {
		return timerId;
	}


	public void setTimerId(String timerId) {
		this.timerId = timerId;
	}


	private BroadcastReceiver responseBcr = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			try {
				Log.d("***", "TripDetailsActivity Receives n/w status");
				try {

					Set<String> exK = intent.getExtras().keySet();
					for(String exkey :exK) {
						Log.d("***", " key : " + exkey + " : "  + intent.getExtras().get(exkey));
					}
					hasResponse = Boolean.parseBoolean( intent.getExtras().get("RESPONSE").toString() );
				}catch(Exception e) {
					hasResponse = intent.getBooleanExtra("RESPONSE_1", true);
					Log.d("***", "Error 1 in responseBcr in TripDetailsActivity :  " + e);
				}
				Log.d("***", "now hasresponse : "+ hasResponse);
				nwAvailable=hasResponse;
				if(nwAvailable)
				{

					Log.d("***", "Now N/w is working ..1");
					DBCheckAndSend ds = new DBCheckAndSend(TripDetailsActivity.this);

					if(networkUtil==null ) {
						networkUtil = new NetworkUtil();
					}
					if(networkUtil.getConnectivityStatus(TripDetailsActivity.this)!=0   )
					{
						if(statusText==null)
							statusText = (TextView) findViewById(R.id.statusText);
						statusText.setText("");
						ds.check();
					}


				} else {
					if(statusText==null)
						statusText = (TextView) findViewById(R.id.statusText);
					statusText.setText("No internet connection..");
					Log.d("***", "Now N/w is not working ..");
				}


			}catch (Exception e) {
				//	Toast.makeText(CompleteGPSLocatorService .this ,  "Error In Reader "+e, Toast.LENGTH_LONG).show();
				Log.d("***", "Error in responseBcr in TripDetailsActivity : " + e);
			}

		}

	};



	private void setLastGPSCoordinate() {
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		List<String> providers = lm.getProviders(true);

				        /* Loop over the array backwards, and if you get an accurate location, then break out the loop*/
		Location l = null;

		for (int i=providers.size()-1; i>=0; i--) {
			l = lm.getLastKnownLocation(providers.get(i));
			if (l != null) break;
		}

		double[] gps = new double[2];
		if (l != null) {
			latitude = l.getLatitude();
			longitude = l.getLongitude();
		}

	}

	private void panicalert() {
		final Dialog dialog = new Dialog(TripDetailsActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.panicpopup);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.CENTER;

		dialog.getWindow().setAttributes(lp);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		ImageView yes = (ImageView) dialog.findViewById(R.id.yespanic);
		final  ImageButton b1=(ImageButton) findViewById(R.id.button1);
		Button no = (Button) dialog.findViewById(R.id.nopanic);
		yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {


				dialog.cancel();

				boolean flag = false;
				for (String status : showStatus) {
					if (status.equalsIgnoreCase("Show")) {
						flag = true;
						break;
					}
				}
				if (flag) {
					TripSheetTask task = new TripSheetTask(TripDetailsActivity.this.getApplicationContext(), nwAvailable);
					b1.setSelected(true);
					task.setCurrentActivity(TripDetailsActivity.this);
					currentNaunce = Calendar.getInstance().getTimeInMillis();
					Log.d("***", "Naunce in alarm :" + currentNaunce);
					task.alarm(String.valueOf(currentNaunce));
				} else {

					OtherFunctions.alertDailog(TripDetailsActivity.this, "Panic Alarm Denied", "Atleast One Person Should Be In The Vehicle");
				}
			}

		});

		no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();

			}
		});
		dialog.show();
	}
	private void panicactivated() {
		final Dialog dialog = new Dialog(TripDetailsActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.panicactivatedpopup);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.CENTER;

		dialog.getWindow().setAttributes(lp);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		Button yes = (Button) dialog.findViewById(R.id.hidepanic);
		yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dialog.cancel();

			}
		});

		dialog.show();

	}


}


