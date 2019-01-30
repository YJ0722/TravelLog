package com.example.admin.travellog_ver30.expnese;

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

import com.example.admin.travellog_ver30.R;
import com.example.admin.travellog_ver30.map.ViewerActivity;
import com.example.admin.travellog_ver30._adapter.ExpenseAdapter;
import com.example.admin.travellog_ver30._models.Coord;
import com.example.admin.travellog_ver30._models.ExpenseHistory;
import com.example.admin.travellog_ver30._models.ExpenseHistoryDAO;
import com.example.admin.travellog_ver30._models.TrackingHistoryDAO;

import java.util.ArrayList;


public class ExpenseFragment extends Fragment implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    private ArrayList<Coord> recordedPath;
    private int totalCost = 0;
    private int incomeCost = 0;
    private int expenseCost = 0;
    private ExpenseHistory data;
    private TrackingHistoryDAO trackingHistoryDAO;
    private ExpenseHistoryDAO expenseHistoryDAO;
    private TextView eNotifyTextView;
    private ListView eListView;
    private ExpenseAdapter eAdapter;
    Cursor cursor;

    private TextView mTotalCostText, mIncomeText, mExpenseText;

    String pathJson;
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

        Log.d("ExpenseFragment", "onCreateView() 시작");
        return inflater.inflate(R.layout.fragment_expense, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("ExpenseFragment " , " onActivityCreated() 실행");
        Log.d(" 경비창에서 travelNo 확인" , String.valueOf(getTravelNo()));

        trackingHistoryDAO = new TrackingHistoryDAO(getActivity());
        expenseHistoryDAO = new ExpenseHistoryDAO(getActivity());
        initViews();


        cursor = expenseHistoryDAO.findByTravelNo(getTravelNo());
        System.out.println(cursor.getCount());
        if (eAdapter == null) {
            eAdapter = new ExpenseAdapter(getActivity(), cursor);

        } else {
            eAdapter.setIncomeCost(0);
            eAdapter.setExpenseCost(0);
            eAdapter.setTotalCost(0);
            eAdapter.swapCursor(cursor);

        }
        eListView.setAdapter(eAdapter);

        if (cursor == null) {
            eNotifyTextView.setVisibility(View.VISIBLE);
        } else {
            eNotifyTextView.setVisibility(View.GONE);

        }

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d("ExpenseFragment", "onResume() 시작");

        eAdapter.swapCursor(cursor);

        eAdapter.setCostView(mIncomeText, mExpenseText, mTotalCostText);

    }

    // TODO: 20181018
    public void initViews() {

        eNotifyTextView = (TextView) getActivity().findViewById(R.id.expense_notify_ev);
        mTotalCostText = (TextView) getActivity().findViewById(R.id.totalCostText);
        mIncomeText = (TextView) getActivity().findViewById(R.id.incomeText);
        mExpenseText = (TextView) getActivity().findViewById(R.id.expenseText);

        eListView = (ListView) getActivity().findViewById(R.id.expense_lv);
        eListView.setOnItemLongClickListener(this);
        eListView.setOnItemClickListener(this);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("ExpenseFragment", "onViewCreated() 시작");
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
        Log.d("삭제할 경비 id", String.valueOf((int)id));

        int log = eAdapter.get(position).getLog_no();
        Log.d("----------------", "----------------");
        Log.d("여행: " + travelNo, "로그: " + log);
        Log.d("----------------", "----------------");
        Cursor cursorTH = trackingHistoryDAO.findByLogNo(travelNo, log);
        cursorTH.moveToNext();
        int receivedTN = cursorTH.getInt(cursorTH.getColumnIndex("travel_no"));
        int receivedLN = cursorTH.getInt(cursorTH.getColumnIndex("_id"));
        Log.d("불러온 정보----- 여행: " + String.valueOf(receivedTN), "로그: " + String.valueOf(receivedLN));

        new AlertDialog.Builder(getActivity())
                .setTitle("기록 삭제")
                .setMessage("기록을 삭제하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        expenseHistoryDAO.delete((int) id);
                        eAdapter.setIncomeCost(0);
                        eAdapter.setExpenseCost(0);
                        eAdapter.setTotalCost(0);
                        eAdapter.swapCursor(expenseHistoryDAO.findByTravelNo(getTravelNo()));
                        mIncomeText.setText(String.valueOf(eAdapter.getIncomeCost()));
                        mExpenseText.setText(String.valueOf(eAdapter.getExpenseCost()));
                        mTotalCostText.setText(String.valueOf(eAdapter.getTotalCost()));
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
        data = eAdapter.get(position);
        Log.d("클릭 아이템 확인 : " , data.toString());

        String pathJson = makePathJson(data);

        Intent intent = new Intent(getActivity(), ViewerActivity.class);
        intent.putExtra("travelNo", travelNo);
        intent.putExtra("logNo", data.getLog_no());
        intent.putExtra("expenseNo", data.getId());
        intent.putExtra("json", pathJson);
        int markerFlag = 1;
        intent.putExtra("indexMarker", markerFlag);
        startActivity(intent);
    }

    public String makePathJson(ExpenseHistory expenseData) {

        recordedPath = new ArrayList<>();

        Log.d("검색 여행 번호", String.valueOf(expenseData.getTravel_no()));
        Log.d("검색 로그 번호 ", String.valueOf(expenseData.getLog_no()));
        Cursor detailECursor = trackingHistoryDAO.findByLogNo(expenseData.getTravel_no(), expenseData.getLog_no());
        Log.d("개수 : ", String.valueOf(cursor.getCount()));
        while (detailECursor.moveToNext()) {
            pathJson = detailECursor.getString(detailECursor.getColumnIndex("pathJson"));
    }

        Log.d("pathJson!!!!!!!!!!!!", pathJson);
        return pathJson;
    }

}
