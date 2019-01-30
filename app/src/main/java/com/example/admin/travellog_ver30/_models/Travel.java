package com.example.admin.travellog_ver30._models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by a on 2018-10-14.
 */

public class Travel implements Parcelable{

    private int travel_no;
    private String travelTitle;
    private String start_date;
    private String end_date;

    public Travel() {
    }

    public Travel(String travelTitle, String start_date, String end_date) {
        this.travelTitle = travelTitle;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public Travel(int travel_no, String travelTitle, String start_date, String end_date) {
        this.travel_no = travel_no;
        this.travelTitle = travelTitle;
        this.start_date = start_date;
        this.end_date = end_date;
    }


    protected Travel(Parcel in) {
        travel_no = in.readInt();
        travelTitle = in.readString();
        start_date = in.readString();
        end_date = in.readString();
    }

    public static final Creator<Travel> CREATOR = new Creator<Travel>() {
        @Override
        public Travel createFromParcel(Parcel in) {
            return new Travel(in);
        }

        @Override
        public Travel[] newArray(int size) {
            return new Travel[size];
        }
    };

    @Override
    public String toString() {
        return "Travel{" +
                "travel_no=" + travel_no +
                ", travelTitle='" + travelTitle + '\'' +
                ", start_date='" + start_date + '\'' +
                ", end_date='" + end_date + '\'' +
                '}';
    }

    public int getTravel_no() {
        return travel_no;
    }

    public void setTravel_no(int no) {
        this.travel_no = no;
    }

    public String getTravelTitle() {
        return travelTitle;
    }

    public void setTravelTitle(String travelTitle) {
        this.travelTitle = travelTitle;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(travel_no);
        dest.writeString(travelTitle);
        dest.writeString(start_date);
        dest.writeString(end_date);
    }
}
