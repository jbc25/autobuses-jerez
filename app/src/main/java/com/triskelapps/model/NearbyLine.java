package com.triskelapps.model;

public class NearbyLine {

    private BusLine busLine;
    private BusStop busStop;
    private double distance = Double.MAX_VALUE;

    public NearbyLine(BusLine busLine, BusStop busStop, double distance) {
        this.busLine = busLine;
        this.busStop = busStop;
        this.distance = distance;
    }

    public BusLine getBusLine() {
        return busLine;
    }

    public void setBusLine(BusLine busLine) {
        this.busLine = busLine;
    }

    public BusStop getBusStop() {
        return busStop;
    }

    public void setBusStop(BusStop busStop) {
        this.busStop = busStop;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
