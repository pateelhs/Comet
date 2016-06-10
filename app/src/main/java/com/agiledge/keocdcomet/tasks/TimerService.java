package com.agiledge.keocdcomet.tasks;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TimerService extends Service {

	private long time=0; 
	private long startTime;
	private Timer timer;
	private TimerTask timerTask;
	private String id;
	private Intent broadCastIntent;
	public static final String BROADCAST_ACTION="timerBroadCast";
	@Override
	public void onCreate() {
	 
		super.onCreate();
		broadCastIntent= new Intent(BROADCAST_ACTION);
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		 time=	intent.getLongExtra("timeElapsed", 0);
		 id =  intent.getStringExtra("timerId");

		 Log.d("***", "Received Time Elapsed to TimerService :"+time + " id : " + id);
		// startTime=Calendar.getInstance().getTimeInMillis();
		// startTime=+ startTime -time;
	/*	 if(timer==null) {
		 timer = new Timer();
		  
		 }  
	*/	 	 timer = new Timer();
		/* if(timerTask==null) {
			 timerTask = new TimerTask() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						time+=1000L;
						 
						broadCastIntent.putExtra("timeElapsed",   time);
						broadCastIntent.putExtra("timerId",   id); 
						sendBroadcast(broadCastIntent);
					
						
					}
				};
		 }
*/		 
		/*timer.scheduleAtFixedRate( timerTask
				,0,1000L);*/
		timer.scheduleAtFixedRate( new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				time+=1000L;
				broadCastIntent.putExtra("timeElapsed",   time);
				broadCastIntent.putExtra("timerId",   id); 
				sendBroadcast(broadCastIntent);
			
				
			}
		}
				,0,1000L);
		
		
		return START_NOT_STICKY;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	 	Log.d("***", "Timer Service onDestroy");
		try {
			timer.cancel();
		 stopService(broadCastIntent);
		 
		} catch (Exception e) {
			 	Log.d("***", "Error in TimerService onDestroy :"+e);
		}
		
	}
	
	

}
