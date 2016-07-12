package com.algo.raycast;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Infernus on 05/07/16.
 */
public class Point {
    private double x, y;

    public Point(LatLng latLng) {
        this.x = latLng.latitude;
        this.y = latLng.longitude;
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
