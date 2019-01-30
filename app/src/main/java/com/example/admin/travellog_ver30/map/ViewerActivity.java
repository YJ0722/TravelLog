package com.example.admin.travellog_ver30.map;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.admin.travellog_ver30.R;
import com.example.admin.travellog_ver30.expnese.ExpenseViewActivtiy;
import com.example.admin.travellog_ver30.memo.MemoViewActivity;
import com.example.admin.travellog_ver30._models.Coord;
import com.example.admin.travellog_ver30._models.ExpenseHistory;
import com.example.admin.travellog_ver30._models.ExpenseHistoryDAO;
import com.example.admin.travellog_ver30._models.Memo;
import com.example.admin.travellog_ver30._models.MemoDAO;
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
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static android.util.Log.d;

public class ViewerActivity extends FragmentActivity implements OnMapReadyCallback {

    private MemoDAO memoDAO;
    private Memo memoBeans;
    private ExpenseHistoryDAO expenseDAO;
    private ExpenseHistory expenseBeans;
    private GoogleMap mMap;

    private int expenseNo = 0;
    private int indexMarker;
    private int travelNo;
    private int logNo;
    private String pathJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        Intent intent = getIntent();
        pathJson = intent.getStringExtra("json");
        Log.d("경로", pathJson);

        travelNo = intent.getIntExtra("travelNo", 0);
        Log.d("travelNo ViewerActivity", String.valueOf(travelNo));

        logNo = intent.getIntExtra("logNo", 0);
        Log.d("logNo ViewerActivity 확인", String.valueOf(logNo));

        indexMarker = intent.getIntExtra("indexMarker", 0);
        Log.d("마커 종류 확인", String.valueOf(indexMarker));

        if(indexMarker == 1) {
            expenseNo = intent.getIntExtra("expenseNo", 0);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d("noMapReady 에서 logNo 확인", String.valueOf(logNo));

        mMap = googleMap;
        mMap.clear();

        ArrayList<Coord> pathList = new Gson().fromJson(pathJson, new TypeToken<ArrayList<Coord>>() {
        }.getType());

        // 확인용! 추후 삭제 필요
        d("-----------", "------------");
        for(int i=0; i<pathList.size(); i++) {
            d("[" + i +"] : " , pathList.get(i).toString());
        }
        d("-----------", "------------");

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.parseColor("#3949AB"));
        polylineOptions.width(18);

        List<LatLng> latLngList = new ArrayList<>();
        for (Coord coord : pathList) {
            latLngList.add(new LatLng(coord.getLatitude(), coord.getLongitude()));
        }
        polylineOptions.addAll(latLngList);

        int size = latLngList.size();
        if (size > 2) {
            LatLng firstLocation = latLngList.get(0);
            LatLng lastLocation = latLngList.get(size - 1);
            mMap.addMarker(new MarkerOptions().position(firstLocation).title("시작")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.addMarker(new MarkerOptions().position(lastLocation).title("종료")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 18));
        }

        // 메모 정보 보여줄 경우
        if(indexMarker == 0) {
            // 메모 마커 표시
            addMemoMarker(logNo);
            // 메모 정보창 클릭 리스너
            googleMap.setOnInfoWindowClickListener(memoInfoWindowClickListener);
        }
        // 경비 정보 보여줄 경우
        else {
            addExpenseMarker(logNo);
            // 경비 정보창 클릭 리스너
            googleMap.setOnInfoWindowClickListener(expenseInfoWindowClickListener);
        }

        mMap.addPolyline(polylineOptions);
    }

    public void updateMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    // 메모 정보창 클릭 리스너
    GoogleMap.OnInfoWindowClickListener memoInfoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {

            String memoId = String.valueOf(marker.getTag());

            Log.d("클릭 태그 확인", memoId);
            Intent intent = new Intent(ViewerActivity.this, MemoViewActivity.class);

            // 시작 or 종료 마커 말풍선 터치
            if(memoId == "null") {
                intent.putExtra("memoId", 0);
                Log.d("11111", "111111");

                String title = marker.getTitle();
                Log.d("* marker.getTitle() *", title);
                if(title.compareTo("시작") == 0) {
                    Log.d("22222", "22222");
                    intent.putExtra("markerTitle", "시작");
                } else if(title.compareTo("종료") == 0) {
                    Log.d("33333", "33333");
                    intent.putExtra("markerTitle", "종료");
                }
            }
            // 메모 마커 말풍선 터치
            else {
                Log.d("이게 찍혀야 하는데...", String.valueOf(memoId));
                intent.putExtra("memoId", Integer.parseInt(memoId));
            }
            startActivityForResult(intent, 1);
        }
    };

    // 경비 정보창 클릭 리스너
    GoogleMap.OnInfoWindowClickListener expenseInfoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {

            String expenseId = String.valueOf(marker.getTag());

            Intent intent = new Intent(ViewerActivity.this, ExpenseViewActivtiy.class);

            // 시작 or 종료 마커 말풍선 터치
            if(expenseId == "null") {
                intent.putExtra("expenseId", 0);
                Log.d("11111", "111111");

                String title = marker.getTitle();
                Log.d("* marker.getTitle() *", title);
                if(title.compareTo("시작") == 0) {
                    Log.d("22222", "22222");
                    intent.putExtra("markerTitle", "시작");
                } else if(title.compareTo("종료") == 0) {
                    Log.d("33333", "33333");
                    intent.putExtra("markerTitle", "종료");
                }
            }
            // 경비 마커 말풍선 터치
            else {
                intent.putExtra("expenseId", Integer.parseInt(expenseId));
            }
            startActivityForResult(intent, 1);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 1) {
            if(resultCode==RESULT_OK){
                Log.d("ViewerActivity", "onActivityResult()-------------------");
                mMap.clear();
                onMapReady(mMap);
                updateMap();
            }
        }
    }


    private void addMemoMarker(int logNoForMemo) {

        memoDAO = new MemoDAO(getApplicationContext());

        Log.d("addMemoMarker 로그 넘버", String.valueOf(logNoForMemo));
        // TODO : 해당 로그의 메모 목록 불러오기
        Cursor cursor = memoDAO.findByLogNo(logNoForMemo);
        int recordCount = cursor.getCount();    // 저장된 메모 개수

        Log.d("메모 갯수 확인!!", String.valueOf(recordCount));

        for(int i=0; i<recordCount; i++) {
            cursor.moveToNext();
            int memoId = cursor.getInt(cursor.getColumnIndex("_id"));
            String memoTitle = cursor.getString(cursor.getColumnIndex("memoTitle"));
            String memoContent = cursor.getString(cursor.getColumnIndex("memoContent"));
            double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
            double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));

            memoBeans = new Memo(memoTitle, memoContent, latitude, longitude);
            memoBeans.setMemo_no(memoId);
            d("@@@@@@@", "Recode #" + i + " : " + memoBeans.getMemo_no() +"," + memoBeans.getMemoTitle() + ", " + memoBeans.getLatitude() + ", " +
                    memoBeans.getLongitude() );

            // 해당 memo 객체에 해당하는 마크 추가
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                    .title(memoBeans.getMemoTitle())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                    .setTag(memoBeans.getMemo_no());

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 18));

        }
    }

    private void addExpenseMarker(int logNoForExpense) {

        expenseDAO = new ExpenseHistoryDAO(getApplicationContext());
        double selectLat = 0;
        double selectLon = 0;

        Log.d("addExpenseMarker 로그 넘버", String.valueOf(logNoForExpense));
        // TODO : 해당 로그의 메모 목록 불러오기
        Cursor cursor = expenseDAO.findByLogNo(travelNo, logNoForExpense);
        int recordCount = cursor.getCount();    // 저장된 메모 개수

        Log.d("경비 갯수 확인!!", String.valueOf(recordCount));

        for(int i=0; i<recordCount; i++) {
            cursor.moveToNext();
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String expenseTitle = cursor.getString(cursor.getColumnIndex("expenseTitle"));
            int expenseType = cursor.getInt(cursor.getColumnIndex("expenseType"));
            int cost = cursor.getInt(cursor.getColumnIndex("cost"));
            double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
            double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));

            expenseBeans = new ExpenseHistory(id, expenseTitle, expenseType, cost, latitude, longitude);
            d("@@@@@@@", "Recode #" + i + " : " + String.valueOf(logNo)+"," + expenseBeans.getExpenseTitle() + ", " +
                    String.valueOf(expenseBeans.getExpenseType()) + ", " + String.valueOf(expenseBeans.getCost()) + ", " +
                    String.valueOf(expenseBeans.getLatitude()) + ", " + String.valueOf(expenseBeans.getLongitude()));

            String showCost = "-";
            if(expenseBeans.getExpenseType() == 0) {
                showCost = "+" + String.valueOf(expenseBeans.getCost());
            } else {
                showCost = '-' + String.valueOf(expenseBeans.getCost());
            }

            // 해당 객체에 해당하는 마크 추가
            if(expenseNo == expenseBeans.getId()) {
                selectLat = expenseBeans.getLatitude();
                selectLon = expenseBeans.getLongitude();
                mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                        .title(showCost)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)))
                        .setTag(String.valueOf(id));
            } else {

                mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                        .title(showCost)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                        .setTag(String.valueOf(id));

            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(selectLat, selectLon), 18));

        }
    }
}
