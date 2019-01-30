package com.example.admin.travellog_ver30._adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.admin.travellog_ver30.R;
import com.example.admin.travellog_ver30._models.ExpenseHistory;
import com.example.admin.travellog_ver30._models.TrackingHistoryDAO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by a on 2018-10-18.
 */

public class ExpenseAdapter extends CursorAdapter {


    private TrackingHistoryDAO trackingHistoryDAO;
    private ArrayList<Integer> incomeArr, expenseArr, typeArr, costArr;
    TextView incomeText, expenseText, totalText;

    int totalCost = 0;
    int incomeCost = 0;
    int expenseCost = 0;

    public int getIncomeCost() {
        return incomeCost;
    }

    public void setIncomeCost(int incomeCost) {
        this.incomeCost = incomeCost;
    }

    public int getExpenseCost() {
        return expenseCost;
    }

    public void setExpenseCost(int expenseCost) {
        this.expenseCost = expenseCost;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    private SimpleDateFormat mSimpleDateFormat;

    public ExpenseAdapter(Context context, Cursor c) {
        super(context, c, false);
        Log.d("adapter", "생성");
        mSimpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREAN);
    }

    public ExpenseAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mSimpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREAN);
    }

    // 총 수입 + 지출 금액
    /*
    public int calTotalCost(Cursor costCursor) {

        typeArr = new ArrayList<>();
        costArr = new ArrayList<>();

        incomeArr = new ArrayList<>();
        expenseArr = new ArrayList<>();


        while(costCursor.moveToNext()) {
            String title = costCursor.getString(costCursor.getColumnIndex("expenseTitle"));
            int expenseType = costCursor.getInt(costCursor.getColumnIndex("expenseType"));
            int cost = costCursor.getInt(costCursor.getColumnIndex("cost"));

            typeArr.add(expenseType);
            costArr.add(cost);

        }

        for(int i=0 ; i<typeArr.size(); i++) {
            if(typeArr.get(i) == 0) {
                incomeCost += costArr.get(i);
            } else {
                expenseCost += costArr.get(i);
            }
        }

        setIncomeCost(incomeCost);
        setExpenseCost(expenseCost);
        totalCost = incomeCost - expenseCost;

        Log.d("계산 결과 : ", String.valueOf(totalCost));

        return totalCost;
    }
    */

    public ExpenseHistory get(int position) {
        Cursor cursor = getCursor();
        ExpenseHistory expenseDB = null;

        if (cursor.moveToPosition(position)) {
            int expenseNo = cursor.getInt(cursor.getColumnIndex("_id"));
            int travelNo = cursor.getInt(cursor.getColumnIndex("travel_no"));
            int logNo = cursor.getInt(cursor.getColumnIndex("log_no"));
            String expenseTitle = cursor.getString(cursor.getColumnIndex("expenseTitle"));
            int expenseType = cursor.getInt(cursor.getColumnIndex("expenseType"));
            int cost = cursor.getInt(cursor.getColumnIndex("cost"));
            double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
            double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
            long date = cursor.getLong(cursor.getColumnIndex("date"));
            expenseDB = new ExpenseHistory(expenseNo, travelNo, logNo, expenseTitle, expenseType, cost, latitude, longitude, date);
        }

        return expenseDB;
    }

    public ArrayList<Integer> deleteENo(Cursor eCursor) {
        ArrayList<Integer> noList = new ArrayList<>();

        Log.d("adapter 커서 개수 확인", String.valueOf(eCursor.getCount()));
        int count=0;

        while (eCursor.moveToNext()) {
            count++;
            Log.d("커서 반복 확인", String.valueOf(count));
            int id = eCursor.getInt(eCursor.getColumnIndex("_id"));
            noList.add(id);
        }

        for(int i=0; i<noList.size(); i++) {
            Log.d("삭제할 id 리스트", String.valueOf(noList.get(i)));
        }


        return noList;
    }

    private static class ViewHolder {
        TextView mExpenseNo, mExpenseTitle, mExpenseDate, mExpenseLogName, mExpenseCost, mColorIndex;

        ViewHolder(View itemView) {
            mExpenseNo = (TextView) itemView.findViewById(R.id.expenseNoTextView);
            mExpenseTitle = (TextView) itemView.findViewById(R.id.expenseTitle);
            mExpenseDate = (TextView) itemView.findViewById(R.id.item_date_ev);
            mExpenseLogName = (TextView) itemView.findViewById(R.id.expense_logName_ev);
            mExpenseCost = (TextView) itemView.findViewById(R.id.expense_cost_ev);
            mColorIndex = (TextView) itemView.findViewById(R.id.colorIndex);


        }
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.d("ㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴ", "newView() 실행 시작");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense_history, parent, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Log.d("ㅂㅂㅂㅂㅂㅂㅂㅂㅂㅂㅂ", "bindView() 실행 시작");
        ViewHolder holder = (ViewHolder) view.getTag();
        String expenseTitle = cursor.getString(cursor.getColumnIndex("expenseTitle"));
        int expenseNo = cursor.getPosition() + 1;
        int logNo = cursor.getInt(cursor.getColumnIndex("log_no"));
        int expenseType = cursor.getInt(cursor.getColumnIndex("expenseType"));
        int cost = cursor.getInt(cursor.getColumnIndex("cost"));
        long date = cursor.getLong(cursor.getColumnIndex("date"));


        holder.mExpenseNo.setText(String.valueOf(expenseNo));
        holder.mExpenseTitle.setText(expenseTitle);

        trackingHistoryDAO = new TrackingHistoryDAO(context);
        Cursor thCursor = trackingHistoryDAO.findByOnlyLogNo(logNo);
        thCursor.moveToNext();
        String logName = thCursor.getString(thCursor.getColumnIndex("logTitle"));
        Log.d("로그 이름 체크", logName);
        holder.mExpenseLogName.setText(logName);

        if(expenseType == 0) {
            //holder.mExpenseType.setText("수입");
            holder.mColorIndex.setBackgroundColor(Color.parseColor("#336dcc"));
            incomeCost += cost;
            totalCost += cost;
        } else {
            //holder.mExpenseType.setText("지출");
            holder.mColorIndex.setBackgroundColor(Color.parseColor("#d86475"));
            expenseCost -= cost;
            totalCost -= cost;
        }

        holder.mExpenseCost.setText(String.valueOf(cost) + " 원");
        holder.mExpenseDate.setText(String.valueOf(mSimpleDateFormat.format(new Date(date))));

        if(cursor.isLast() == true) {
            Log.d("!!!", "income : " + String.valueOf(incomeCost) + " , expense : " + String.valueOf(expenseCost) + " , total : " + String.valueOf(totalCost));
            incomeText.setText(String.valueOf(incomeCost));
            expenseText.setText(String.valueOf(expenseCost));
            totalText.setText(String.valueOf(totalCost));
        }
    }

    public void setCostView(TextView income, TextView expense, TextView total) {

        incomeText = income;
        expenseText = expense;
        totalText = total;

    }
}
