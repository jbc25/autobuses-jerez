package com.triskelapps.model.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class BusLineVisible {

    @PrimaryKey
    private int lineId;

    private boolean visible = true;

    public BusLineVisible(int lineId, boolean visible) {
        this.lineId = lineId;
        this.visible = visible;
    }

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
