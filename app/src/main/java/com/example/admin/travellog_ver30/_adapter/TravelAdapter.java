package com.example.admin.travellog_ver30._adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.admin.travellog_ver30.R;
import com.example.admin.travellog_ver30._models.Travel;


/**
 * Created by a on 2018-10-14.
 */

public class TravelAdapter extends CursorAdapter {

    public TravelAdapter(Context context, Cursor c) {
        super(context, c, false);
    }

    public Travel get(int position) {
        Cursor cursor = getCursor();
        Travel travel = null;

        if (cursor.moveToPosition(position)) {
            int travel_no = cursor.getInt(cursor.getColumnIndex("_id"));
            String travelTitle = cursor.getString(cursor.getColumnIndex("travelTitle"));
            String startDate = cursor.getString(cursor.getColumnIndex("startDate"));
            String endDate = cursor.getString(cursor.getColumnIndex("endDate"));
            travel = new Travel(travel_no, travelTitle, startDate, endDate);

        }

        return travel;
    }

    // 여행 기록 목록 뷰 설정
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_travel, parent, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    // 여행 기록 목록에 데이터 셋팅
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        int no = cursor.getPosition()+1;
        String travelTitle = cursor.getString(cursor.getColumnIndex("travelTitle"));
        String startDate = cursor.getString(cursor.getColumnIndex("startDate"));
        String endDate = cursor.getString(cursor.getColumnIndex("endDate"));

        holder.itemTravelNo.setText(String.valueOf(no));
        holder.itemTravelTitle.setText(travelTitle);
        holder.itemTravelStartDate.setText(startDate);
        holder.itemTravelEndDate.setText(endDate);
    }


    private static class ViewHolder {
        TextView itemTravelNo, itemTravelTitle, itemTravelStartDate, itemTravelEndDate;

        ViewHolder(View itemView) {
            itemTravelNo = (TextView) itemView.findViewById(R.id.item_travel_no);
            itemTravelTitle = (TextView) itemView.findViewById(R.id.item_travelTitle);
            itemTravelStartDate = (TextView) itemView.findViewById(R.id.item_travel_start_date);
            itemTravelEndDate = (TextView) itemView.findViewById(R.id.item_travel_end_date);
        }
    }

}
