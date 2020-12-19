package ru.bmstu.ProxyApp;

public class Partitions {
    private String startPos;
    private String endPos;

    private long time;

    Partitions(String startPos, String endPos, long time) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean belongTo(String pos) {
        return Integer.parseInt(startPos) <= Integer.parseInt(pos) && Integer.parseInt(pos) <= Integer.parseInt(endPos);
    }
}