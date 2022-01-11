package com.triskelapps.model;

import android.content.Context;
import android.text.TextUtils;

import androidx.room.Entity;

import com.triskelapps.R;

@Entity(primaryKeys = {"code","lineId"})
public class BusStop  extends BusStopBase {

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

}
