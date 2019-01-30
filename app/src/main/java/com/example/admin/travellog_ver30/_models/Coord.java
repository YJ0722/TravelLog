package com.example.admin.travellog_ver30._models;

/**
 * Created by massivcode@gmail.com on 2017. 1. 5. 14:47
 */

public class Coord {
    private double latitude;
    private double longitude;

    public Coord() {
    }

    public Coord(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Coord{");
        sb.append("latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append('}');
        return sb.toString();
    }
}
