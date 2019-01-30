package com.example.admin.travellog_ver30.travel;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.admin.travellog_ver30.ManualActivity;
import com.example.admin.travellog_ver30.R;
import com.example.admin.travellog_ver30.TabsActivity;
import com.example.admin.travellog_ver30._adapter.TravelAdapter;
import com.example.admin.travellog_ver30._models.Travel;
import com.example.admin.travellog_ver30._models.TravelDAO;


public class TravelListActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    private TravelDAO travelDAO;
    private TextView travelNotifyTextView;
    private ListView travelListView;
    private TravelAdapter travelAdapter;
    public SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        checkFirstRun();

        setContentView(R.layout.activity_travel_list);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("TravelListActivity,", " onResume() 실행 시작!");
        // Travel 전체 목록 가져오기
        travelDAO = new TravelDAO(getApplicationContext());
        Cursor cursor = travelDAO.findAll();

        if (travelAdapter == null) {
            travelAdapter = new TravelAdapter(getApplicationContext(), cursor);
            travelListView.setAdapter(travelAdapter);
        } else {
            travelAdapter.swapCursor(cursor);
        }

        if (cursor == null) {
            travelNotifyTextView.setVisibility(View.VISIBLE);
        } else {
            travelNotifyTextView.setVisibility(View.GONE);
        }
    }

    private void initViews() {
        travelNotifyTextView = (TextView) findViewById(R.id.travel_notify_tv);

        travelListView = (ListView) findViewById(R.id.travel_lv);
        travelListView.setOnItemLongClickListener(this);
        travelListView.setOnItemClickListener(this);
        findViewById(R.id.travel_fab).setOnClickListener(mOnTrackingStartButtonClickListener);
    }

    private View.OnClickListener mOnTrackingStartButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            startActivityForResult(new Intent(TravelListActivity.this, TravelAddActivity.class), 1);
        }
    };

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
        new AlertDialog.Builder(TravelListActivity.this)
                .setTitle("기록 삭제")
                .setMessage("기록을 삭제하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        travelDAO.delete((int) id);
                        travelAdapter.swapCursor(travelDAO.findAll());

                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Travel data = travelAdapter.get(position);
        Log.d("클릭 데이터 확인 : " , data.toString());
        startActivity(new Intent(TravelListActivity.this, TabsActivity.class).putExtra("data", (Parcelable) data));
    }

    public void checkFirstRun(){
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);
        if(isFirstRun){
            Intent newIntent = new Intent(TravelListActivity.this, ManualActivity.class);

            startActivity(newIntent);

            prefs.edit().putBoolean("isFirstRun", false).apply();
        }

    }

}
