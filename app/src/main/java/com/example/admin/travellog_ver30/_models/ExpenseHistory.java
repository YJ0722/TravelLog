package com.example.admin.travellog_ver30._models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by a on 2018-10-01.
 */

public class ExpenseHistory implements Parcelable {

    private int id;     // ExpenseHistory 레코드 ID
    private int travel_no;     // 해당 여행의 no
    private int log_no;     // 해당 경비 해당하는 로그의 no
    private String expenseTitle;    // 비용 제목
    private int expenseType;        // 수입: 0, 지출: 1
    private int cost;   // 비용
    private double latitude;    // 위도
    private double longitude;   // 경도
    private long date;      // 날짜

    public ExpenseHistory(int id, String expenseTitle, int expenseType, int cost, double latitude, double longitude) {
        this.id = id;
        this.expenseTitle = expenseTitle;
        this.expenseType = expenseType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cost = cost;
    }

    public ExpenseHistory(String expenseTitle, int expenseType, int cost, double latitude, double longitude) {
        this.expenseTitle = expenseTitle;
        this.expenseType = expenseType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cost = cost;
    }

    public ExpenseHistory(int travel_no, String expenseTitle, int expenseType, int cost, double latitude, double longitude, long date) {
        this.travel_no = travel_no;
        this.expenseTitle = expenseTitle;
        this.expenseType = expenseType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cost = cost;
        this.date = date;
    }

    public ExpenseHistory(int id, int travel_no, int log_no, String expenseTitle, int expenseType, int cost, double latitude, double longitude, long date) {
        this.id = id;
        this.travel_no = travel_no;
        this.log_no = log_no;
        this.expenseTitle = expenseTitle;
        this.expenseType = expenseType;
        this.cost = cost;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    protected ExpenseHistory(Parcel in) {
        id = in.readInt();
        travel_no = in.readInt();
        log_no = in.readInt();
        expenseTitle = in.readString();
        expenseType = in.readInt();
        cost = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
        date = in.readLong();
    }

    public static final Creator<ExpenseHistory> CREATOR = new Creator<ExpenseHistory>() {
        @Override
        public ExpenseHistory createFromParcel(Parcel in) {
            return new ExpenseHistory(in);
        }

        @Override
        public ExpenseHistory[] newArray(int size) {
            return new ExpenseHistory[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getExpenseTitle() {
        return expenseTitle;
    }

    public void setExpenseTitle(String expenseTitle) {
        this.expenseTitle = expenseTitle;
    }

    public int getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(int expenseType) {
        this.expenseType = expenseType;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ExpenseHistory{" +
                "id=" + id +
                ", travel_no=" + travel_no +
                ", log_no=" + log_no +
                ", expenseTitle='" + expenseTitle + '\'' +
                ", expenseType=" + expenseType +
                ", cost=" + cost +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", date=" + date +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(travel_no);
        dest.writeInt(log_no);
        dest.writeString(expenseTitle);
        dest.writeInt(expenseType);
        dest.writeInt(cost);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeLong(date);
    }
}
