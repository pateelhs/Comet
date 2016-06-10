package com.agiledge.keocdcomet.network;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
 

public class NetworkChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {
		 Log.d("***","In onReceive function");
		
         try{
        	 
        	 DBCheckAndSend ds = new DBCheckAndSend(context);
        	 if(new NetworkUtil().getConnectivityStatus(context)!=0)
        	 {
        		 ds.check();
        	 }
		
          
    
		 
        	 
         }catch (Exception e) {
        	 Log.d("***","Error in onReceive of N/wChnageReceiver");
		}
	}
}
