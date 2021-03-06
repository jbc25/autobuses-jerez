package com.triskelapps.busjerez.model;

import android.graphics.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BusLine implements Serializable {

    private int id;
    private String name;
    private String description;
    private String color;
    private List<BusStop> busStops;
    private List<List<Double>> path = new ArrayList<>();

    private int finalBusStopCode;

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
        return Color.parseColor(color);
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getFinalBusStopCode() {
        return finalBusStopCode;
    }

    public void setFinalBusStopCode(int finalBusStopCode) {
        this.finalBusStopCode = finalBusStopCode;
    }
}
