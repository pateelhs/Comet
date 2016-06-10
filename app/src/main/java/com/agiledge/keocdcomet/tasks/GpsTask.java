package com.agiledge.keocdcomet.tasks;

import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import com.agiledge.keocdcomet.network.Listener;

import android.content.Context;
import android.location.Location;
import android.util.Log;
 

public class GpsTask implements Listener {
MobileSender mobile;
	public GpsTask(Context contextParam) {
		mobile=new MobileSender(contextParam,GpsTask.this);
		// TODO Auto-generated constructor stub
	}

	public void sendCordinateInfo(Location location) {
		Log.d("Request: ", "In SendCordinate Info");
		HashMap<String, String> map = new HashMap<String, String>();
		

		HttpResponse response = null;
		try {
			Log.d("Request: ", "Request");
			JSONObject cmd = new JSONObject();

			cmd.put("ACTION", "location");
			cmd.put("latitude", ""+location.getLatitude());
			cmd.put("longitude", ""+location.getLongitude());
			mobile.makeRequest(mobile.getPath(),cmd);
			// Log.d("Response : data posted to servlet");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("Response : ", "Exception in response ");
			e.printStackTrace();
		}
	}

	@Override
	public void update(boolean result, JSONObject obj) {
		// TODO Auto-generated method stub
		
	}
	
 
	 
}
