package com.triskelapps.model;

import android.graphics.Color;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusLine implements Serializable {

    private static final String TAG = BusLine.class.getSimpleName();

    private int id;
    private String name;
    private String description;
    private int color;
    private String colorHex;
    private List<BusStop> busStops;
    private List<List<Double>> path = new ArrayList<>();
    private Map<String, String> extras = new HashMap<>();

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
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addCoordsToPath(Double lat, Double lng) {
        path.add(new ArrayList<Double>() {{
            add(lat);
            add(lng);
        }});
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

    public void setColor(int color) {
        this.color = color;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public Map<String, String> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, String> extras) {
        this.extras = extras;
    }

    public String getExtra(String key) {
        return extras != null ? extras.get(key) : null;
    }
}
