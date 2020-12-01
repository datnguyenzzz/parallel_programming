package ru.bmstu.anonymizer.Messages;

public class SetServerList {
    private String [] serverList;

    public SetServerMessage(String[] serverList) {
        this.serverList = serverList;
    }

    public String[] getServerList() {
        return serverList;
    }
}
