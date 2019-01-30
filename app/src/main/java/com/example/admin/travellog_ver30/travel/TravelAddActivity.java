package com.example.admin.travellog_ver30.travel;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.admin.travellog_ver30.R;
import com.example.admin.travellog_ver30._models.Travel;
import com.example.admin.travellog_ver30._models.TravelDAO;

public class TravelAddActivity extends AppCompatActivity implements MyDatePickerFragment.OnMyListener {

    Button saveTravelBtn, cancelTravelBtn;
    EditText inputTravelTitle, inputTravelStartDate, inputTravelEndDate;
    String travelTitle;

    private boolean flag;
    private String startDate = null;
    private String endDate = null;
    private Travel travel = new Travel(null, null, null);
    private int year, month, day;

    //chaeeun test
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_travel_add);

        inputTravelTitle = (EditText) findViewById(R.id.inputTravelTitle);
        inputTravelStartDate = (EditText) findViewById(R.id.inputTravelStartDate);
        inputTravelEndDate = (EditText) findViewById(R.id.inputTravelEndDate);

        inputTravelStartDate.setOnClickListener(startDateClickListener);
        inputTravelEndDate.setOnClickListener(endDateClickListener);

        saveTravelBtn = (Button) findViewById(R.id.saveTravelBtn);
        cancelTravelBtn = (Button) findViewById(R.id.cancelTravelBtn);
        saveTravelBtn.setOnClickListener(saveLogBtnOnClickListener);
        cancelTravelBtn.setOnClickListener(cancelLogBtnOnClickListener);

        //데이터 전달하기
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
    }

    public void showDatePicker(View v) {
        DialogFragment newFragment = new MyDatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "date picker");

    }
    // 시작일 입력 클릭 리스너
    private View.OnClickListener startDateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            flag = true;
            //데이터피커 보여주기
            showDatePicker(v);
        }
    };
    // 종료일 입력 클릭 리스너
    private View.OnClickListener endDateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            flag = false;
            //데이터피커 보여주기
            showDatePicker(v);
        }
    };
    // 확인 버튼 클릭 리스너
    private View.OnClickListener saveLogBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            travelTitle = inputTravelTitle.getText().toString();
            travel.setTravelTitle(travelTitle);
            if(inputTravelTitle.getText().length() == 0) {
                Log.d("null", "인데 왜 안나올까...");
            } else {
                Log.d("TRAVELTITLE 확인" , String.valueOf(travelTitle));
            }

            if(inputTravelTitle.getText().length() == 0) {
                Log.d("제목없음", "....");
            }
            if(travel.getStart_date() == null) {
                Log.d("시작일없음", "....");
            }
            if(travel.getEnd_date() == null) {
                Log.d("종료일없음", "....");
            }
//
//            Log.d("제목 : ", travelTitle);
//            Log.d("시작일 : ", travel.getStart_date());
//            Log.d("종료일 : ", travel.getEnd_date());
            if(inputTravelTitle.getText().length() != 0
                    && travel.getStart_date() != null
                    && travel.getEnd_date() != null) {
                Log.d("##################","############3");

                // TODO : 객체 DB에 save
                new Thread(new Runnable() {

                    TravelDAO travelDAO = new TravelDAO(getApplicationContext());

                    @Override
                    public void run() {
                        travelDAO.save(travel);
                        //restartActivity();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //mSaveDialog.dismiss();
                                Toast.makeText(TravelAddActivity.this, "여행이 저장되었습니다!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();

                //액티비티(팝업) 닫기
                finish();


            }
            if(inputTravelTitle.getText().length() == 0) {
                Log.d("여행제목", "입력해주세요");
                inputTravelTitle.setHintTextColor(Color.parseColor("#d86475"));
                inputTravelTitle.setHint("여행제목을 입력해주세요");
            }
            if(travel.getStart_date() == null) {
                Log.d("시작일을", "입력해주세요");
                inputTravelStartDate.setHintTextColor(Color.parseColor("#d86475"));
                inputTravelStartDate.setHint("시작일을 입력해주세요");
            }
            if(travel.getEnd_date() == null) {Log.d("종료일을", "입력해주세요");
                inputTravelEndDate.setHintTextColor(Color.parseColor("#d86475"));
                inputTravelEndDate.setHint("종료일을 입력해주세요");
            }
        }
    };

    // 취소 버튼 클릭 리스너
    private View.OnClickListener cancelLogBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //액티비티(팝업) 닫기
            finish();
        }
    };


    @Override
    public void onReceivedData(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;

        String str = String.valueOf(this.year) + "."
                + String.valueOf(this.month)
                + "." + String.valueOf(this.day);

        if(flag == true) {
            // 시작일 입력 결과 setting
            startDate = str;
            inputTravelStartDate.setText(str);
            travel.setStart_date(startDate);
        } else {
            endDate = str;
            inputTravelEndDate.setText(str);
            travel.setEnd_date(endDate);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }
}
