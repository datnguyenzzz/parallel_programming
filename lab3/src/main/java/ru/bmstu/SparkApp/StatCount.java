package ru.bmstu.SparkApp;


import java.io.Serializable;

public class StatCount implements Serializable {

    private static final long serialVersionUID = 4582044296179750556L;

    private int countFlights;
    private int countCancelledFlights;
    private int countDelayFlights;
    private float maxDelay;

    public StatCount(){};

    public StatCount(int countFlights, int countCancelledFlights, int countDelayFlights, float maxDelay) {
        this.countFlights = countFlights;
        this.countCancelledFlights = countCancelledFlights;
        this.countDelayFlights = countDelayFlights;
        this.maxDelay = maxDelay;
    }

    public int get_count_flights() {
        return countFlights;
    }

    public int get_count_cancelled() {
        return countCancelledFlights;
    }

    public int get_count_delay() {
        return countDelayFlights;
    }

    public float get_max_delay() {
        return maxDelay;
    }

    public static StatCount add_value(StatCount a, boolean isCancelled,boolean isDelay,float delayTime) {
        return new StatCount(a.get_count_flights()+1,
                             isCancelled ? a.get_count_cancelled()+1 : a.get_count_cancelled(),
                             isDelay ? a.get_count_delay()+1 : a.get_count_delay(),
                             Math.max(delayTime, a.get_max_delay()));
    }

    public static StatCount add(StatCount a,StatCount b) {
        return new StatCount(a.get_count_flights() + b.get_count_flights(),
                             a.get_count_cancelled() + b.get_count_cancelled(),
                             a.get_count_delay() + b.get_count_delay(),
                             a.get_max_delay() + b.get_max_delay());
    }

    public static String output(StatCount a) {
            float delayPercent = (float)a.countDelayFlights / (float)a.countFlights * 100.0f ;
            float cancelPercent = (float)a.countCancelledFlights / (float)a.countFlights * 100.0f;
            String output = " | Count all flights: " + a.countFlights + ", Statistics: ";
            output += "Cancelled flights[" + a.countCancelledFlights + "/" + a.countFlights + "]: " + cancelPercent + "%; ";
            output += "Delayed flights[" + a.countDelayFlights + "/" + a.countFlights + "]: " + delayPercent + "%; ";
            output += "Max delay time flight: " + a.maxDelay;
            return output;
    }

    @Override
    public String toString() {
        return "StatisticCount{" +
                "countFlights=" + countFlights +
                ", countCancelledFlights=" + countCancelledFlights +
                ", countDelayFlights=" + countDelayFlights +
                ", maxDelay=" + maxDelay +
                '}';
    }
}
