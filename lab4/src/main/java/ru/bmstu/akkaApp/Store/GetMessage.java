package ru.bmstu.akkaApp.Store;

import ru.bmstu.akkaApp.Test.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class GetMessage {
    private int packageID;

    public GetMessage(int id) {
        this.packageID = id;
    }

    public int getPackageID() {
        return packageID;
    }
}
