package com.example.admin.travellog_ver30;
/**
 * Created by eee30 on 2018-10-14.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import com.example.admin.travellog_ver30.travel.TravelListActivity;


public class ManualActivity extends Activity{
    Button startBtn;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        ManualSlideActivity sv = new ManualSlideActivity(this);
        View v1 = View.inflate(this, R.layout.t1, null);
        View v2 = View.inflate(this, R.layout.t2, null);
        View v3 = View.inflate(this, R.layout.t3, null);
        View v4 = View.inflate(this, R.layout.t4, null);
        View v5 = View.inflate(this, R.layout.t5, null);
        sv.addView(v1);
        sv.addView(v2);
        sv.addView(v3);
        sv.addView(v4);
        sv.addView(v5);
        setContentView(sv);



        startBtn = (Button) findViewById(R.id.start);
    }

    public void clickedStartBtn(View v) {
        Intent intent = new Intent(this, TravelListActivity.class);
        startActivity(intent);
        finish();
    }

}