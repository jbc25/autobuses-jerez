package com.triskelapps.model;

import android.graphics.Color;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BusLineBase implements Serializable {

    private static final String TAG = BusLineBase.class.getSimpleName();

    private int id;
    private String name;
    private String description;
    private String colorHex;
    private List<BusStop> busStops;
    private List<List<Double>> path = new ArrayList<>();

    private transient boolean visible = true;

    public String getName() {
        return name.trim();
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BusStop> getBusStops() {
        return busStops;
    }

    public void setBusStops(List<BusStop> busStops) {
        this.busStops = busStops;
    }

    public List<List<Double>> getPath() {
        return path;
    }

    public void setPath(List<List<Double>> path) {
        this.path = path;
    }

    public String getDescription() {
        return description.trim();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getColor() {
        try {
            return Color.parseColor(colorHex);
        } catch (Exception e) {
            Log.e(TAG, "color parse error: " + colorHex + ", line: " + id );
            return Color.BLUE;
        }
    }


    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

}
