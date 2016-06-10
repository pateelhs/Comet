package com.agiledge.keocdcomet.tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.agiledge.keocdcomet.network.Listener;

public class MobileSender extends AsyncTask<Void, Void, Boolean> {

	 private String responseMode;
	//private JSONDBAdapter db=null;


		private Listener listener; 
		private JSONObject inObj;
		private JSONObject outObj;
		private String status;
		private String path;
		private String sendStatus;
		public Object SYSTEM_SERVICE;
		public Context context;
		//private JSONDBAdapter db=null;

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}


	public MobileSender(Context contextParam, Listener listenerParam)
	{
		listener=listenerParam;
		this.context=contextParam;
		SYSTEM_SERVICE=context.getSystemService(Context.TELEPHONY_SERVICE);
	 
	}

	
	public  void getResponse() throws Exception 
	{
		/*HttpClient httpClient = new DefaultHttpClient();
	 
		HttpPost httpPost = new HttpPost(path);
		httpPost.setHeader("Content-type","application/json");
		
		
		
		HttpResponse response;

		try{
			HttpEntity se = new StringEntity(inObj.toString());
			httpPost.setEntity(se);
			response= httpClient.execute(httpPost);
			HttpEntity httpEntity1 = response.getEntity();
			String line1;
			if(httpEntity1!=null)
			{
				InputStream is= httpEntity1.getContent();
				BufferedReader b = new BufferedReader(new InputStreamReader(is));
				String lines1="";
				while((line1=b.readLine())!=null)
				{
					lines1+=line1+"\n";
					Log.i("Response",line1);
				}
				 outObj=new JSONObject(lines1);
				
				 
				
			is.close();
			}
			
			
		}catch (Exception e) {
			Log.d("Error in Mobile sender ", ""+e);
			outObj=null;
			
		}
*/		 
		makeRequest(path,inObj);
	}

	public   JSONObject makeRequest(String path, JSONObject cmd) throws Exception {
		
		

	HttpClient httpClient = new DefaultHttpClient();
	 
	HttpPost httpPost = new HttpPost(path);
	JSONObject rObj=null;
	HttpResponse response;
	try{
		
	 
		
		//------using http post
		sendStatus="sending";

		Log.d("About to send JSON",cmd.toString());
		httpPost.setHeader("Content-type","application/json");
		HttpEntity se = new StringEntity(cmd.toString());
		httpPost.setEntity(se);
		
		
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used. 
		int timeoutConnection = 9000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 15000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		httpClient = new DefaultHttpClient(httpParameters);
		response= httpClient.execute(httpPost);
		sendStatus="finished";
		Log.i("ResponsePOST",response.getStatusLine().toString());
		HttpEntity httpEntity1 = response.getEntity();
		String line1;
		if(httpEntity1!=null)
		{
			InputStream is= httpEntity1.getContent();
			BufferedReader b = new BufferedReader(new InputStreamReader(is));
			String lines1="";
			while((line1=b.readLine())!=null)
			{
				lines1+=line1+"\n";
				Log.i("Response",line1);
			}
			 rObj=new JSONObject(lines1);
			
			 
			
		is.close();
			try {
			b.close();
			httpClient.getConnectionManager().closeExpiredConnections();
			}catch(Exception e) {}
		}
		
		status="YES";
	}catch( ConnectTimeoutException e) {
		status="TIMEOUT";
		Log.d("***", "Connection Time Out Exception : "+ e);
		
	} catch(NoHttpResponseException re) {
		Log.d("***", "NoHttpResponseException  Exception : "+ re);
		status="NORESPONSE";
	} catch(HttpHostConnectException he) {
		status="NORESPONSE";
	}
	
	catch (Exception e) {
		Log.d("Error in Mobile sender ", ""+e);
		status="ERROR";
	}
		return rObj;
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		boolean flag=false;
		try {
			Log.d("Request: ", "Request____))");
		 

		
			 
			outObj= makeRequest(path,inObj);
			flag=true;
			
			 
			//getResponse();
			 

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("Response : ", "Exception in response "+e);
			
			e.printStackTrace();
			flag=false;
		}
		Log.d("***","In in execute method");
		
		return flag;
	}

	public JSONObject getInObj() {
		return inObj;
	}

	public void setInObj(JSONObject inObj) {
		this.inObj = inObj;
	}

	public JSONObject getOutObj() {
		return outObj;
	}

	public void setOutObj(JSONObject outObj) {
		this.outObj = outObj;
	}

	public String getSendStatus() {
		return sendStatus;
	}

	public void setSendStatus(String sendStatus) {
		this.sendStatus = sendStatus;
	}


	public String getIMEINumber()
	 {
		 TelephonyManager tm = (TelephonyManager) SYSTEM_SERVICE;
 
		 String device_id = tm.getDeviceId();
		 return device_id;
		 

		 
	 }

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		Log.d("***", "Status : " + status);
		if(status.equalsIgnoreCase("YES"))   {
			try {
				getOutObj() .put ("responseStatus",  status);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			listener.update(result, getOutObj());
		} else {
			try {
				inObj.put ("responseStatus",  status);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("***", "Mobile Sender's call back.. :to "+ listener.toString()  );	 
			listener.update(result, inObj);
		}
		
	}

	public String getResponseMode() {
		return responseMode;
	}

	public void setResponseMode(String responseMode) {
		this.responseMode = responseMode;
	}
 
 
}
