package com.agiledge.keocdcomet.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.agiledge.keocdcomet.R;
import com.agiledge.keocdcomet.Select_tripActivity;
import com.agiledge.keocdcomet.TripDetailsActivity;
import com.agiledge.keocdcomet.db.JSONDBAdapter;
import com.agiledge.keocdcomet.gss.common.CometSettings;
import com.agiledge.keocdcomet.network.DbLapListener;
import com.agiledge.keocdcomet.network.Listener;
import com.agiledge.keocdcomet.network.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class TripSheetTask  implements DbLapListener, Listener {
	private JSONDBAdapter db;
	private Context context;
	private  Activity currentActivity;
	private String message="";
	private String tripId;
	private MobileSender mobile;
	private String[] tripTimes;
	/*private ImageView itemImg;*/
	private CheckBox checkBox;


	private boolean nwAvailable;
	public TripSheetTask(Context contextParam,boolean nwAvailable) {
		this.nwAvailable=nwAvailable;
		context=contextParam;
		mobile = new MobileSender(contextParam,TripSheetTask.this);
		db=new JSONDBAdapter(contextParam);
		// TODO Auto-generated constructor stub


	}



	public void stopTrip(String tripId, String nuance)
	{

		try{

			JSONObject obj= new JSONObject();
			obj.put("ACTION", "stopTrip");
			Log.d("***", "In stop trip");


			Log.d("***", "Trip id :"+ tripId);


			obj.put("IMEI", ""+ mobile.getIMEINumber());
			obj.put("tripId", tripId);
			obj.put("NUANCE", nuance);

			this.tripId=tripId;
			//	GPSLocatorService.setTripCode(tripId);

			execute(obj);


		}catch (Exception e) {
			Toast.makeText(context,  " Error : . in stopTrip ."+e, Toast.LENGTH_LONG).show();
		}
	}

	public void forceStopTrip(String tripId, String nuance)
	{

		try{

			JSONObject obj= new JSONObject();
			obj.put("ACTION", "forceStopTrip");
			Log.d("***", "In stop trip");


			Log.d("***", "Trip id :"+ tripId);


			obj.put("IMEI", ""+ mobile.getIMEINumber());
			obj.put("tripId", tripId);
			obj.put("NUANCE", nuance);

			this.tripId=tripId;
			//	GPSLocatorService.setTripCode(tripId);

			execute(obj);


		}catch (Exception e) {
			Toast.makeText(context,  " Error : . in ForceStopTrip ."+e, Toast.LENGTH_LONG).show();
		}
	}


	private void execute(JSONObject obj) throws JSONException
	{
		mobile.setPath(CometSettings.ServerAddress);

		mobile.setInObj(obj);
		Log.d("***", "before check ");
		//db.isEmpty1()&&NetworkUtil.getConnectivityStatus(mobile.context)!=NetworkUtil.TYPE_NOT_CONNECTED
		if(nwAvailable&& new NetworkUtil().getConnectivityStatus(mobile.context)!=0)
		{
			Log.d("***", "before send ");
			mobile.execute( );
		}else
		{
			Log.d("***", "Network not is available");
			if( db.insertJSON(mobile.getInObj())>0)
			{
				Log.d("***", "Request Inserted to DB");

				obj.put("result", "true");

				onPostDbExecute(true,obj);

			}else{
				onPostDbExecute(false,obj);
			}

		}
	}


	public void logout()
	{

		try{
			if(true)
			{

				JSONObject obj= new JSONObject();
				obj.put("ACTION", "logout");
				obj.put("NUANCE", String.valueOf(Calendar.getInstance().getTimeInMillis()));


				obj.put("IMEI", ""+ mobile.getIMEINumber());


				execute(obj);
			}
		}catch (Exception e) {
			Toast.makeText(context,  " Error : ..."+e, Toast.LENGTH_LONG).show();
		}

	}
	public void alarm(String nuance)
	{

		try{
			if(true)
			{

				JSONObject obj= new JSONObject();
				obj.put("ACTION", "alarm");
				obj.put("NUANCE", nuance);
				Log.d("***","nanuce at alarm() : "+ nuance);


				obj.put("IMEI", ""+ mobile.getIMEINumber());


				execute(obj);
			}
		}catch (Exception e) {
			Toast.makeText(context,  " Error : ..."+e, Toast.LENGTH_LONG).show();
		}

	}

	public void startTrip(String tripId, String nuance)
	{

		try{


			JSONObject obj= new JSONObject();
			obj.put("ACTION", "startTrip");
			obj.put("NUANCE", nuance);
			Log.d("***", "In start trip");



			Log.d("***", "Trip id :"+ tripId);

			obj.put("IMEI", ""+ mobile.getIMEINumber());
			obj.put("tripId", tripId);

			this.tripId=tripId;
			//	GPSLocatorService.setTripCode(tripId);
			Log.d("***", "in startTrip before execute fn ");

			execute(obj);

		}catch (Exception e) {
			Toast.makeText(context,  " Error :  in start trip.."+e, Toast.LENGTH_LONG).show();
		}

	}

	public void getTripLabels(int day,int month,int year)
	{

		try{
			if(true)
			{

				JSONObject obj= new JSONObject();
				obj.put("ACTION", "gettrips");
				//	DatePicker date = (DatePicker)currentActivity.findViewById(R.id.DatePickerTripDate);

				obj.put("date", year+"-"+ month+"-"+ day);
				obj.put("IMEI", ""+ mobile.getIMEINumber());

				if(new NetworkUtil().getConnectivityStatus(mobile.getContext())!=0)
				{
					loadSpinner();
					execute(obj);
				}else
				{
					Toast.makeText(context,  " Network Not Available ", Toast.LENGTH_LONG).show();
				}
			}
		}catch (Exception e) {
			Toast.makeText(context,  " Error : ..."+e, Toast.LENGTH_LONG).show();
		}

	}

	public void getTrip(String tripLabel, String date )
	{
		Log.d("***", "In get Trip");
		try{
			Log.d("***","In getTrip");
			JSONObject obj= new JSONObject();
			obj.put("ACTION", "gettrip");

			String tripT[]= tripLabel.split(" ");
			obj.put("time",tripT[0]);
			obj.put("log",tripT[1]);
			//DatePicker date = (DatePicker)currentActivity.findViewById(R.id.DatePickerTripDate);


			obj.put("date", date);
			obj.put("IMEI", ""+ mobile.getIMEINumber());



			Toast.makeText(context,  " Please wait ...", Toast.LENGTH_LONG).show();
			if(new NetworkUtil().getConnectivityStatus(mobile.getContext())!=0)
			{
				loadSpinner();
				execute(obj);
			}else
			{
				Toast.makeText(context,  " Network Not Available ", Toast.LENGTH_LONG).show();
			}


		}catch (Exception e) {
			Toast.makeText(context,  " Error : ..."+e, Toast.LENGTH_LONG).show();
		}

	}
	public void employeeGetIn(String empCode, String password, CheckBox checkBox1, String tripId, Double latitude,Double longitude, String nuance )
	{

		try{
			checkBox=	checkBox1;
			JSONObject obj= new JSONObject();
			obj.put("ACTION", "employeeGetIn");
			obj.put("empCode",empCode);
			obj.put("NUANCE", nuance);
			Log.d("***","Before tripCode to Controle");
			Log.d("***","nuance in employeeGetIn :"+nuance);

			Log.d("***","Before tripCode to String");

			obj.put("tripId",tripId);
			obj.put("password",password);
			obj.put("IMEI", ""+ mobile.getIMEINumber());



			obj.put("latitude", ""+ latitude);
			obj.put("longitude", ""+ longitude);


			//Toast.makeText(context,  " Please wait ...", Toast.LENGTH_LONG).show();
			execute(obj);

		}catch (Exception e) {
			Toast.makeText(context,  " Error : in EmployeeGetIn "+e, Toast.LENGTH_LONG).show();
			System.out.println("Error :"+e);
		}

	}

	public Activity getCurrentActivity() {
		return currentActivity;
	}

	public void setCurrentActivity(Activity currentActivity) {
		this.currentActivity = currentActivity;
	}




	private void showTripLabels(JSONObject obj)
	{

		String result1="";
		try {

			JSONArray jsonArray =  obj.getJSONArray("tripTime");

			tripTimes=new String[jsonArray.length()];
			if (jsonArray != null) {
				for (int i=0;i<jsonArray.length();i++){
					JSONObject objo=(JSONObject) jsonArray.get(i);
					tripTimes[i]= objo.getString("trip_time")+" " +objo.getString("trip_log");
					Log.d("***","||"+ tripTimes[i]);
				}

				Log.d("***","In getting trips on post execute");

				Spinner l = (Spinner) currentActivity. findViewById(R.id.spinnerTripLabels);
				Select_tripActivity parent= (Select_tripActivity)currentActivity;
				parent.l=l;

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(currentActivity, android.R.layout.simple_spinner_item, tripTimes);

				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				l.setAdapter(adapter);



				Log.d("***","In onpostexecute result : " + result1);


			}else
			{
				Toast.makeText(context,  " No Trips Found", Toast.LENGTH_LONG).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(context,  " Error : "+e, Toast.LENGTH_LONG).show();
		}
	}


	private void showTripDetails(JSONObject obj)
	{
		String result1="";
		try {


			if(obj.getString("ACTION").equalsIgnoreCase("gettrip"))
			{





				Log.d("***","In getting trip on post execute");

				Intent intent =new Intent(currentActivity, TripDetailsActivity.class);
				Log.d("***"," "+obj.toString());
				intent.putExtra("trip", obj.toString());
				currentActivity.startActivity(intent);




				Log.d("***","In onpostexecute result : " + result1);


			}else
			{
				Toast.makeText(context,  " No Trips Found", Toast.LENGTH_LONG).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(context,  " Error : "+e, Toast.LENGTH_LONG).show();
		}
	}


	public void authenticateEscort(String tripId, String userName, String password, String nuance, Double latitude,Double longitude, String log) {

		try{



			JSONObject obj= new JSONObject();

			Log.d("***", "In stop trip");
			String action = "escortGetIn";
			if(log.equals("IN")) {
				action = "escortGetIn";
			} else {
				action = "escortGetOut";
			}


			obj.put("ACTION", action);
			obj.put("IMEI", ""+ mobile.getIMEINumber());
			obj.put("tripId", tripId);
			obj.put("userName", userName);
			obj.put("password", password);
			obj.put("NUANCE", nuance);
			obj.put("latitude", ""+ latitude);
			obj.put("longitude", ""+ longitude);
			obj.put("log", log);



			execute(obj);

		}catch (Exception e) {
			Toast.makeText(context,  " Error : ..."+e, Toast.LENGTH_LONG).show();
		}
	}



	public void employeeGetOut(String empCode, String password, CheckBox checkBox1, String tripId, Double latitude,Double longitude, String nuance )
	{

		try{
			checkBox=	checkBox1;
			JSONObject obj= new JSONObject();
			obj.put("ACTION", "employeeGetOut");
			obj.put("empCode",empCode);
			Log.d("***","Before tripCode to Controle");
			Log.d("***","nuance in employeeGetOut :"+nuance);
			Log.d("***","Before tripCode to String");

			obj.put("tripId",tripId);
			obj.put("password",password);
			obj.put("IMEI", ""+ mobile.getIMEINumber());

			obj.put("NUANCE", nuance);

			obj.put("latitude", ""+ latitude);
			obj.put("longitude", ""+ longitude);


			//Toast.makeText(context,  " Please wait ...", Toast.LENGTH_LONG).show();
			execute(obj);

		}catch (Exception e) {
			Toast.makeText(context,  " Error : ..."+e, Toast.LENGTH_LONG).show();
			System.out.println("Error :"+e);
		}

	}


	private void loadSpinner() {
		try {
			ProgressBar spinner = (ProgressBar)currentActivity.findViewById(R.id.progressBar1);
			spinner.setVisibility(View.VISIBLE);
		}catch(Exception e) {}
	}

	private void stopSpinner() {
		try {
			ProgressBar spinner = (ProgressBar)currentActivity.findViewById(R.id.progressBar1);
			spinner.setVisibility(View.GONE);
		}catch(Exception e) {}
	}

	public void updateTripTime(String tripId,String userName, String password,String time,String nuance)
	{

		try{



			JSONObject obj= new JSONObject();
			obj.put("ACTION", "updateTime");
			Log.d("***", "In stop trip");





			obj.put("IMEI", ""+mobile.getIMEINumber());
			obj.put("tripId", tripId);
			obj.put("userName", userName);
			obj.put("password", password);
			obj.put("NUANCE", nuance);
			obj.put("time", time);

			// GPSLocatorService.setTripCode(tripId);

			execute(obj);

		}catch (Exception e) {
			Toast.makeText(context,  " Error : ..."+e, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void update(boolean result, JSONObject obj) {


		Log.d("***",""+ result);
		if(result==true&&obj!=null)
		{

			try {
				String action=obj.getString("ACTION");

				String responseStatus=obj.getString("responseStatus");
				if(responseStatus.equalsIgnoreCase("YES")) {



					Log.d("***","Response Action " + action);
					if(action.equalsIgnoreCase("gettrips"))
					{
						try {
							TextView statusText = (TextView) currentActivity.findViewById(R.id.statusText);
							statusText.setText("");
						}catch (Exception e) { }
						stopSpinner();
						showTripLabels(obj);
					}else if( action.equalsIgnoreCase("gettrip"))
					{
						try {
							TextView statusText = (TextView) currentActivity.findViewById(R.id.statusText);
							statusText.setText("");
						}catch (Exception e) { }
						showTripDetails(obj);
					}else if(action.equalsIgnoreCase("employeeGetIn") )
					{
							/* TripDetailsActivity activity = (TripDetailsActivity)currentActivity;
							 activity.triggerEvent(obj);*/
					}
					else if(action.equalsIgnoreCase("employeeGetOut") )
					{
							 /*TripDetailsActivity activity = (TripDetailsActivity)currentActivity;
							 activity.triggerEvent(obj);*/
					}
					else if(action.equalsIgnoreCase("startTrip")||action.equalsIgnoreCase("stopTrip") || action.equalsIgnoreCase("forceStopTrip") )
					{

						TripDetailsActivity activity = (TripDetailsActivity)currentActivity;
						activity.triggerEvent(obj);


					}
					else if(action.equalsIgnoreCase("alarm") )
					{

						TripDetailsActivity activity = (TripDetailsActivity)currentActivity;
						activity.triggerEvent(obj);



						// currentActivity.stopService(new Intent( currentActivity,com.agiledge.comet.tasks.GPSLocatorService.class));

					} else if(action.equalsIgnoreCase("logout") )
					{
						TripDetailsActivity activity = (TripDetailsActivity)currentActivity;
						activity.triggerEvent(obj);


					} else	 if(action.equalsIgnoreCase("updateTime") )
					{
						TripDetailsActivity activity = (TripDetailsActivity)currentActivity;
						activity.triggerEvent(obj);


					} else	 if(action.equalsIgnoreCase("escortGetOut") || action.equalsIgnoreCase("escortGetIn") )
					{

							/* TripDetailsActivity activity = (TripDetailsActivity)currentActivity;
							 activity.triggerEvent(obj);*/
					}

				} else if(responseStatus.equalsIgnoreCase("TIMEOUT") || responseStatus.equalsIgnoreCase("NORESPONSE")){
					if(action.equalsIgnoreCase("gettrips")||action.equalsIgnoreCase("gettrip"))
					{
						try {
							TextView statusText = (TextView) currentActivity.findViewById(R.id.statusText);
							statusText.setText("No Internet..");
							stopSpinner();
						}catch (Exception e) { }
						stopSpinner();
						// showTripLabels(obj);
					} else
					if( db.insertJSON(mobile.getInObj())>0)
					{
						Log.d("***", "Request Inserted to DB");

						obj.put("result", "true");

						onPostDbExecute(true,obj);

					}else{
						onPostDbExecute(false,obj);
					}


				}

				else{
					throw new Exception("Response mismatches");
				}




			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(context,  " Error : "+e, Toast.LENGTH_LONG).show();
			}

		}else
		{
			Toast.makeText(context,  "Invalid Response", Toast.LENGTH_LONG).show();
		}

	}



	@Override
	public void onPostDbExecute(Boolean result, JSONObject obj) {


		Log.d("***",""+ result);
		if(result==true&&obj!=null)
		{
			TripDetailsActivity activity = (TripDetailsActivity)currentActivity;
			activity.triggerDBEvent(obj);
		}else{
			JSONObject jobj= new JSONObject();
			try{
				jobj.put("ACTION", "ERROR");
				jobj.put("MESSAGE", "DB FAILURE !");

				TripDetailsActivity activity = (TripDetailsActivity)currentActivity;
				activity.triggerDBEvent(jobj);
			}catch (Exception e) {
				Log.d("***","Error message");
			}
		}

	}
}
