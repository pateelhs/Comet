package com.agiledge.keocdcomet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.agiledge.keocdcomet.dto.LoginDto;
import com.agiledge.keocdcomet.gss.common.CometSettings;
import com.agiledge.keocdcomet.network.Listener;
import com.agiledge.keocdcomet.tasks.MobileSender;

import org.json.JSONObject;

public class LoginTaskOtpOnly implements Listener {

	 
private MobileSender mobile;
private Context context;
	public String trip;
	public String stopPassword;



	public LoginTaskOtpOnly(Context contextParam) {
		mobile=(MobileSender) new MobileSender(contextParam,LoginTaskOtpOnly.this);
		setContext(contextParam);
		
		// TODO Auto-generated constructor stub
	}

	private LoginDto dto= new LoginDto();
	private  TripDownloadActivity currentActivity;
	public Activity getCurrentActivity() {
		return currentActivity;
	}

	public void setCurrentActivity(Activity currentActivity) {
		if(currentActivity==null){
			this.currentActivity=new TripDownloadActivity();
		}else
		this.currentActivity = (TripDownloadActivity) currentActivity;
	}
public void setStopPassword(String stopPassword){
	this.stopPassword=stopPassword;
}

	protected void onPostExecute(Boolean result,JSONObject obj) {
		// TODO Auto-generated method stub

		
		if(result==true&&obj!=null)
		{
			
			try {

				//Toast.makeText(getContext(), obj.toString() , Toast.LENGTH_SHORT).show();

				String responseStatus=obj.getString("result");
				String action=obj.getString("ACTION");

				if(responseStatus.equalsIgnoreCase("TRUE"))
					 {


					if (action.equalsIgnoreCase("gettrip")||action.equalsIgnoreCase("gettripOTP")||action.equalsIgnoreCase("loginOTP") ) {
				//	if (action.equalsIgnoreCase("START_TRIP") ) {
						Log.d("***", "getTripAction ... starts");
						stopSpinner();

						getTripAction(obj);
						Log.d("***", "getTripAction ... ends");
					}
				}  else {
					Log.d("***", "In LoginTaskOtpOnly settings no internet");
					if(currentActivity==null) {
						Log.d("***",  "currentActivity   is null");
					} else {
						try {
						if (action.equalsIgnoreCase("gettrip")||action.equalsIgnoreCase("gettripOTP")||action.equalsIgnoreCase("loginOTP") ) {
					//		if (action.equalsIgnoreCase("START_TRIP") ) {

								stopSpinner();
							getTripAction(obj);
						}
						}catch(Exception e) {}
					 TextView errorText= (TextView) currentActivity.findViewById(R.id.errorLabel);
					 errorText.setText("No Internet/Wrong Password");
					 Log.d("***", "set no internet text");
					}
				}
			} catch ( Exception e) {
				// TODO Auto-generated catch block
				 Toast.makeText(getContext(),  " Error "+e, Toast.LENGTH_SHORT).show();
				 
			}
		}else
		{
			 Toast.makeText(getContext(),  "Invalid Login ", Toast.LENGTH_SHORT).show();
		}
		 
		// Toast.makeText(getContext(),  " Please wait ...", Toast.LENGTH_SHORT).show();
		
	}
	
	public void getTripAction(JSONObject obj) {
		try {

		//	Toast.makeText(getContext(), obj.toString(), Toast.LENGTH_LONG).show();

			Log.d("***", "in getTripAction..");
			if((obj.getString("ACTION").equalsIgnoreCase("loginOTP")||obj.getString("ACTION").equalsIgnoreCase("gettrip"))&&obj.getString("result").equalsIgnoreCase("true"))
			{
				Log.d("***", "in getTripAction..in if ");
				SharedPreferences pr = currentActivity.getSharedPreferences("abc",Context.MODE_PRIVATE);
				Log.d("***", "in getTripAction..in if step 1 ");
				SharedPreferences.Editor editor = pr.edit();
//				String password=pr.getString("password", "");
//				if(password != null && password.equals("") == false) {
//					Log.d("***","Old Password "+ password);
//					editor.remove("password");
//
//				}

				editor.putString("password", dto.getPassword());

				editor.commit();
				Intent intent =new Intent(currentActivity, TripDetailsActivity.class);
				Log.d("***"," "+obj.toString());
				intent.putExtra("trip", obj.toString());
				intent.putExtra("stopPassword",stopPassword);
				currentActivity.startActivity(intent);
				currentActivity.finish();

			} else {
				Log.d("***", "in getTripAction..else ");
				TextView errorText= (TextView) currentActivity.findViewById(R.id.errorLabel);
				errorText.setText("Invalid Login");
			}

		} catch(Exception e) {
			Log.d("***-OTP : ",e.getMessage());
		}
		
		Log.d("***", "in getTripAction..ends ..");
	}
	
	public void login(   )
	{
		try{
	
			LoginOTPOnlyActivity login = new LoginOTPOnlyActivity();
			int versionNumber=login.getVersionCode(getContext());
				
		JSONObject obj= new JSONObject();
		obj.put("ACTION", "LOGINOTP");
		obj.put("password", dto.getPassword());
			// obj.put("ACTION", "START_TRIP");
			//obj.put("password", dto.getPassword());
		obj.put("IMEI", ""+ mobile.getIMEINumber());
		obj.put("VERSION", versionNumber+"");
		mobile.setPath(CometSettings.ServerAddress);
		mobile.	setInObj(obj);

//		ImageButton btn =(ImageButton) currentActivity.findViewById(R.id.loginButton);
		
		loadSpinner();
		 mobile.execute( );
			// next screen
			Toast.makeText(getContext(), "please wait" , Toast.LENGTH_SHORT).show();

			Intent intent =new Intent(currentActivity, TripDetailsActivity.class);

	///	Log.d("***"," "+obj.toString());
	///		//intent.putExtra("trip", obj.toString());
	///		currentActivity.startActivity(intent);
	///	currentActivity.finish();

		//	Toast.makeText(getContext(), "inserted", Toast.LENGTH_LONG).show();
		}catch (Exception e) {
			
			 Toast.makeText(getContext(),  " Error "+e, Toast.LENGTH_SHORT).show();
		}
	}
	/*/public boolean sendversion( int VersionNumber  )
	{
		boolean flag =false;
		try{
			
		JSONObject obj= new JSONObject();
		obj.put("ACTION", "VERSIONCHECK"); 
		obj.put("VERSION", VersionNumber+"");
		obj.put("IMEI", ""+ mobile.getIMEINumber());
		mobile.setPath(CometSettings.ServerAddress);
		mobile.	setInObj(obj);
	//	ImageButton btn =(ImageButton) currentActivity.findViewById(R.id.loginButton);
		
		//loadSpinner();
		 mobile.execute( );
		flag= mobile.getInObj().getBoolean("Status");
		}catch (Exception e) {
			
		//	 Toast.makeText(getContext(),  " Error "+e, Toast.LENGTH_SHORT).show();
		}
		return flag;
	}/*/
	 
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
	
	public void setDto(LoginDto dto) {
		this.dto = dto;
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


	

}
