package com.agiledge.keocdcomet.network;

import org.json.JSONObject;

public interface DbLapListener {

	public void  onPostDbExecute(Boolean result, JSONObject obj);
}
