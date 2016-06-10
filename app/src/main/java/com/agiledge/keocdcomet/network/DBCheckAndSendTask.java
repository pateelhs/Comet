package com.agiledge.keocdcomet.network;

import org.json.JSONObject;

import android.content.Context;

import com.agiledge.keocdcomet.gss.common.CometSettings;
import com.agiledge.keocdcomet.tasks.MobileSender;

public class DBCheckAndSendTask   implements Listener {

	Listener listener;
	MobileSender mobile;
	public DBCheckAndSendTask(Context contextParam,Listener listenerParam) {
		mobile= new MobileSender(contextParam, DBCheckAndSendTask.this);
		listener=listenerParam;
		// TODO Auto-generated constructor stub
	}

	public void send(JSONObject obj)
	{
		mobile.setPath(CometSettings.ServerAddress);
		mobile.setInObj(obj);
		mobile.execute();
	}

 

	@Override
	public void update(boolean result, JSONObject obj) {
		// TODO Auto-generated method stub
		listener.update(result,  obj);
		
	}
}
