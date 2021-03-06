package com.example.admin.travellog_ver30._models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.example.admin.travellog_ver30._db.TrackingHistoryHelper;


/**
 * Created by a on 2018-10-14.
 */

public class TravelDAO {

    private TrackingHistoryHelper mHelper;

    public TravelDAO(Context context) {
        this.mHelper = TrackingHistoryHelper.getInstance(context);
    }

    /**
     * 메모를 테이블에 추가합니다.
     *
     * @param data : 추가할 메모
     * @return : 성공 유무
     */
    public boolean save(Travel data) {
        // db는 읽기만 가능한 것과 읽고 쓸 수 있는 것이 있습니다.
        // 삽입은 쓰는 것이므로 getWritableDatabase() 메소드를 이용해야 합니다.
        SQLiteDatabase db = mHelper.getWritableDatabase();

        // 삽입할 데이터는 ContentValues 객체에 담깁니다.
        // 맵과 동일하게 key 와 value 로 데이터를 저장합니다.
        ContentValues values = new ContentValues();

        // 삽입할 메모의 제목, 내용, 시간을 ContentValues 에 넣습니다.
        // 메모의 id 는 AUTO INCREMENT 이므로 추가하지 않습니다.
        values.put("travelTitle", data.getTravelTitle());
        values.put("startDate", data.getStart_date());
        values.put("endDate", data.getEnd_date());

        long insertedId = db.insert(Travel.class.getSimpleName(), null, values);

        return insertedId != -1;
    }

    /**
     * 테이블에 존재하는 모든 데이터를 로드합니다.
     */
    public Cursor findAll() {

        // SELECT 작업은 읽기 작업이므로 getReadableDatabase 메소드를 이용하여 읽기 전용 database 를 얻습니다.
        SQLiteDatabase db = mHelper.getReadableDatabase();

        return db.query(Travel.class.getSimpleName(), null, null, null, null, null, "_id" + " ASC");
    }

    /**
     * ListView 에서 롱클릭한 메모를 삭제합니다.
     */
    public boolean delete(int id) {
        // DELETE 작업은 쓰기 권한이 필요합니다.
        SQLiteDatabase db = mHelper.getWritableDatabase();

        /**
         * public int delete (String table, String whereClause, String[] whereArgs)
         * table : 지울 데이터가 위치하는 테이블, whereClause : 조건절, whereArgs : 조건절
         *
         * update 메소드와 동일하게 delete 메소드는 반영된 행의 카운트를 리턴합니다.
         */
        int affectedRowsCount = db.delete(Travel.class.getSimpleName(), BaseColumns._ID + " = ? ", new String[]{String.valueOf(id)});
        return affectedRowsCount == 1;
    }

}
