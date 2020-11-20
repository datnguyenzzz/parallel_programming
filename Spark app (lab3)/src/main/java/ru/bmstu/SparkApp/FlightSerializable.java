package ru.bmstu.SparkApp;

import java.io.Serializable;

public class FlightSerializable implements Serializable {

    private static final long serialVersionUID = -6714985524961312675L;

    private int originAirportID;
    private int destAirportID;
    private float delayTime;
    private float cancelled;

    public FlightSerializable() {}

    public FlightSerializable(int originAirportID,int destAirportID,float delayTime,float cancelled) {
        this.originAirportID = originAirportID;
        this.destAirportID = destAirportID;
        this.delayTime = delayTime;
        this.cancelled = cancelled;
    }

    public float get_delay_time() {
        return delayTime;
    }

    public float get_cancelled() {
        return cancelled;
    }

    @Override
    public String toString() {
        return "FlightSerializable{" +
               "originAirportID=" + originAirportID +
               ", destAirportID=" + destAirportID +
               ", delayTime=" + delayTime +
               ", cancelled=" + cancelled +
               '}';
    }
}
