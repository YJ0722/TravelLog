package com.example.admin.travellog_ver30._models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by a on 2018-09-16.
 */

public class Memo implements Parcelable{
    private int travel_no;
    private int log_no;
    private int memo_no;
    private String memoTitle;
    private double latitude;
    private double longitude;
    private String memoContent;
    private long date;

    public Memo() {

    }
    public Memo(String memoTitle, double latitude, double longitude) {
        this.memoTitle = memoTitle;
        this.latitude = latitude;
        this.longitude = longitude;
        //this.date = date;
    }
    public Memo(String memoTitle, String memoContent, double latitude, double longitude) {
        this.memoTitle = memoTitle;
        this.memoContent = memoContent;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public Memo(String memoTitle, double latitude, double longitude, long date) {
        this.memoTitle = memoTitle;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    public Memo(int travel_no, String memoTitle, double latitude, double longitude, long date) {
        this.travel_no = travel_no;
        //this.log_no = log_no;
        this.memo_no = memo_no;
        this.memoTitle = memoTitle;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }
    public Memo(int travelNo, String memoTitle, String memoContent,
                double latitude, double longitude, long date) {
        this.travel_no = travelNo;
        this.memoTitle = memoTitle;
        this.memoContent = memoContent;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }
    public Memo(int travelNo, int logNo, String memoTitle, String memoContent,
                double latitude, double longitude, long date) {
        this.travel_no = travelNo;
        this.log_no = logNo;
        this.memoTitle = memoTitle;
        this.memoContent = memoContent;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }
    public Memo(int travelNo, int logNo, int memoNo, String memoTitle, String memoContent,
                double latitude, double longitude, long date) {
        this.travel_no = travelNo;
        this.log_no = logNo;
        this.memo_no = memoNo;
        this.memoTitle = memoTitle;
        this.memoContent = memoContent;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    protected Memo(Parcel in) {
        travel_no = in.readInt();
        log_no = in.readInt();
        memo_no = in.readInt();
        memoTitle = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        memoContent = in.readString();
        date = in.readLong();
    }

    public static final Creator<Memo> CREATOR = new Creator<Memo>() {
        @Override
        public Memo createFromParcel(Parcel in) {
            return new Memo(in);
        }

        @Override
        public Memo[] newArray(int size) {
            return new Memo[size];
        }
    };

    public int getTravel_no() {
        return travel_no;
    }

    public void setTravel_no(int travel_no) {
        this.travel_no = travel_no;
    }

    public int getLog_no() {
        return log_no;
    }

    public void setLog_no(int log_no) {
        this.log_no = log_no;
    }

    public int getMemo_no() {
        return memo_no;
    }

    public void setMemo_no(int memo_no) {
        this.memo_no = memo_no;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMemoTitle() {
        return memoTitle;
    }

    public void setMemoTitle(String memoTitle) {
        this.memoTitle = memoTitle;
    }

    public String getMemoContent() {
        return memoContent;
    }

    public void setMemoContent(String memoContent) {
        this.memoContent = memoContent;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Memo{" +
                "travel_no=" + travel_no +
                ", log_no=" + log_no +
                ", memo_no=" + memo_no +
                ", memoTitle='" + memoTitle + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", memoContent='" + memoContent + '\'' +
                ", date=" + date +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(travel_no);
        dest.writeInt(log_no);
        dest.writeInt(memo_no);
        dest.writeString(memoTitle);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(memoContent);
        dest.writeLong(date);
    }
}
