package com.agiledge.keocdcomet.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBConnect extends SQLiteOpenHelper {
	final public String TABLE1="request_table";
	final public String TABLE2="priority_request_table";
final String CREATE_TABLE1="CREATE TABLE "+TABLE1+" ( id integer PRIMARY KEY AUTOINCREMENT, json varchar(200), nuance varchar(60), status varchar(5))";
final String CREATE_TABLE2="CREATE TABLE  "+TABLE2+"  ( id integer PRIMARY KEY AUTOINCREMENT, json varchar(200), nuance varchar(60), status varchar(5))";

final String DROP_TABLE1="DROP TABLE IF EXISTS "+TABLE1;
final String DROP_TABLE2="DROP TABLE IF EXISTS "+TABLE2;

	public DBConnect(Context context ) {
		super(context, "jackdb.db", null, 3);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		
		
		 
		database.execSQL(CREATE_TABLE1);
		 
		 database.execSQL(CREATE_TABLE2);
		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int currentVersion) {
		// TODO Auto-generated method stub
		
		database.execSQL(DROP_TABLE1);
		 
		 database.execSQL(DROP_TABLE2);
		 onCreate(database);
		
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

		database.execSQL(DROP_TABLE1);
		 
		 database.execSQL(DROP_TABLE2);
		 onCreate(database);
	}
	
	

}
