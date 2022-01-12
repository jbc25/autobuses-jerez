package com.triskelapps.model;

import android.content.Context;
import android.text.TextUtils;

import androidx.room.Entity;

import com.triskelapps.R;

@Entity(primaryKeys = {"code","lineId"})
public class BusStop  extends BusStopBase {

    private String lineName;

    @Override
    public boolean hasBusStopExtraInfo() {
        return false;
    }

    @Override
    public String getBusStopExtraInfo(Context context) {
        return null;
    }

    @Override
    public String getNameComplete() {
        return name;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }
}
