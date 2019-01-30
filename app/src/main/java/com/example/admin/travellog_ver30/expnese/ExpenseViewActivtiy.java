package com.example.admin.travellog_ver30.expnese;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.admin.travellog_ver30.R;
import com.example.admin.travellog_ver30._models.ExpenseHistoryDAO;

import static com.example.admin.travellog_ver30.R.id.memoContentView;
import static com.example.admin.travellog_ver30.R.id.memoTitleView;
import static com.example.admin.travellog_ver30.R.id.memoViewBtn;

public class ExpenseViewActivtiy extends Activity {

    private final String startTitle = "시작";
    private final String endTitle = "종료";
    private final String startContent = "로그 시작 지점입니다";
    private final String endContent = "로그 종료 지점입니다";
    Intent intent;
    private ExpenseHistoryDAO expenseHistoryDAO;

    private TextView expenseTitleView, expenseContentView;
    private Button expenseViewBtn, expenseDeleteBtn;

    private int expenseId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_memo_view);

        initView();

        intent = getIntent();
        expenseId = intent.getIntExtra("expenseId", 0);

        // 시작 종료 마커 말풍선 터치했을 경우
        if(expenseId == 0) {
            setStartEndData();
        }
        // 경비 정보 마커 말풍선 터치했을 경우
        else {
            getExpenseDetailData(expenseId);
        }
    }

    private void initView() {
        expenseTitleView = (TextView) findViewById(memoTitleView);
        expenseContentView = (TextView) findViewById(memoContentView);
        expenseViewBtn = (Button) findViewById(memoViewBtn);

        expenseDeleteBtn.setOnClickListener(expenseDeleteBtnOnClickListener);
        expenseViewBtn.setOnClickListener(expenseViewBtnOnClickListener);
    }

    private void setStartEndData() {
        String markerTitle = intent.getStringExtra("markerTitle");
        Log.d("markerTitle 확인", markerTitle);
        if(markerTitle.compareTo("시작") == 0) {
            Log.d("제목 시작", "들어옴");
            expenseTitleView.setText(startTitle);
            expenseContentView.setText(startContent);
        } else if(markerTitle.compareTo("종료") == 0) {
            Log.d("제목 종료", "들어옴");
            expenseTitleView.setText(endTitle);
            expenseContentView.setText(endContent);
        } else {
            expenseTitleView.setText("-");
            expenseContentView.setText("-");
        }
    }
    private void getExpenseDetailData(int expenseNo) {
        expenseHistoryDAO = new ExpenseHistoryDAO(getApplicationContext());

        Cursor cursor = expenseHistoryDAO.findByExpenseNo(expenseNo);
        cursor.moveToNext();

        /////////////////////////////////
        String expenseTitle = cursor.getString(cursor.getColumnIndex("expenseTitle"));
        int expenseType = cursor.getInt(cursor.getColumnIndex("expenseType"));
        int cost = cursor.getInt(cursor.getColumnIndex("cost"));
        String type;
        if(expenseType == 0) {
            type = "+";
        } else if(expenseType == 1) {
            type = "-";
        } else {
            type="-";
        }
        String expenseContent = type + String.valueOf(cost);

        expenseTitleView.setText(String.valueOf(expenseTitle));
        expenseContentView.setText(String.valueOf(expenseContent));

    }

    // 확인 버튼 클릭 리스너
    private View.OnClickListener expenseViewBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //액티비티(팝업) 닫기
            finish();
        }
    };

    // 경비 삭제 버튼 클릭 리스너
    private View.OnClickListener expenseDeleteBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("확인용", "확인확인");

            expenseHistoryDAO.delete((int) expenseId);
//            Toast.makeText(getApplicationContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show();

            Intent expenseViewIntent = new Intent();
            setResult(RESULT_OK, expenseViewIntent);
            //액티비티(팝업) 닫기
            finish();
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

}
