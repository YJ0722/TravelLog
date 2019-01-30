package com.example.admin.travellog_ver30.map;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.admin.travellog_ver30.R;
import com.example.admin.travellog_ver30._models.Coord;
import com.example.admin.travellog_ver30._models.ExpenseHistory;
import com.example.admin.travellog_ver30._models.ExpenseHistoryDAO;
import com.example.admin.travellog_ver30._models.FormatUtil;
import com.example.admin.travellog_ver30._models.Memo;
import com.example.admin.travellog_ver30._models.MemoDAO;
import com.example.admin.travellog_ver30._models.TrackingHistory;
import com.example.admin.travellog_ver30._models.TrackingHistoryDAO;
import com.example.admin.travellog_ver30.expnese.ExpensePopupActivity;
import com.example.admin.travellog_ver30.memo.MemoPopupActivity;
import com.example.admin.travellog_ver30.memo.MemoViewActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener {

    private TrackingHistoryDAO mTrackingHistoryDAO;
    private boolean mIsInitialized = false;
    private boolean mIsTracking = false;
    private TextView mDistanceTextView, mTimeTextView;
    private GoogleMap mMap;
    private ProgressDialog mProgressDialog, mSaveDialog;
    private FloatingActionButton mToggleButton, mStopButton;
    private LinkedList<Location> mHistory = new LinkedList<>();
    private long mTime;
    private float mDistance;
    private Location mCurrentLocation;
    private Timer mTimer;
    private PolylineOptions mPolyLineOptions;

    private int recordedPathSize;

    private FloatingActionButton mMemoButton, mExpenseButton;
    private MemoDAO memoDAO;

    private TrackingHistory trackingHistory;
    private ExpenseHistory expenseHistory;
    private ArrayList<Memo> memoArrCheck = new ArrayList<>();
    private ArrayList<ExpenseHistory> expenseArrCheck = new ArrayList<>();

    private ExpenseHistoryDAO expenseHistoryDAO;

    private int travelNo, logNoForSave;
    private String pathJsonSave;


    /// 타이머 ////
    //스톱워치의 상태를 위한 상수
    final static int IDLE = 0;
    final static int RUNNING = 1;
    final static int PAUSE = 2;

    int mStatus = IDLE;//처음 상태는 IDLE
    long mBaseTime, mPauseTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initDialog();

        // 권한 추가
        setPermission();

        mTrackingHistoryDAO = new TrackingHistoryDAO(getApplicationContext());
        memoDAO = new MemoDAO(getApplicationContext());
        expenseHistoryDAO = new ExpenseHistoryDAO(getApplicationContext());

        updateMap();

        mPolyLineOptions = new PolylineOptions();
        mPolyLineOptions.color(Color.parseColor("#3949AB")).width(18);

        mTimer = new Timer();
        initViews();

        Intent intent = getIntent();
        travelNo = Integer.parseInt(intent.getStringExtra("travelNo"));

    }

    //스톱워치는 위해 핸들러를 만든다.
    Handler timeHandler = new Handler() {

        //핸들러는 기본적으로 handleMessage에서 처리한다.
        public void handleMessage(android.os.Message msg) {

            //텍스트뷰를 수정해준다.
            mTimeTextView.setText(getEllapse());

            //메시지를 다시 보낸다.
            timeHandler.sendEmptyMessage(0);//0은 메시지를 구분하기 위한 것


        }
    };

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub

        timeHandler.removeMessages(0);//메시지를 지워서 메모리릭 방지
        super.onDestroy();

    }

    String getEllapse() {

        long now = SystemClock.elapsedRealtime();
        long ell = now - mBaseTime;//현재 시간과 지난 시간을 빼서 ell값을 구하고

        //아래에서 포맷을 예쁘게 바꾼다음 리턴해준다.
        String sEll = String.format("%02d:%02d:%02d", ell / 1000 / 60 / 60, ell / 1000 / 60, (ell / 1000) % 60);

        return sEll;
    }

    private void initDialog() {
        mProgressDialog = new ProgressDialog(MapsActivity.this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setTitle("위치 로딩");
        mProgressDialog.setMessage("현재 위치를 불러오고 있습니다.");
        mProgressDialog.show();

        mSaveDialog = new ProgressDialog(MapsActivity.this);
        mSaveDialog.setCancelable(false);
        mSaveDialog.setTitle("트래킹 기록 저장");
        mSaveDialog.setMessage("경로 데이터를 저장하고 있습니다.");
    }

    private void initViews() {
        mDistanceTextView = (TextView) findViewById(R.id.distance_tv);
        mTimeTextView = (TextView) findViewById(R.id.time_tv);

        mToggleButton = (FloatingActionButton) findViewById(R.id.toggle_fab);
        mToggleButton.setOnClickListener(mOnToggleButtonClickListener);
        mStopButton = (FloatingActionButton) findViewById(R.id.stop_fab);
        mStopButton.setOnClickListener(mOnStopButtonClickListener);
        mMemoButton = (FloatingActionButton) findViewById(R.id.memo_fab);
        mMemoButton.setOnClickListener(mOnMemoButtonClickListener);
        mExpenseButton = (FloatingActionButton) findViewById(R.id.expense_fab);
        mExpenseButton.setOnClickListener(mOnExpenseButtonOnClickListener);

    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);


    }

    public void updateMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        return;
    }

    // 메모 마커 추가
    private void addMemoMarker(ArrayList<Memo> memoArrC) {

        for (Memo memoVO : memoArrC) {

            mMap.addMarker(new MarkerOptions().position(new LatLng(memoVO.getLatitude(), memoVO.getLongitude()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .title(memoVO.getMemoTitle())).showInfoWindow();

        }

    }

    // 경비 마커 추가
    private void addExpenseMarker(ArrayList<ExpenseHistory> expenseHistoryArrayList) {

        for (ExpenseHistory expenseVO : expenseHistoryArrayList) {

            mMap.addMarker(new MarkerOptions().position(new LatLng(expenseVO.getLatitude(), expenseVO.getLongitude()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .title(expenseVO.getExpenseTitle())).showInfoWindow();

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("onMepReady()", "시작");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap = googleMap;
        mMap.setOnMyLocationChangeListener(this);

        addMemoMarker(memoArrCheck);
        addExpenseMarker(expenseArrCheck);

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        //정보창 클릭 리스너
        googleMap.setOnInfoWindowClickListener(infoWindowClickListener);

        updateMap();

        return;
    }

    private void setPermission() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
    }

    @Override
    public void onMyLocationChange(Location location) {
        if (!mIsInitialized) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18));
            mIsInitialized = true;

            mProgressDialog.dismiss();
        }

        if (mIsTracking) {
            mCurrentLocation = location;
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mHistory.add(mCurrentLocation);
                        mPolyLineOptions.add(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
                        int size = mHistory.size();
                        if (size > 1) {
                            Location previousLocation = mHistory.get(size - 2);
                            // 누적 이동거리 (미터)
                            mDistance += mCurrentLocation.distanceTo(previousLocation);


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDistanceTextView.setText(FormatUtil.getDouble(mDistance) + " m");
                                mMap.clear();
                                mMap.addPolyline(mPolyLineOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 18));
                            }
                        });
                    }

                    mTime += 1000;
                }

            }, 1000);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        final TrackingHistory saveTrackingHistory;
        final Memo memo;

        if(requestCode==1){
            if(resultCode==RESULT_OK){

                //현재값 가져옴
                long now = SystemClock.elapsedRealtime();

                //베이스타임 = 베이스타임 + (now - mPauseTime)
                //잠깐 스톱워치를 멈췄다가 다시 시작하면 기준점이 변하게 되므로..
                mBaseTime += (now - mPauseTime);
                timeHandler.sendEmptyMessage(0);

                long ell = now - mBaseTime;//현재 시간과 지난 시간을 빼서 ell값을 구하고

                String logSaveTitle = data.getStringExtra("logTitle");

                saveTrackingHistory
                            = new TrackingHistory(travelNo, logSaveTitle, System.currentTimeMillis(), mDistance, ell, pathJsonSave);

                Log.d("저장할 로그 정보", saveTrackingHistory.toString());
                mTrackingHistoryDAO.save(saveTrackingHistory);


                Cursor logCursor = mTrackingHistoryDAO.findAll();
                logCursor.moveToLast();
                logNoForSave = logCursor.getInt(logCursor.getColumnIndex("_id"));

                for(Memo saveMemo : memoArrCheck) {
                    saveMemo.setLog_no(logNoForSave);
                    Log.d("memo 저장 전 확인", saveMemo.toString());
                }

                for(ExpenseHistory saveExpense : expenseArrCheck) {
                    saveExpense.setLog_no(logNoForSave);
                    Log.d("expense 저장 전 확인", saveExpense.toString());
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(Memo saveMemo : memoArrCheck) {
                            memoDAO.save(saveMemo);
                        }
                        for(ExpenseHistory saveExpense : expenseArrCheck) {
                            Log.d("saveExpense 마지막 목록 확인", saveExpense.toString());
                            expenseHistoryDAO.save(saveExpense);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSaveDialog.dismiss();
                                finish();
                            }
                        });
                    }
                }).start();

                }


        }

        // 메모 저장 후 돌아올 때
        if(requestCode==2){
            if(resultCode==RESULT_OK){

                String memoTitle = data.getStringExtra("memoTitle");
                String memoContent = data.getStringExtra("memoContent");
                double memoLatitude = data.getDoubleExtra("memoLatitude", 0);
                double memoLongitude = data.getDoubleExtra("memoLongitude", 0);

                // 로그넘버 없는 vo 객체
                memo = new Memo(travelNo, logNoForSave, memoTitle, memoContent, memoLatitude, memoLongitude, System.currentTimeMillis());

                memoArrCheck.add(memo);

                Log.d(";;;;;;;;;;; 메모 배열 체크", ";;;;;;;;;;;;;");
                for(int i=0; i<memoArrCheck.size(); i++) {
                    Log.d("[" + i +"]", memoArrCheck.get(i).toString());
                }
                Log.d(";;;;;;;;;;;;;;;;;;;;", ";;;;;;;;;;;;;;;;;;");

            }

        }

        // 경비 저장 후 돌아올 때
        if(requestCode==3){

            String expenseTitle;
            int type, cost;

            if(resultCode==RESULT_OK){

                expenseTitle = data.getStringExtra("expenseTitle");
                type = data.getIntExtra("type", 0);
                cost = data.getIntExtra("cost", 0);
                double expenseLatitude = data.getDoubleExtra("expenseLatitude", 0);
                double expenseLongitude = data.getDoubleExtra("expenseLongitude", 0);

                expenseHistory = new ExpenseHistory(travelNo, expenseTitle, type, cost, expenseLatitude, expenseLongitude, System.currentTimeMillis());

                expenseArrCheck.add(expenseHistory);
                Log.d(";;;;;;;;;;; 경비 배열 체크", ";;;;;;;;;;;;;");
                for(int i=0; i<expenseArrCheck.size(); i++) {
                    Log.d("[" + i +"]", expenseArrCheck.get(i).toString());
                }
                Log.d(";;;;;;;;;;;;;;;;;;;;", ";;;;;;;;;;;;;;;;;;");

            }
        }

    }

    //정보창 클릭 리스너
    GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {

            Intent intent = new Intent(MapsActivity.this, MemoViewActivity.class);
            //startActivityForResult(intent, 4);
        }
    };

    private View.OnClickListener mOnToggleButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mIsTracking) {
                mIsTracking = false;
                mToggleButton.setImageResource(R.drawable.ic_start);

                //핸들러 메시지를 없애고
                timeHandler.removeMessages(0);
                //멈춘 시간을 파악
                mPauseTime = SystemClock.elapsedRealtime();

                mStatus = PAUSE;//상태를 멈춤으로 표시

            } else {
                mIsTracking = true;
                mToggleButton.setImageResource(R.drawable.ic_pause);

                //현재 값을 세팅해주고
                mBaseTime = SystemClock.elapsedRealtime();

                //핸드러로 메시지를 보낸다
                timeHandler.sendEmptyMessage(0);

                //상태를 RUNNING으로 바꾼다.
                mStatus = RUNNING;

            }
        }
    };

    private View.OnClickListener mOnStopButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

                mOnToggleButtonClickListener.onClick(mToggleButton);
                List<LatLng> recordedPath = mPolyLineOptions.getPoints();
                recordedPathSize = recordedPath.size();

                    List<Coord> coords = new ArrayList<>();
                    for (LatLng each : recordedPath) {
                        coords.add(new Coord(each.latitude, each.longitude));
                    }

                    String pathJson = new Gson().toJson(coords);
                    pathJsonSave = pathJson;
                    double time = mTime / 1000.0;
                    trackingHistory = new TrackingHistory(travelNo, System.currentTimeMillis(), mDistance, mTime, pathJson);

                    //데이터 담아서 팝업(액티비티) 호출
                    Intent intent = new Intent(MapsActivity.this, InputTitlePopupActivtiy.class);
                    intent.putExtra("trackingHistory", trackingHistory);
                intent.putExtra("json", pathJson);
                    startActivityForResult(intent, 1);

        }
    };

    // 메모 버튼 클릭리스너
    private View.OnClickListener mOnMemoButtonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            // 위치 정보 있다면
            if(mCurrentLocation != null) {

                //데이터 담아서 팝업(액티비티) 호출
                Intent intent = new Intent(MapsActivity.this, MemoPopupActivity.class);
                intent.putExtra("memoLatitude", mCurrentLocation.getLatitude());
                intent.putExtra("memoLongitude", mCurrentLocation.getLongitude());
                startActivityForResult(intent, 2);
            } else {
                return;
            }
        }
    };

    // 경비 버튼 클릭 리스너
    private View.OnClickListener mOnExpenseButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // 위치 정보 있다면
            if(mCurrentLocation != null) {
                //데이터 담아서 팝업(액티비티) 호출
                Intent intent = new Intent(MapsActivity.this, ExpensePopupActivity.class);
                intent.putExtra("expenseLatitude", mCurrentLocation.getLatitude());
                intent.putExtra("expenseLongitude", mCurrentLocation.getLongitude());

                startActivityForResult(intent, 3);
            } else {
                return;
            }
        }
    };
}

