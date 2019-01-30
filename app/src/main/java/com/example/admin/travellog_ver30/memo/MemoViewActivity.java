package com.example.admin.travellog_ver30.memo;

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
import com.example.admin.travellog_ver30._models.MemoDAO;

public class MemoViewActivity extends Activity {

    private final String startTitle = "시작";
    private final String endTitle = "종료";
    private final String startContent = "로그 시작 지점입니다";
    private final String endContent = "로그 종료 지점입니다";
    private Intent intent;
    private MemoDAO memoDAO;

    private TextView memoTitleView, memoContentView;
    private Button memoDeleteBtn, memoViewBtn;

    private int memoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_memo_view);

        initView();

        intent = getIntent();
        memoId = intent.getIntExtra("memoId", 0);
        Log.d("00000000000", String.valueOf(memoId));

        // 시작 종료 마커 정보일 경우
        if(memoId == 0) {
            memoDeleteBtn.setVisibility(View.GONE);
            setStartEndData();
        }
        // 메모 정보 마커 말풍선 정보일 경우
        else {
            getMemoDetailData(memoId);
        }
    }

    private void initView() {
        memoTitleView = (TextView) findViewById(R.id.memoTitleView);
        memoContentView = (TextView) findViewById(R.id.memoContentView);
        memoViewBtn = (Button) findViewById(R.id.memoViewBtn);
        memoDeleteBtn = (Button) findViewById(R.id.memoDeleteBtn);

        memoDeleteBtn.setOnClickListener(memoDeleteBtnOnClickListener);
        memoViewBtn.setOnClickListener(memoViewBtnOnClickListener);
    }

    private void setStartEndData() {
        String markerTitle = intent.getStringExtra("markerTitle");
        Log.d("markerTitle 확인", markerTitle);
        if(markerTitle.compareTo("시작") == 0) {
            Log.d("제목 시작", "들어옴");
            memoTitleView.setText(startTitle);
            memoContentView.setText(startContent);
        } else if(markerTitle.compareTo("종료") == 0) {
            Log.d("제목 종료", "들어옴");
            memoTitleView.setText(endTitle);
            memoContentView.setText(endContent);
        } else {
            memoTitleView.setText("-");
            memoContentView.setText("-");
        }
    }

    private void getMemoDetailData(int memoNo) {
        memoDAO = new MemoDAO(getApplicationContext());

        Cursor cursor = memoDAO.findByMemoNo(memoNo);
        cursor.moveToNext();

        String memoTitle = cursor.getString(cursor.getColumnIndex("memoTitle"));
        String memoContent = cursor.getString(cursor.getColumnIndex("memoContent"));

        memoTitleView.setText(String.valueOf(memoTitle));
        memoContentView.setText(String.valueOf(memoContent));

    }

    // 확인 버튼 클릭 리스너
    private View.OnClickListener memoViewBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //액티비티(팝업) 닫기
            finish();
        }
    };

    // 메모 삭제 버튼 클릭 리스너
    private View.OnClickListener memoDeleteBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("확인용", "확인확인");

            memoDAO.delete((int) memoId);
//            Toast.makeText(getApplicationContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show();

            Intent memoViewIntent = new Intent();
            setResult(RESULT_OK, memoViewIntent);
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
