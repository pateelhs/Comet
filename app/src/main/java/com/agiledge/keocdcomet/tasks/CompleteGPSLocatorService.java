package com.agiledge.keocdcomet.tasks;

import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


import com.agiledge.keocdcomet.network.Listener;


public class CompleteGPSLocatorService  extends Service implements Listener {

	 
	
	 
	
	public static final String STOP_BROADCAST_ACTION="StopCompleteGPSLocatorService";
	public static final String NORESPONSE_ACTION="INTERNET_RESPONSE";
	
	private final String TAG = "AGILEDGEGPS";
	private MobileSender mSender;
	private Intent noResponseBroadCastIntent;
	public boolean hasResponse=true;
	public   boolean isRequest() {
		
		return request;
	}

	public  double distFrom(double lat1, double lng1, double lat2, double lng2) {
	    double earthRadius = 6371.00;
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double sindLat = Math.sin(dLat / 2);
	    double sindLng = Math.sin(dLng / 2);
	    double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
	            * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    return dist;
	    }
	

	private LocationManager mLocationManager = null;
	private   final int LOCATION_INTERVAL = 10000;
	private   final float LOCATION_DISTANCE = 5f;
	 
  
	boolean isFirst=true;
	private   Location phoneLocation;
	private   boolean request = false;
	private   boolean gpsReady = false;
	 
	
	 
	  
	/*private final IBinder mBinder = new MyBinder();*/
	/*public class MyBinder extends Binder {
	    public CompleteGPSLocatorService getService() {
	      return CompleteGPSLocatorService.this;
	    }
	  }*/
	private class LocationListener implements android.location.LocationListener {
		//Location mLastLocation;

		public LocationListener(String provider) {
			Log.d(TAG, "LocationListener " + provider);
			Location mLastLocation = new Location(provider);

			phoneLocation = mLastLocation;
			 
		}
		
	/*/	private void sendCoordinates(Location location   ) {
			Log.d("***(full)", "In send  Cordinate Info 0");
			JSONObject obj = new JSONObject();
			try {
				Context context = getApplicationContext();
				 
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				 
				String device_id = tm.getDeviceId();
				 
				obj.put("ACTION", "fullVehiclePosition");
				obj.put("IMEI", "" + device_id);
 
				obj.put("longitude",  location.getLongitude());
				obj.put("latitude",    location.getLatitude());
 				obj.put("NUANCE", Calendar.getInstance().getTimeInMillis());
				Log.d("***(full)", "In send  Cordinate Info");
				 
				mSender = new MobileSender(getApplicationContext(),CompleteGPSLocatorService.this);
				mSender.setInObj(obj);
				mSender.setPath(CometSettings.ServerAddress);
				mSender.setContext(context);
				if( new NetworkUtil().getConnectivityStatus(mSender.getContext())!=0)
				{
					mSender.execute();
				} 
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}/*/

		
		
		@Override
		public void onLocationChanged(Location location) {
			Log.e(TAG, "onLocationChanged: " + location);
			
			if(isFirst) {
				isFirst=false;
			} else {
				 
				Log.d("***(full)"," current loc "+location.getLatitude());
				 
			 
			}
		 
 

		 
			
			setPhoneLocation(location);
			
 		//*/	sendCoordinates(location );
			// mSender.sendCordinateInfo();
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.e(TAG, "onProviderDisabled: " + provider);
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.e(TAG, "onProviderEnabled: " + provider);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.e(TAG, "onStatusChanged: " + provider);
		}
	}

	LocationListener[] mLocationListeners = new LocationListener[] { new LocationListener(
			LocationManager.GPS_PROVIDER)
	// new LocationListener(LocationManager.NETWORK_PROVIDER)
	};

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

/*	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Log.e(TAG, "onStartCommand");
		super.onStartCommand(intent, flags, startId);
		 
		 
	  
		return START_STICKY;
	}
*/
	@Override
	public void onCreate( ) {
		
		Log.e(TAG, "onCreate");
		initializeLocationManager();
		 
		 
		  
		try {
			noResponseBroadCastIntent = new Intent(CompleteGPSLocatorService.NORESPONSE_ACTION);
			 
			registerReceiver(responseBcr,  new IntentFilter(CompleteGPSLocatorService.NORESPONSE_ACTION));
			registerReceiver(stopBcr, new IntentFilter( STOP_BROADCAST_ACTION));
			mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, LOCATION_INTERVAL,
					LOCATION_DISTANCE, mLocationListeners[0]);
		} catch (java.lang.SecurityException ex) {
			Log.i(TAG, "fail to request location update, ignore", ex);
		} catch (IllegalArgumentException ex) {
			Log.d(TAG, "gps provider does not exist " + ex.getMessage());
		}
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy CompletGPSLocatorService");
		super.onDestroy();
		unregisterReceiver(stopBcr);
		unregisterReceiver(responseBcr);
		 
		if (mLocationManager != null) {
			for (int i = 0; i < mLocationListeners.length; i++) {
				try {
					mLocationManager.removeUpdates(mLocationListeners[i]);
				} catch (Exception ex) {
					Log.i(TAG, "fail to remove location listners, ignore", ex);
				}
			}
		}
	}

	private void initializeLocationManager() {

		Log.e(TAG, "initializeLocationManager");
		if (mLocationManager == null) {
			mLocationManager = (LocationManager) getApplicationContext()
					.getSystemService(Context.LOCATION_SERVICE);
			/* if gps is not enabled starts...*/
			
			 
			/* gps is not enable d ends... */

		}
	}

	public   boolean isGpsReady() {
		return gpsReady;
	}

	public   void setGpsReady(boolean gpsReady) {
		 this.gpsReady = gpsReady;
	}

	public   Location getPhoneLocation() {
		return phoneLocation;
	}

	public   void setPhoneLocation(Location phoneLocation) {
		 this.phoneLocation = phoneLocation;
	}

	@Override
	public void update(boolean result, JSONObject obj) {
	//  checking connection with server acknowledgement
			try {
			if(result==true && obj!=null) {
				String responseStatus = obj.getString("responseStatus");
				if(responseStatus.equalsIgnoreCase("TIMEOUT") || responseStatus.equalsIgnoreCase("NORESPONSE")){
					obj.put("result", "true");
					 if(hasResponse) { 
					 		Log.d("***", "Request Inserted to DB in GPSLocatorService ");
					 		 
						
					 		noResponseBroadCastIntent.putExtra("RESPONSE",  false );
								noResponseBroadCastIntent.putExtra("RESPONSE_1",   false);
								noResponseBroadCastIntent.putExtra("SENDER",   "CompleteGPSLocatorService");
								sendBroadcast(noResponseBroadCastIntent);
							
					 }
					  
					 
					 
				 } else {
					 if(hasResponse==false) {
							noResponseBroadCastIntent.putExtra("RESPONSE",   true);
							noResponseBroadCastIntent.putExtra("SENDER",   "CompleteGPSLocatorService");
							sendBroadcast(noResponseBroadCastIntent);
					 }
				 }
				 
					 
			}
			}catch(Exception e) {
				Log.d("***", "Error in GPSLocatorService : " + e);
			}
		
	}
	
	private BroadcastReceiver stopBcr = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			try {
				Log.d("***","Receiving in CompleteGPSLocatorService");
				String action = intent.getStringExtra("action");
				Log.d("***","action : " + action );
				 if(action.equals("show")){
					String message=intent.getStringExtra("message");
					print(message);
					
				} else {
			boolean stopFlag=intent.getBooleanExtra("stop", true);
			Log.d("***","stoFlag : " + stopFlag );
			if(stopFlag) {
				CompleteGPSLocatorService.this.stopSelf();
				 
			}
			} 
			}catch (Exception e) {
				Toast.makeText(CompleteGPSLocatorService .this ,  "Error In Reader "+e, Toast.LENGTH_LONG).show();
			}
			
		}
		 
		public void print(String message) 
		{
	    	Toast.makeText(CompleteGPSLocatorService.this.getApplicationContext(),message, Toast.LENGTH_SHORT).show();
		}
	 };
	 
	 
		private BroadcastReceiver responseBcr = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				try {
					 try {
						 hasResponse = intent.getBooleanExtra("RESPONSE", true);
					 }catch(Exception e ) {
							hasResponse = Boolean.parseBoolean( intent.getExtras().get("RESPONSE").toString() );
					 }
					 
						
					   
				}catch (Exception e) {
				//	Toast.makeText(CompleteGPSLocatorService .this ,  "Error In Reader "+e, Toast.LENGTH_LONG).show();
					Log.d("***", "Error in responseBcr in CompleteGPSLocatorService : " + e);
				}
				
			}
			 
		 };

}
