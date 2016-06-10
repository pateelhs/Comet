package com.agiledge.keocdcomet.view.adapter;

 
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.agiledge.keocdcomet.R;

public class TripListAdapter  extends ArrayAdapter<String> { 
	  private final Context context;
	  
	  private final String[] description;
	  private final String[] showStatus;
	  private final String[] getInStatus;
	  private final String[] gender;
	  private LayoutInflater inflater; 
	  

	private String[] values;
	  static class ViewContainer {
		public ImageView passangerImage;
		  public TextView txtCount;
		  public TextView txtTitle;
		  public TextView txtDescription;
		  public CheckBox checkControl;
		  
		//public ImageView imageViewGetInStatus;
		  }
	  public TripListAdapter(Context context, String[] values, String[] description, String [] showStatus, String[] getInStatus, String[] gender) {
	    super(context, R.layout.rowlayout, values);
	     inflater = (LayoutInflater) context
    	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    this.context = context;
	    this.values = values;
	    this.description=description;
	    this.showStatus=showStatus;
	    this.getInStatus=getInStatus;
	    this.gender=gender;
	    Log.d("***", "IIIIIIIIIII");
	    
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {

	    //View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
	    View rowView =convertView;
		final ViewContainer viewContainer;
		Log.d("***", " Position  "+position);
	    if(rowView==null)
	    {
	    	
	    	
	    	  viewContainer= new ViewContainer();
	    	  rowView=	inflater.inflate(R.layout.rowlayout, parent,false);
	    	viewContainer.txtTitle= (TextView) rowView.findViewById(R.id.label);
	    	viewContainer.txtDescription= (TextView) rowView.findViewById(R.id.description);
	    	
	    	//viewContainer.imageView= (ImageView) rowView.findViewById(R.id.icon);
	    	viewContainer.passangerImage=(ImageView) rowView.findViewById(R.id.passangerImage);
	    	viewContainer.checkControl=(CheckBox) rowView.findViewById(R.id.checkBox1);
	    	viewContainer.checkControl.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					CheckBox chk = (CheckBox) view;
					Log.d("**~@", "Status before :"+chk.isChecked());
						if(!chk.isChecked()){
							
						chk.setChecked(true);
					}
					else{
						chk.setChecked(false);
					}
						Log.d("**~@", "Status before :"+chk.isChecked());
				}
			});
	    	viewContainer.txtCount=(TextView)rowView.findViewById(R.id.textView1);
	    	//viewContainer.imageViewGetInStatus=(ImageView) rowView.findViewById(R.id.imageViewGetInStatus);
	    	rowView.setTag(viewContainer);
	    	
	    	 
	    	
	    }else
	    {
	    	 
	    	  viewContainer=(ViewContainer)rowView.getTag(	 );
	    }
	    
	   
	    viewContainer.txtTitle.setText(values[position]);
	 //   viewContainer.imageView.setImageResource(R.drawable.go);
	    viewContainer.txtCount.setText(Integer.toString(position+1));
	    viewContainer.txtDescription.setText(description[position]);
	    if(gender[position].equalsIgnoreCase("F"))
	    {
	    	viewContainer.passangerImage.setImageResource(R.drawable.female_avatar);
	    }else if(gender[position].equalsIgnoreCase("M"))	
	    {
	    	viewContainer.passangerImage.setImageResource(R.drawable.male_avatar);
	    }else if(gender[position].equalsIgnoreCase("ESCORT"))	
	    {
	    	viewContainer.passangerImage.setImageResource(R.drawable.security_icon);
	    }
	    if(showStatus[position].equalsIgnoreCase("show"))
	    {
	    	//viewContainer.imageViewGetInStatus.setVisibility(View.VISIBLE);
	    	viewContainer.checkControl.setChecked(true);
	    }else
	    {
	    	//viewContainer.imageViewGetInStatus.setVisibility(View.GONE);
	    	viewContainer.checkControl.setChecked(false);
	    }
	    
	    if(getInStatus[position].equalsIgnoreCase("OUT"))
	    {
	    	//viewContainer.imageViewGetInStatus.setVisibility(View.VISIBLE);
	    	
	    	viewContainer.checkControl.setBackgroundColor(Color.GRAY);
	    } else {
	    	viewContainer.checkControl.setBackgroundColor(Color.TRANSPARENT);
	    }
	     
	 
	    
	    
	    
	     
	     
	    

	    return rowView;
	  }

	public String[] getShowStatus() {
		return showStatus;
	}
}
