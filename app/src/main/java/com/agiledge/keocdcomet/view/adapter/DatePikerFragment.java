package com.agiledge.keocdcomet.view.adapter;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

public class DatePikerFragment   extends DialogFragment
implements DatePickerDialog.OnDateSetListener {
	public int year;
	public int month;
	public int day; 
	Activity currentActivity;

@Override
public Dialog onCreateDialog(Bundle savedInstanceState) {
// Use the current date as the default date in the picker
final Calendar c = Calendar.getInstance();
  year = c.get(Calendar.YEAR);
  month = c.get(Calendar.MONTH);
  day = c.get(Calendar.DAY_OF_MONTH);

// Create a new instance of DatePickerDialog and return it
return new DatePickerDialog(getActivity(), this, year, month, day);
}

public void onDateSet(DatePicker view, int year, int month, int day) {
// Do something with the date chosen by the user
	final Calendar c = Calendar.getInstance();
	  this.year = year;
	  this.month = month;
	  this.day = day;
}
	 

}
