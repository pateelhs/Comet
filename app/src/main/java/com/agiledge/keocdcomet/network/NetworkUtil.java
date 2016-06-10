package com.agiledge.keocdcomet.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
	
	public   int TYPE_WIFI = 1;
	public   int TYPE_MOBILE = 2;
	public   int TYPE_NOT_CONNECTED = 0;
	
	
	public   int getConnectivityStatus(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (null != activeNetwork) {
			if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
				return TYPE_WIFI;
			
			if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
				return TYPE_MOBILE;
		} 
		return TYPE_NOT_CONNECTED;
	}
	
	public   String getConnectivityStatusString(Context context) {
		int conn = this.getConnectivityStatus(context);
		String status = null;
		if (conn == this.TYPE_WIFI) {
			status = "Wifi enabled";
		} else if (conn == this.TYPE_MOBILE) {
			status = "Mobile data enabled";
		} else if (conn == this.TYPE_NOT_CONNECTED) {
			status = "Not connected to Internet";
		}
		return status;
	}
}
