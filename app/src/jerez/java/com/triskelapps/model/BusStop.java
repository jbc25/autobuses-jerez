package com.triskelapps.model;

import android.content.Context;

import androidx.room.Entity;
import androidx.room.Ignore;

import com.triskelapps.R;

@Entity(primaryKeys = {"code","lineId"})
public class BusStop  extends BusStopBase {

    private String way; // Not used but must keep it for backwards database integrity

    @Override
    public boolean hasBusStopExtraInfo() {
        return true;
    }

    @Override
    public String getBusStopExtraInfo(Context context) {
        return context.getString(
                R.string.bus_stop_direction_transfer_format, getDirection());
    }


    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
    }
}
