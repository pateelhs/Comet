package com.agiledge.keocdcomet.tasks;

 
 
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.agiledge.keocdcomet.LoginActivity;
import com.agiledge.keocdcomet.R;
import com.agiledge.keocdcomet.Select_tripActivity;
import com.agiledge.keocdcomet.TripDetailsActivity;
import com.agiledge.keocdcomet.dto.LoginDto;
import com.agiledge.keocdcomet.gss.common.CometSettings;
import com.agiledge.keocdcomet.network.Listener;
 

 

 

public class LoginTask implements Listener{

	 
private MobileSender mobile;
private Context context;
 

	 
	public LoginTask(Context contextParam) {
		mobile=(MobileSender) new MobileSender(contextParam,LoginTask.this);
		setContext(contextParam);
		// TODO Auto-generated constructor stub
	}

	private LoginDto dto= new LoginDto(); 
	private  Activity currentActivity;
	public Activity getCurrentActivity() {
		return currentActivity;
	}

	
	public void setCurrentActivity(Activity currentActivity) {
		this.currentActivity = currentActivity;
	}

	 
	
	protected void onPostExecute(Boolean result,JSONObject obj) {
		// TODO Auto-generated method stub
		 
		 
		
		if(result==true&&obj!=null)
		{
			 	
				try {
					String responseStatus=obj.getString("responseStatus");
					String action=obj.getString("ACTION"); 
					if(responseStatus.equalsIgnoreCase("YES")) {
					
							
						
						
						if(action.equalsIgnoreCase("login"))
						{
							loginAction(obj);
						}else if(action.equalsIgnoreCase("logout"))
						{
							logoutAction(obj);
						}else if (action.equalsIgnoreCase("gettrip")||action.equalsIgnoreCase("loginOTP") ) {
							getTripAction(obj);
						}else if(action.equalsIgnoreCase("downloadSettings" )) {
							Log.d("***", " downloadSettings response in LoginTask");
							 Listener listener = (Listener) context;
							 listener.update(result, obj);
							 
						}
					} else {
						Log.d("***", "In LoginTaskOtpOnly settings no internet");
						if(currentActivity==null) {
							Log.d("***",  "currentActivity   is null");
						} else {
						 TextView errorText= (TextView) currentActivity.findViewById(R.id.errorLabel);
						 errorText.setText("No Internet");
						}
					}
						} catch ( Exception e) {
							// TODO Auto-generated catch block
							 Toast.makeText(getContext(),  " Error "+e, Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					
				
			 
		}else
		{
			 Toast.makeText(getContext(),  "Operation failed ", Toast.LENGTH_SHORT).show();
			 
		}
		 
		  
		
	}
	
	public void getTripAction(JSONObject obj) {
		try {
			Intent intent =new Intent(currentActivity, TripDetailsActivity.class);
			Log.d("***"," "+obj.toString());
			intent.putExtra("trip", obj.toString());
		     currentActivity.startActivity(intent);
		     currentActivity.finish();
		} catch(Exception e) {
			Log.d("***",e.getMessage());
		}
	}
	
	 
	public void login(   )
	{
		try{
			currentActivity=dto.getCurrentActivity();
		JSONObject obj= new JSONObject();
		obj.put("ACTION", "login");
		obj.put("userName",dto.getUserName());
		obj.put("password", dto.getPassword());
		obj.put("IMEI", ""+ mobile.getIMEINumber());
		mobile.setPath(CometSettings.ServerAddress);
		mobile.	setInObj(obj);
		Button btn =(Button) currentActivity.findViewById(R.id.loginButton);
		
		
		 Toast.makeText(getContext(),  " Please wait ...", Toast.LENGTH_SHORT).show();
		 mobile.execute( );
		 
		}catch (Exception e) {
			 Toast.makeText(getContext(),  " Error "+e, Toast.LENGTH_SHORT).show();
		}
	}
	
	public void logout(   )
	{
		try{
			currentActivity=dto.getCurrentActivity();
		JSONObject obj= new JSONObject();
		obj.put("ACTION", "logout");
	 
		obj.put("IMEI", ""+ mobile.getIMEINumber());
		mobile.setPath(CometSettings.ServerAddress);
		mobile.setInObj(obj);
		 
		 Toast.makeText(getContext(),  " Please wait ...", Toast.LENGTH_SHORT).show();
		 mobile.execute( );
		 
		}catch (Exception e) {
			 Toast.makeText(getContext(),  " Error "+e, Toast.LENGTH_SHORT).show();
		}
	}

	public LoginDto getDto() {
		return dto;
	}

	public void setDto(LoginDto dto) {
		this.dto = dto;
	}
	
	private void loginAction(JSONObject obj)
	{
		 
			try {
			
			
			
			
			if(obj.getString("ACTION").equalsIgnoreCase("login")&&obj.getString("result").equalsIgnoreCase("true"))
			{
				SharedPreferences pr = currentActivity.getSharedPreferences("abc",Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = pr.edit();
				String userName=pr.getString("username", "");
				String password=pr.getString("password", "");
				if(userName!=null && userName.equals("") == false) {
					Log.d("***","Old User name"+ userName);
					editor.remove("username");
					
				}
				if(password != null && password.equals("") == false) {
					Log.d("***","Old Password "+ password);
					editor.remove("password");
					
				}
				editor.putString("username", dto.getUserName()
						);
				editor.putString("password", dto.getPassword()
						);
				editor.commit();
				 TextView errorText= (TextView) currentActivity.findViewById(R.id.errorLabel);
				 errorText.setText("");
				Intent tripsWithDate= new Intent(currentActivity,Select_tripActivity.class);
				currentActivity.startActivity(tripsWithDate);
			}else
			{
				 Toast.makeText(getContext(),  "Invalid Login ", Toast.LENGTH_SHORT).show();
				 TextView errorText= (TextView) currentActivity.findViewById(R.id.errorLabel);
				 errorText.setText("Invalid Login");
				 
			} 
			}catch (JSONException e) {
				// TODO Auto-generated catch block
				 Toast.makeText(getContext(),  " Error "+e, Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		
		
	 
		 
		
	}
	
	
	private void logoutAction(JSONObject obj)
	{
		 
			try {
			
			
			
			
			if(obj.getString("ACTION").equalsIgnoreCase("login")&&obj.getString("result").equalsIgnoreCase("true"))
			{
				try {
					SharedPreferences pr = currentActivity.getSharedPreferences("abc",Context.MODE_PRIVATE);
					pr.edit().remove("username");
					pr.edit().remove("password");
					
					pr.edit().commit();
				}catch (Exception e) {
					Log.d("***", "unable to delete user name and password ");
					Log.d("***","Error :"+ e);
					 
				}
			Intent home = new Intent(currentActivity,LoginActivity.class);
			currentActivity.startActivity(home);
			}else
			{
				 Toast.makeText(getContext(),  "Invalid Login ", Toast.LENGTH_SHORT).show();
				 
			} 
			}catch (JSONException e) {
				// TODO Auto-generated catch block
				 Toast.makeText(getContext(),  " Error "+e, Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		
		
		 
		 
		
	}

	@Override
	public void update(boolean result, JSONObject obj) {
		onPostExecute(result,obj);
		
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}


	public void downloadBasicSettings() {
		// TODO Auto-generated method stub
		try{
			
			
			currentActivity=dto.getCurrentActivity();
		JSONObject obj= new JSONObject();
		obj.put("ACTION", "downloadSettings");
		 
		obj.put("IMEI", ""+ mobile.getIMEINumber());
		mobile.setPath(CometSettings.ServerAddress);
		mobile.	setInObj(obj);
		 
		mobile.execute( );
		
		 
		 
		}catch (Exception e) {
			 Log.d("***", "Error in downloadBasic Settings");
			 Toast.makeText(getContext(),  " Error "+e, Toast.LENGTH_SHORT).show();
		}
	}



	  

}
