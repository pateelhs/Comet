package com.agiledge.keocdcomet;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.BatteryManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.agiledge.keocdcomet.dto.LoginDto;
import com.agiledge.keocdcomet.network.DBCheckAndSend;
import com.agiledge.keocdcomet.network.Listener;
import com.agiledge.keocdcomet.network.NetworkUtil;
import com.agiledge.keocdcomet.tasks.GPSLocatorService;

import org.json.JSONObject;

public class LoginOTPOnlyActivity extends Activity implements Listener {

	private int exitCount=0;
	private Intent myIntent =null; 
	private boolean nwAvailable=true;
	private NetworkUtil networkUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_otp_only);
		try{    
		    boolean enabled=true;
//		    final ConnectivityManager conman = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//		    final Class<?> conmanClass = Class.forName(conman.getClass().getName());
//		    final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
//		    iConnectivityManagerField.setAccessible(true);
//		    final Object iConnectivityManager = iConnectivityManagerField.get(conman);
//		    final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
//		    final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
//		    setMobileDataEnabledMethod.setAccessible(true);
//		    setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
		    }catch(Exception e)
		    		{e.printStackTrace();
		    	} 
		 ProgressBar spinner = (ProgressBar)findViewById(R.id.progressBar1);
	      spinner.setVisibility(View.GONE);
	   
	try{
		Log.d("***", "step 1");
		myIntent=getIntent();
		    boolean homeFlag=false;
		    boolean defaultFlag=false;
		    Log.d("***", "step 2");
		   /* Set<String> cats=myIntent.getCategories();
		    Log.d("***", "step 3");
		    for(String s:cats) {
		    	if(myIntent.hasCategory(Intent.CATEGORY_HOME)) {
		    		Log.d("***", "step 4 CATEGORY_HOME");
		    		homeFlag=true;
		    	} else if(myIntent.hasCategory(Intent.CATEGORY_DEFAULT)) {
		    		Log.d("***", "step 5 CATEGORY_DEFAULT");
		    		defaultFlag=true;
		    	}
		    		
		    }*/
		    Log.d("***", "step 6");
		    if(homeFlag==false) {
		    	
		    	myIntent.addCategory(Intent.CATEGORY_HOME);
		    	Log.d("***", "step 7");
		    } 
		    Log.d("***", "step 8");
		    if (defaultFlag==false) {
	        
	        myIntent.addCategory(Intent.CATEGORY_DEFAULT);
	        Log.d("***", "step 9");
		    }
		    TextView txtVersion = (TextView) findViewById(R.id.textVersionNo);
		    txtVersion.setText( "  v:" +String.valueOf( getVersionCode(this) ) ); 
		    Log.d("***", "step 10");
		    IntentFilter intentFilter= new IntentFilter();
			intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE" );
			intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
			registerReceiver(dbbcr, intentFilter);
		     
		    registerReceiver(responseBcr, new IntentFilter(GPSLocatorService.NORESPONSE_ACTION));
		    IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		     registerReceiver(batteryLevelReceiver, batteryLevelFilter);
			TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			tm.listen(signalStrengthListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
			
		/*/	if(!isCompleteGPSLocatorServiceRunning()) {
				Intent intent=null;
				intent= new Intent(this,CompleteGPSLocatorService.class);
				this.startService(intent);
			}  /*/
			
			/*
			 *  download basic settings.
			 */
		/*/	LoginTask login = new LoginTask(this);
			login.downloadBasicSettings(); /*/
			
			EditText passwordTxt = (EditText) findViewById(R.id.password);
			passwordTxt.setRawInputType(Configuration.KEYBOARD_12KEY);
		
			
	} catch (Exception e) {
		Log.d("***", "Error in LoginActivity :  "+e);
	}


		
		
		//Log.d("***","Starting service location :"+GPSLocatorService.getPhoneLocation().getLatitude()+" , " + GPSLocatorService.getPhoneLocation().getLongitude() );

		Button login= (Button) findViewById( R.id.loginButton);
		login.setOnClickListener(new OnClickListener() {
			LoginTaskOtpOnly login = new LoginTaskOtpOnly(getApplicationContext());
			
			
			
			@Override
			public void onClick(View arg0) {
			
				LoginDto dto = new LoginDto();
				
				
				EditText pText=(EditText) findViewById(R.id.password);
				 
				dto.setPassword(pText.getText().toString());
				
				Log.d("COMet","In on click");
			//	LoginTaskOtpOnly login = new LoginTaskOtpOnly(getApplicationContext());
				
				
//				login.setCurrentActivity(LoginOTPOnlyActivity.this);
//				dto.setCurrentActivity(LoginOTPOnlyActivity.this);
//				login.setDto(dto);
				Log.d("***","In before execute");
				
				if(validate(dto))
				{
					Intent in=new Intent(LoginOTPOnlyActivity.this,TripDownloadActivity.class);
					in.putExtra("password",pText.getText().toString());
					startActivity(in);
					//	finish();
				//	login.login();
					
				}
				
			}
		});
		
		Button exit= (Button) findViewById( R.id.resetButton);
		exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
								
				/*Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCatOegory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);*/
				 
				EditText password= (EditText)findViewById(R.id.password);
				 
				password.setText("");
			}
		});
		
		TextView textViewOTP= (TextView) findViewById(R.id.password);
		textViewOTP.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				exitCount=exitCount+1;
				Log.d("***", "Counting "+ exitCount);
				if(exitCount>4) {
					exitCount=0;
					 
					  
					alertExit();
					  
				}
				
			}   
		});

		
	
		
	} 


 /*/	private void setCometAuthType(String cometAuthType) {
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
			
			Log.d("***", "again configurationType : " + getCongfigurationType());
		}catch (Exception e) {
			Log.d("***", "unable to delete configurationType");
			Log.d("***","Error :"+ e);
			 
		}
	}


	private String getCongfigurationType(){
		String configurationType="username and password type";
		try {
			SharedPreferences pr = this.getSharedPreferences("abc",Context.MODE_PRIVATE);
			configurationType=pr.getString("configurationType",configurationType);
			
			if(configurationType!=null && configurationType.equals("")) {
				configurationType="username and password type";
			}
		} catch(Exception e) {
			Log.d("***","getConfigurationType :" + e.getMessage());
		}
		return configurationType;
	} /*/

	public static void simulateKey(final int KeyCode) {

		new Thread() {
			@Override
			public void run() {
				try {
					Instrumentation inst = new Instrumentation();
					inst.sendKeyDownUpSync(KeyCode);
				} catch (Exception e) {
					Log.e("Exception when send", e.toString());
				}
			}

		}.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	
	private boolean validate(LoginDto dto)
	{
		boolean flag=true;
		 TextView errorText= (TextView)  findViewById(R.id.errorLabel);
		 errorText.setText("");
		 if(dto.getPassword()==null||dto.getPassword().equals(""))
		{
			errorText.setText("Please Enter Password!");
			flag=false;
		}
		
		return flag;
	}
	
	@Override
	public void onBackPressed() {
	    // do nothing.
	}
	
	 private void alertExit() {
		 AlertDialog.Builder alert = new AlertDialog.Builder (this);
		 alert.setTitle("Exit");
		 alert.setMessage("Do you want to exit");
		 Log.d("***", "exit dailog");
		 alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				myIntent.removeCategory(Intent.CATEGORY_DEFAULT);
				Toast.makeText(LoginOTPOnlyActivity.this,"Exiting", Toast.LENGTH_SHORT);
				startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
				LoginOTPOnlyActivity.this.finish();
				
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
	 
	 
//	 public void onWindowFocusChanged(boolean hasFocus) {
//			super.onWindowFocusChanged(hasFocus);
//		 /*
//		        Log.d("Focus debug", "Focus changed !");
//
//			if(!hasFocus) {
//				Log.d("Focus debug", "Lost focus !");
//
//				Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//				sendBroadcast(closeDialog);
//			}*/
//
//			//super.onWindowFocusChanged(hasFocus);
//			if( !hasFocus )
//			{
//	/*		ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
//			try {
//				;
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				Log.d("***", ""+e);
//			}*/
//		//	am.
//				//Service  ac=(Service )  getSystemService(Context.ACTIVITY_SERVICE);
//		//ac.stopSelf();
//		//ac.finish();
//			/*	Intent actvM=new Intent(Context.ACTIVITY_SERVICE);
//				startService(actvM);
//				stopService(actvM);*/
//
//			//am.moveTaskToFront(getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME );
//			sendBroadcast( new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS) );
//			}
//		}
//

		@Override
		protected void onDestroy() {
			
			try{
				unregisterReceiver(dbbcr);
				Log.d("***", "Select_tripActivity is being destroyed..");
				unregisterReceiver(responseBcr);
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
	   
	   
	   
	/*/	private boolean isCompleteGPSLocatorServiceRunning() {
		    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
		    	Log.d("***", "In MainACt  "+service.service.getClassName());
		        if ("com.agiledge.gsscomet.tasks.completeGPSLocatorService".equals(service.service.getClassName())) {
					
		            return true;
		        }
		    }
		    return false;
		}  /*/
		
		
		public int getVersionCode(Context context) {
			   PackageManager pm = context.getPackageManager();
			   try {
			      PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			      return pi.versionCode;
			   } catch (NameNotFoundException ex) {}
			   return 0;
			}
		
		@Override
		public void update(boolean result, JSONObject obj) {
			// TODO Auto-generated method stub
			try {
				if(result) {
					
				
					/*if(obj.getString("ACTION" ).equalsIgnoreCase("downloadSettings")) {
						if(obj.getString("result").equalsIgnoreCase("true")) {
							
							String versionNo = obj.getString("versionNo");
							try {
								
								if(Integer.parseInt( versionNo)< (getVersionCode(LoginOTPOnlyActivity.this))) {
									// code to update 
								}
							} catch(Exception e) {
								Log.d("***", "Error in downloadSettings versionNo : " + e);
							}
						}
					}*/
				/*/	if(obj.getString("configurationType").equalsIgnoreCase("password type") ==false ) {
						Intent intent = new Intent(this,LoginActivity.class);
						startActivity(intent);
						LoginOTPOnlyActivity .this.finish();
					} /*/
					
				
				}
			} catch (Exception e) {
				// TODO: handle exception
				;
			}
		}

	 
		 
		private BroadcastReceiver responseBcr = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				try {
					 
					 
					 try {
						 nwAvailable = intent.getBooleanExtra("RESPONSE", true);
					 }catch(Exception e ) {
						 nwAvailable = Boolean.parseBoolean( intent.getExtras().get("RESPONSE").toString() );
					 }
					
					 
					if(nwAvailable)
					{
						TextView errorText= (TextView) LoginOTPOnlyActivity.this.findViewById(R.id.errorLabel);
						 errorText.setText("");
						 DBCheckAndSend ds = new DBCheckAndSend(LoginOTPOnlyActivity.this);
						 ds.check();
					} else {
						 
						TextView errorText= (TextView) LoginOTPOnlyActivity.this.findViewById(R.id.errorLabel);
						 errorText.setText("Invalid OTP");
					}
						
					   
				}catch (Exception e) {
				//	Toast.makeText(CompleteGPSLocatorService .this ,  "Error In Reader "+e, Toast.LENGTH_LONG).show();
					Log.d("***", "Error in responseBcr in LoginOTPOnlyActivity : " + e);
				}
				
			}
			 
		 };

		 
		 private BroadcastReceiver dbbcr=new BroadcastReceiver (){

				@Override
				public void onReceive(final Context context, final Intent intent) {
					 Log.d("***","In onReceive function");
					
			         try{
			        	 TextView statusText= (TextView) LoginOTPOnlyActivity.this.findViewById(R.id.errorLabel);
			        	 DBCheckAndSend ds = new DBCheckAndSend(LoginOTPOnlyActivity.this);
			        	 if(networkUtil==null ) {
			        		 networkUtil = new NetworkUtil();
			        	 }
			        	 if(networkUtil.getConnectivityStatus(LoginOTPOnlyActivity.this)!=0 && nwAvailable )
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



}
