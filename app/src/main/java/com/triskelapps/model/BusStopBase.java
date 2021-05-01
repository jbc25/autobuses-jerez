package com.triskelapps.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Ignore;

import java.io.Serializable;
import java.util.List;

public abstract class BusStopBase implements Serializable {

    public static final int CODE_NOT_FOUND = -1;

    protected @NonNull String name;
    protected int code;
    protected int lineId;

    @Ignore
    protected List<Double> coordinates;

    public abstract boolean hasBusStopExtraInfo();

    public abstract String getBusStopExtraInfo(Context context);

    public String getName() {
        return name;
    }

    public abstract String getNameComplete();

    public void setName(String name) {
        this.name = name;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public boolean hasValidCode() {
        return getCode() != CODE_NOT_FOUND;
    }


}
