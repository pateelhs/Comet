package com.agiledge.keocdcomet;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.agiledge.keocdcomet.controller.AppController;
import com.agiledge.keocdcomet.dto.LoginDto;
import com.agiledge.keocdcomet.gss.common.CometSettings;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class TripDownloadActivity extends Activity implements AdapterView.OnItemClickListener {
    ListView listView;
    String password="";
    String otps[];
    private TransparentProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_download);
        listView=(ListView) findViewById(R.id.tripdlist);
        listView.setOnItemClickListener(this);
        pd = new TransparentProgressDialog(this, R.drawable.loading);
        pd.show();
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            password=extras.getString("password");
        }
        fillvalue();

    }

    public void fillvalue(){
        try {
            JSONObject jobj = new JSONObject();
            jobj.put("ACTION", "GET_TRIPS");
            jobj.put("password",password);
            JsonObjectRequest req = new JsonObjectRequest(CometSettings.ServerAddress, jobj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {

                        if (!response.getString("total trips").equalsIgnoreCase("0")) {
                            int limit=Integer.parseInt(response.getString("total trips"));
                            String[] items=new String[limit];
                            otps=new String[limit];
                            for(int i=1;i<=limit;i++){
                                items[i-1]=response.getString("trip_date"+i)+" "+response.getString("trip_time"+i)+" "+response.getString("trip_log"+i);
                                otps[i-1]=response.getString("otp"+i);
                            }
                            listView.setAdapter(new ArrayAdapter<String>(TripDownloadActivity.this,
                                    android.R.layout.simple_list_item_1, items));
                                pd.hide();

                        } else {
                            pd.hide();
                            finish();
                            Toast.makeText(getApplicationContext(), "Wrong Password/No Trips found!", Toast.LENGTH_LONG).show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Error while communicating" + error.getMessage(), Toast.LENGTH_LONG).show();


                }
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(req);
        }catch(Exception e){e.printStackTrace();}
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
        LoginDto dto = new LoginDto();
        dto.setPassword(otps[position]);
        LoginTaskOtpOnly login = new LoginTaskOtpOnly(getApplicationContext());
        login.setCurrentActivity(TripDownloadActivity.this);
        dto.setCurrentActivity(TripDownloadActivity.this);
        login.setStopPassword(password);
        login.setDto(dto);
        login.login();


    }
    private class TransparentProgressDialog extends Dialog {

        private ImageView iv;

        public TransparentProgressDialog(Context context, int resourceIdOfImage) {
            super(context, R.style.TransparentProgressDialog);
            WindowManager.LayoutParams wlmp = getWindow().getAttributes();
            wlmp.gravity = Gravity.CENTER_HORIZONTAL;
            getWindow().setAttributes(wlmp);
            setTitle(null);
            setCancelable(false);
            setOnCancelListener(null);
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            iv = new ImageView(context);
            iv.setImageResource(resourceIdOfImage);
            layout.addView(iv, params);
            addContentView(layout, params);
        }

        @Override
        public void show() {
            super.show();
            RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
            anim.setInterpolator(new LinearInterpolator());
            anim.setRepeatCount(Animation.INFINITE);
            anim.setDuration(1000);
            iv.setAnimation(anim);
            iv.startAnimation(anim);
        }
    }
    }
