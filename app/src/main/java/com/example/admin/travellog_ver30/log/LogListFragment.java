package com.example.admin.travellog_ver30.log;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.admin.travellog_ver30.map.MapsActivity;
import com.example.admin.travellog_ver30.R;
import com.example.admin.travellog_ver30.map.ViewerActivity;
import com.example.admin.travellog_ver30._adapter.ExpenseAdapter;
import com.example.admin.travellog_ver30._adapter.TrackingHistoryAdapter;
import com.example.admin.travellog_ver30.expnese.ExpenseFragment;
import com.example.admin.travellog_ver30._models.ExpenseHistoryDAO;
import com.example.admin.travellog_ver30._models.TrackingHistory;
import com.example.admin.travellog_ver30._models.TrackingHistoryDAO;

import java.util.ArrayList;

public class LogListFragment extends Fragment implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    private TrackingHistoryDAO mTrackingHistoryDAO;
    private ExpenseHistoryDAO mExpenseHistoryDAO;
    private TextView mNotifyTextView;
    private ListView mListView;
    private TrackingHistoryAdapter mAdapter;
    private ExpenseAdapter eAdapter;
    Cursor cursor;

    ArrayList<Integer> deleteNoList;
    TrackingHistory selectHistory;


    private int travelNo;

    public int getTravelNo() {
        return travelNo;
    }

    public void setTravelNo(int travelNo) {
        this.travelNo = travelNo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_log_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("LogListFragment " , " onActivityCreated() 실행");
        Log.d("travelNo 데이터 확인", String.valueOf(getTravelNo()));

        initViews();

    }

    @Override
    public void onResume() {
        super.onResume();

        mTrackingHistoryDAO = new TrackingHistoryDAO(getActivity());
        mExpenseHistoryDAO = new ExpenseHistoryDAO(getActivity());
        cursor = mTrackingHistoryDAO.findByTravelNo(getTravelNo());
        System.out.println(cursor.getCount());
        if (mAdapter == null) {
            mAdapter = new TrackingHistoryAdapter(getActivity(), cursor);
        } else {
            mAdapter.swapCursor(cursor);
        }
        mListView.setAdapter(mAdapter);


        if (cursor == null) {
            mNotifyTextView.setVisibility(View.VISIBLE);
        } else {
            mNotifyTextView.setVisibility(View.GONE);
        }
    }

    private void initViews() {
        mNotifyTextView = (TextView) getActivity().findViewById(R.id.notify_tv);

        mListView = (ListView) getActivity().findViewById(R.id.lv);
        mListView.setOnItemLongClickListener(this);
        mListView.setOnItemClickListener(this);
        getActivity().findViewById(R.id.fab).setOnClickListener(mOnTrackingStartButtonClickListener);
    }

    private View.OnClickListener mOnTrackingStartButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), MapsActivity.class);
            Log.d("LogListF travelNo 인텐트", String.valueOf(getTravelNo()));
            intent.putExtra("travelNo", String.valueOf(getTravelNo()));
            startActivity(intent);
        }
    };

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {

        deleteNoList = new ArrayList<>();
        selectHistory = new TrackingHistory();

        new AlertDialog.Builder(getActivity())
                .setTitle("기록 삭제")
                .setMessage("기록을 삭제하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ////// 경비 같이 삭제!!!
                        selectHistory = mAdapter.get(position);
                        Log.d("삭제할 로그 정보", selectHistory.toString());
                        int logNoForDelete = selectHistory.getLogNo();

                        Log.d("%%% 삭제할 로그 번호 %%%", String.valueOf(logNoForDelete));

                        Cursor c = mExpenseHistoryDAO.deleteExpenseDataId(logNoForDelete);
                        Log.d("커서 사이즈", String.valueOf(c.getCount()));
                        eAdapter = new ExpenseAdapter(getActivity(), c);
                        deleteNoList = eAdapter.deleteENo(c);

                        Log.d("size : " , String.valueOf(deleteNoList.size()));
                        for(int i=0; i<deleteNoList.size(); i++) {
                            Log.d("[" + i + "]", String.valueOf(deleteNoList.get(i)));
                        }

                        for(int id : deleteNoList) {
                            Log.d("경비 삭제!!!", String.valueOf(id));
                            mExpenseHistoryDAO.delete(id);
                        }


                        mTrackingHistoryDAO.delete((int) id);
//                        Cursor c
                        mAdapter.swapCursor(mTrackingHistoryDAO.findByTravelNo(getTravelNo()));
//                        mAdapter.\
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
        return true;
    }

    public void updateExpenseInLogList(ExpenseFragment expenseFragment) {
        expenseFragment.onResume();
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        TrackingHistory logData = mAdapter.get(position);
        Log.d("#### logData : ", logData.toString());
        Log.d("#### no : " , String.valueOf(logData.getLogNo()));

        Log.d("클릭 아이템 확인 : " , logData.toString());
        Log.d("아이템 경로", logData.getPathJson());

        Intent intent = new Intent(getActivity(), ViewerActivity.class);
        //intent.putExtra("logData", logData);
        intent.putExtra("logNo", logData.getLogNo());
        int markerFlag = 0;
        intent.putExtra("indexMarker", markerFlag);
        intent.putExtra("json", logData.getPathJson());
        startActivity(intent);

    }
}
