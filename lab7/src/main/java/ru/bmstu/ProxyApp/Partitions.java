package ru.bmstu.ProxyApp.Partitions;

public class Partitions {
    private String startPos;
    private String endPos;

    private long time;

    Partitions(String startPos, String endPos, long time) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.time = time;
    }

    public boolean belongTo(String pos) {
        return Integer.parseInt(startPos) <= Integer.parseInt(pos) && Integer.parseInt(pos) <= Integer.parseInt(endPos);
    }
}