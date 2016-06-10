package com.agiledge.keocdcomet.network;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.agiledge.keocdcomet.db.JSONDBAdapter;

public class DBCheckAndSend  implements Listener   {
private Context context;
 
private JSONDBAdapter dba;
	public DBCheckAndSend(Context contextParam) {
		
		context=contextParam;
		 dba= new JSONDBAdapter(context);
		// TODO Auto-generated constructor stub
	}
	
	public void check()  
	{
		Log.d("***", "IN Check");
		dba.cleanAllStatus1();
		if(dba.isAnyEntryInBufferTable1()==false)
		{
			Log.d("***","In check () but no data");
		}else{
			int i=0;
		 while(dba.isAnyEntryInBufferTable1())
		 {
			 i++;
			 try{
				 Log.d("***", "Taking ...");
			 JSONObject data=dba.getFirstData1();
			 
			 DBCheckAndSendTask task = new DBCheckAndSendTask(context,this);
			 if(new NetworkUtil().getConnectivityStatus(context)!=0)
			 {
				 task.send(data);
			 }
			 
			 }catch (JSONException e) {
				 Log.d("***", "Exception in check of DBCheckAndSend "+e);
			}
		 }
		}
	}
	


	@Override
	public void update(boolean result, JSONObject obj) {
	 
		 Log.d("***","Result : "+ result);
		if(result==true&&obj!=null)
		{
			 
			 try {
				 String action=obj.getString("ACTION");
				 Log.d("***","Response Action " + action);
				 String nuance=obj.getString("NUANCE");
				 Log.d("***","Response NUANCE " + nuance);
			//	 Toast.makeText(context, nuance+", "+action, Toast.LENGTH_SHORT).show();
				 dba.delete1(nuance);
				 Listener listener;
				 listener=(Listener)context;
				 listener.update(result, obj);
				 
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

 

}
