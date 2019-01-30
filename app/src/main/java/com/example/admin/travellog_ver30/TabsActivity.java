package com.example.admin.travellog_ver30;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.admin.travellog_ver30._adapter.ExpenseAdapter;
import com.example.admin.travellog_ver30._adapter.TrackingHistoryAdapter;
import com.example.admin.travellog_ver30.expnese.ExpenseFragment;
import com.example.admin.travellog_ver30.log.LogListFragment;
import com.example.admin.travellog_ver30._models.Travel;

public class TabsActivity extends AppCompatActivity  {

    TextView travelTitle;
    ExpenseFragment expenseFragment;
    LogListFragment logListFragment;
    ExpenseAdapter eAdapter;
    TrackingHistoryAdapter tAdapter;


    public void refresh() {
        eAdapter.notifyDataSetChanged();
        tAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        Intent intent = getIntent();

        Travel travel = intent.getParcelableExtra("data");
        String name = travel.getTravelTitle();

        travelTitle = (TextView) findViewById(R.id.travelTitle);
        travelTitle.setText(name);

        logListFragment = new LogListFragment();
        /////////////////////////////////
        logListFragment.setTravelNo(travel.getTravel_no());
        /////////////////////////////////
        expenseFragment = new ExpenseFragment();
        expenseFragment.setTravelNo(travel.getTravel_no());

        // 잠시 주석 처리....
        getSupportFragmentManager().beginTransaction().replace(R.id.container, logListFragment).commit();
        //getSupportFragmentManager().beginTransaction().replace(R.id.container, expenseFragment).commit();

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("로그"));
        tabs.addTab(tabs.newTab().setText("경비"));

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                Fragment selected = null;
                if(position == 0) {
                    selected = logListFragment;

                } else if(position == 1) {
                    selected = expenseFragment;
                }

                // todo: show()/hide() 사용해서 나중에 수정하기
                getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
