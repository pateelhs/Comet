package com.agiledge.keocdcomet.db;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class JSONDBAdapter    {
	final String GET_DATA_QUERY1="SELECT * FROM request_table  order by id";
	final String GET_DATA_QUERY2="SELECT * FROM priority_request_table  order by id";
	final String GET_FRESH_DATA_QUERY1="SELECT * FROM request_table where status is null or status !='send'  order by id";
	final String GET_FRESH_DATA_QUERY2="SELECT * FROM priority_request_table where  status is null or status !='send'  order by id";
 
	DBConnect db;
	public JSONDBAdapter(Context context) {
		db=new DBConnect(context);
		 
	}
	
	public long insertJSON(JSONObject data) throws JSONException
	{
		long returnValue = 0;
		try{
	 SQLiteDatabase database=	db.getWritableDatabase();
	 ContentValues value = new ContentValues();
	 Log.d("***","Inserting: NUANCE:" +data.getString("NUANCE"));
	 value.put("json", data.toString());
	 value.put("nuance", data.getString("NUANCE") );
	 returnValue= database.insert("request_table", null, value);
	 database.close();
	
		}
		catch(Exception e)
		{
			System.out.println("error $%^*" +  e);
		}
		
		 return returnValue;
	
	}
	
	public long deleteAll()
	{

		  SQLiteDatabase database = db.getWritableDatabase();
		  long returnInt=-1;
		   database.execSQL("delete from " + db.TABLE1 );
		   
		  database.execSQL("delete from " + db.TABLE2 );
		  database.close();
		  return 2;
	}
	
	public int delete1(String nuance)
	{

		  SQLiteDatabase database = db.getWritableDatabase();
		  int returnInt=-1;
		    Log.d("***","Deleteing entry with NUANCE:"+ nuance);
		   
		 returnInt= database.delete(db.TABLE1, " nuance=?", new String[] { nuance });
		 
		  database.close();
		  return returnInt;
	}
	public int delete2(String nuance)
	{

		  SQLiteDatabase database = db.getWritableDatabase();
		  int returnInt=-1;
		    
		   
		 returnInt= database.delete(db.TABLE2, " nuance=?", new String[] { nuance });
		  database.close();
		  return returnInt;
	}
	
	public JSONObject getFirstData1() throws JSONException
	{
		SQLiteDatabase database = db.getReadableDatabase();
		JSONObject returnData=null;
		 String nuance="";
		Cursor cursor=null;
		try{
			Log.d("***", "Inside getData");
		 cursor=database.rawQuery(GET_FRESH_DATA_QUERY1, null);
		if(cursor.moveToFirst())
		{
		 		JSONObject jobj= new JSONObject(cursor.getString(1) );
		 		
		 		nuance=cursor.getString(2);
				jobj.put("NUANCE", cursor.getString(2));
				Log.d("***", "Naunce :"+ cursor.getString(2));
				Log.d("***","Status: "+cursor.getString(3));
				returnData= jobj;
		} 
		
		 
		database.close();
		
		database=db.getWritableDatabase();
		 ContentValues values = new ContentValues();
		    values.put("status", "send");
		database.update(db.TABLE1, values, " nuance=?", new String[]{nuance});
		}
		catch (JSONException e) {
				throw e;
		}
		catch (Exception e) {
			// TODO: handle exception
			Log.d("ERRor",""+ e);
		}
		finally{
			cursor.close();
			database.close();
		}
		return returnData;
	}
	public JSONObject getFirstData2() throws JSONException
	{
		SQLiteDatabase database = db.getReadableDatabase();
		JSONObject returnData=null;
		Cursor cursor=null;
		String nuance="";
		 
		try{
			Log.d("***", "Inside getAllData");
		 cursor=database.rawQuery(GET_FRESH_DATA_QUERY2, null);
		if(cursor.moveToFirst())
		{
		 		JSONObject jobj= new JSONObject(cursor.getString(1) );
		 		nuance=cursor.getString(2);
		 		jobj.put("NUANCE", cursor.getString(2));
		 		Log.d("***", "Naunce :"+ cursor.getString(2));
				Log.d("***","Status: "+cursor.getString(3));
		 		returnData= jobj;
				
			 
		} 
		
		 
		database.close();
		
		database=db.getWritableDatabase();
		 ContentValues values = new ContentValues();
		    values.put("status", "send");
		database.update(db.TABLE2, values, " nuance=?", new String[]{ nuance});
		
		}
		catch (JSONException e) {
				throw e;
		}
		catch (Exception e) {
			// TODO: handle exception
			Log.d("ERRor",""+ e);
		}
		finally{
			cursor.close();
			database.close();
		}
		return returnData;
	}

	public ArrayList<String> getAllData1() throws JSONException
	{
		SQLiteDatabase database = db.getReadableDatabase();
		ArrayList<String > list = new ArrayList<String>();
		Cursor cursor=null;
		
		try{
			Log.d("***", "Inside getAllData");
		 cursor=database.rawQuery(GET_DATA_QUERY1, null);
		if(cursor.moveToFirst())
		{
			do{
				Log.d("***", "Naunce :"+ cursor.getString(2));
				Log.d("***","Status: "+cursor.getString(3));
				JSONObject jobj= new JSONObject(cursor.getString(1) );
				Log.d("***", "value("+ cursor.getPosition()+"):"+ jobj.toString());		
				list.add(jobj.toString());
				
			}while(cursor.moveToNext());
		}
		
		}
		catch (JSONException e) {
				throw e;
		}
		catch (Exception e) {
			// TODO: handle exception
			Log.d("ERRor",""+ e);
		}
		finally{
			cursor.close();
			database.close();
		}
		return list;
	}
	
	public ArrayList<String> getAllData2() throws JSONException
	{
		SQLiteDatabase database = db.getReadableDatabase();
		ArrayList<String > list = new ArrayList<String>();
		Cursor cursor=null;
		try{
			Log.d("***", "Inside getAllData");
		 cursor=database.rawQuery(GET_DATA_QUERY2, null);
		if(cursor.moveToFirst())
		{
			do{
				JSONObject jobj= new JSONObject(cursor.getString(1) );
				Log.d("***", "value("+ cursor.getPosition()+"):"+ jobj.toString());		
				list.add(jobj.toString());
				
			}while(cursor.moveToNext());
		}
		
		}
		catch (JSONException e) {
				throw e;
		}
		catch (Exception e) {
			// TODO: handle exception
			Log.d("ERRor",""+ e);
		}
		finally{
			cursor.close();
			database.close();
		}
		return list;
	}
	
	public void cleanAllStatus1()
	{
		SQLiteDatabase database = db.getWritableDatabase();
		ContentValues value= new ContentValues();
		value.put("status", "");
		database.execSQL("update " + db.TABLE1 + " set status=null ");
		database.close();
	}
	
	public void cleanAllStatus2()
	{
		SQLiteDatabase database = db.getWritableDatabase();
		ContentValues value= new ContentValues();
		value.put("status", "");
		database.execSQL("update " + db.TABLE2 + " set status=null ");
		database.close();
	}
	
	public boolean isEmpty1() 
	
	{
		SQLiteDatabase database = db.getReadableDatabase();
		 boolean status=false;
		Cursor cursor=null;
		try{
			 
		 cursor=database.rawQuery(GET_DATA_QUERY1, null);
		if(cursor.moveToFirst())
		{
			status=true;
		}
		
		}
		 
		catch (Exception e) {
			// TODO: handle exception
			Log.d("ERRor",""+ e);
		}
		finally{
			cursor.close();
			database.close();
		}
		return status;
	}
	
public boolean isEmpty2() 
	
	{
		SQLiteDatabase database = db.getReadableDatabase();
		 boolean status=false;
		Cursor cursor=null;
		try{
			 
		 cursor=database.rawQuery(GET_DATA_QUERY2, null);
		if(cursor.moveToFirst())
		{
			status=true;
		}
		
		}
		 
		catch (Exception e) {
			// TODO: handle exception
			Log.d("ERRor",""+ e);
		}
		finally{
			cursor.close();
			database.close();
		}
		return status;
	}
	
	
	public boolean isAnyEntryInBufferTable1() 
	
	{
		SQLiteDatabase database = db.getReadableDatabase();
		 boolean status=false;
		Cursor cursor=null;
		try{
		 
		 cursor=database.rawQuery(GET_FRESH_DATA_QUERY1, null);
		if(cursor.moveToFirst())
		{
			status=true;
		}
		
		}
		 
		catch (Exception e) {
			// TODO: handle exception
			Log.d("ERRor",""+ e);
		}
		finally{
			cursor.close();
			database.close();
		}
		return status;
	}
	
	 
public boolean isAnyEntryInBufferTable2()  
	
	{
		SQLiteDatabase database = db.getReadableDatabase();
		 boolean status=false;
		Cursor cursor=null;
		try{
	 
		 cursor=database.rawQuery(GET_FRESH_DATA_QUERY2, null);
		if(cursor.moveToFirst())
		{
			status=true;
		}
		
		}
		 
		catch (Exception e) {
			// TODO: handle exception
			Log.d("ERRor",""+ e);
		}
		finally{
			cursor.close();
			database.close();
		}
		return status;
	}
	
}
