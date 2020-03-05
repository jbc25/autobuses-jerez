package com.triskelapps.busjerez.ui;

import com.triskelapps.busjerez.base.BaseView;
import com.triskelapps.busjerez.model.BusLine;

import java.util.List;

public interface MainView extends BaseView {
    void loadBusLines(List<BusLine> busLines);
}
