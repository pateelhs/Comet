package com.agiledge.keocdcomet.dto;

import android.app.Activity;

public class LoginDto {
	
	private String userName;
	private String password;
	private Activity currentActivity;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Activity getCurrentActivity() {
		return currentActivity;
	}
	public void setCurrentActivity(Activity currentActivity) {
		this.currentActivity = currentActivity;
	}

}
