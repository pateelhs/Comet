package com.agiledge.keocdcomet.tasks;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.agiledge.keocdcomet.TripDetailsActivity;
import com.agiledge.keocdcomet.db.JSONDBAdapter;
import com.agiledge.keocdcomet.gss.common.CometSettings;
import com.agiledge.keocdcomet.gss.common.OtherFunctions;
import com.agiledge.keocdcomet.network.Listener;

import org.json.JSONObject;

import java.util.Calendar;

public class GPSLocatorService extends Service implements Listener {
	private String tripId;

	private MobileSender mSender;
	private final String TAG = "AGILEDGEGPS";
	public static final String BROADCAST_ACTION="location";
	public static final String NORESPONSE_ACTION="INTERNET_RESPONSE";
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
	public   void setRequest(boolean request) {
		this.request = request;
	}

	private LocationManager mLocationManager = null;
	private   final int LOCATION_INTERVAL = 10000;
	private   final float LOCATION_DISTANCE = 5f;
	private double distanceCovered;
	private long timeElapsed;
	private long startTime;
	private Location lastPhoneLocation;
	boolean isFirst=true;
	private   Location phoneLocation;
	private   boolean request = false;
	private   boolean gpsReady = false;
	private boolean nwAvailable=true;

	private Intent broadCastIntent;
	private Intent noResponseBroadCastIntent;
	private JSONDBAdapter db;
	Location mLastLocation;
	private BroadcastReceiver bcr = new BroadcastReceiver(){
		public void onReceive(android.content.Context context, Intent intent) {



			nwAvailable= intent.getBooleanExtra("nwAvailable",true);

			Log.d("***", "Got nwAvailable: "+nwAvailable);
			 /* EditText gpsLabel= (EditText) TripDetailsActivity.this.findViewById(R.id.gpsStatusText);
			  count++;
			  gpsLabel.setText("Gps:"+count);
			 */



		}
	};

	/*private final IBinder mBinder = new MyBinder();*/
	/*public class MyBinder extends Binder {
	    public GPSLocatorService getService() {
	      return GPSLocatorService.this;
	    }
	  }*/
	private class LocationListener implements android.location.LocationListener {


		public LocationListener(String provider) {
			Log.d(TAG, "LocationListener " + provider);
			mLastLocation = new Location(provider);

			phoneLocation = mLastLocation;
			lastPhoneLocation=mLastLocation;
		}

		private void sendCoordinates(Location location   ) {
			Log.d("***", "In send  Cordinate Info 0");
			JSONObject obj = new JSONObject();
			try {
				Context context = getApplicationContext();
				Log.d("***", "In send  Cordinate Info 1");
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				Log.d("***", "In send  Cordinate Info 2");
				String device_id = tm.getDeviceId();
				Log.d("***", "In send  Cordinate Info 3");
				obj.put("ACTION", "vehiclePosition");
				obj.put("IMEI", "" + device_id);
				obj.put("tripId", tripId);
				obj.put("longitude",  location.getLongitude());
				obj.put("latitude",    location.getLatitude());
				obj.put("distanceCovered",    distanceCovered);
				obj.put("timeElapsed",    timeElapsed);
				obj.put("NUANCE", Calendar.getInstance().getTimeInMillis());
				Log.d("***", "In send  Cordinate Info 4");
				mSender = new MobileSender(getApplicationContext(),GPSLocatorService.this);
				mSender.setPath(CometSettings.ServerAddress);
				mSender.setInObj(obj);
				mSender.setPath(CometSettings.ServerAddress);
				mSender.setContext(context);
				mSender.execute();

				if( db.insertJSON(mSender.getInObj())>0)
				{
					Log.d("***", "Geo code inserted into DB");

					obj.put("result", "true");



				}else{
					Log.d("***", "Geo code not inserted into DB");
				}

			}

			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}



		@Override
		public void onLocationChanged(Location location) {
			Log.e(TAG, "onLocationChanged: " + location);

			if(isFirst) {
				isFirst=false;
			} else {
				Log.d("***","last loc "+ lastPhoneLocation.getLatitude() );
				Log.d("***"," current loc "+location.getLatitude());
				Log.d("***"," mLastLocation loc "+mLastLocation.getLatitude());
				lastPhoneLocation.set(mLastLocation);
				distanceCovered+= distFrom(lastPhoneLocation.getLatitude(), lastPhoneLocation.getLongitude(), location.getLatitude(), location.getLongitude());
			}
			mLastLocation.set(location);
			timeElapsed = Calendar.getInstance().getTimeInMillis()-startTime;

			location = mLastLocation;

			setPhoneLocation(location);

			broadCastIntent.putExtra("latitude", location.getLatitude());
			broadCastIntent.putExtra("longitude",   location.getLongitude());
			broadCastIntent.putExtra("distanceCovered",   distanceCovered);
			broadCastIntent.putExtra("timeElapsed",   timeElapsed);

			sendBroadcast(broadCastIntent);

			sendCoordinates(location);
			//mSender.sendCordinateInfo();
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

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.e(TAG, "onStartCommand");
		super.onStartCommand(intent, flags, startId);
		Bundle b= intent.getExtras();
		tripId=b.getString("TRIPID");
		distanceCovered=b.getDouble("distanceCovered");
		timeElapsed=b.getLong("timeElapsed");
		startTime = Calendar.getInstance().getTimeInMillis() - timeElapsed;
		return START_STICKY;
	}

	@Override
	public void onCreate( ) {
		String hasResponse = OtherFunctions.getSharedValue(getApplicationContext(), "hasResponse");
		if( hasResponse!=null && hasResponse.equalsIgnoreCase("true")) {
			this.hasResponse = true;
		} else {
			this.hasResponse=false;
		}
		Log.e(TAG, "onCreate");
		initializeLocationManager();
		registerReceiver(bcr, new IntentFilter(TripDetailsActivity.INTENT_ACTION));
		registerReceiver(responseBcr, new IntentFilter(GPSLocatorService.NORESPONSE_ACTION));
		broadCastIntent= new Intent(BROADCAST_ACTION);
		noResponseBroadCastIntent = new Intent(NORESPONSE_ACTION);
		db=new JSONDBAdapter(getApplicationContext());
		/*
		 * try { mLocationManager.requestLocationUpdates(
		 * LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL,
		 * LOCATION_DISTANCE, mLocationListeners[1]); } catch
		 * (java.lang.SecurityException ex) { Log.i(TAG,
		 * "fail to request location update, ignore", ex); } catch
		 * (IllegalArgumentException ex) { Log.d(TAG,
		 * "network provider does not exist, " + ex.getMessage()); }
		 */
		try {

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
		Log.e(TAG, "onDestroy");
		super.onDestroy();
		unregisterReceiver(responseBcr);
		unregisterReceiver(bcr);
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

					if( db.insertJSON(mSender.getInObj())>0)
					{
						Log.d("***", "Request Inserted to DB in GPSLocatorService ");
						Log.d("***", " Has response : " + hasResponse);
						obj.put("result", "true");
						if(hasResponse) {
							OtherFunctions.insertSharedValue(getApplicationContext(), "hasResponse", "false");
							noResponseBroadCastIntent.putExtra("RESPONSE",  false );
							noResponseBroadCastIntent.putExtra("RESPONSE_1",  false );
							noResponseBroadCastIntent.putExtra("SENDER",   "GPSLocatorService");

							sendBroadcast(noResponseBroadCastIntent);
						}


					}else{

						Log.d("***","insertion to db failed in GPSLOCATOR SERVICE");

					}


				} else {
					if(hasResponse==false) {
						Log.d("***", " has response is false : ");
						OtherFunctions.insertSharedValue(getApplicationContext(), "hasResponse", "true");
						noResponseBroadCastIntent.putExtra("RESPONSE",   true);
						noResponseBroadCastIntent.putExtra("SENDER",   "GPSLocatorService");
						sendBroadcast(noResponseBroadCastIntent);
					}
				}


			}
		}catch(Exception e) {
			Log.d("***", "Error in GPSLocatorService : " + e);
		}
	}


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