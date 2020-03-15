package com.triskelapps.busjerez.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.triskelapps.busjerez.model.BusStop;

import java.util.List;


@Dao
public interface BusStopDao extends BaseDao<BusStop> {


    @Query("SELECT * FROM busstop")
    List<BusStop> getAll();

    @Query("SELECT * FROM busstop WHERE code = :code")
    BusStop getBusBusStop(int code);


}
