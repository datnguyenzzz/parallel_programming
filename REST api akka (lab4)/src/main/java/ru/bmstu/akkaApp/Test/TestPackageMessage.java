package ru.bmstu.akkaApp.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class TestPackageMessage {
    private final String PACKAGE_ID = "packageId";
    private final String JSSCRIPT = "jsScript";
    private final String FUNCTION_NAME = "functionName";
    private final String TESTS = "tests";

    private int packageID;
    private String jsScript;
    private String functionName;
    private ArrayList<Test> tests;

    @JsonCreator
    public TestPackageMessage(@JsonProperty(PACKAGE_ID) int packageID, @JsonProperty(JSSCRIPT) String jsScript,
                              @JsonProperty(FUNCTION_NAME) String functionName, @JsonProperty(TESTS) ArrayList<Test> tests) {
        this.packageID = packageID;
        this.jsScript = jsScript;
        this.functionName = functionName;
        this.tests = tests;
    }

    int getPackageID() {
        return packageID;
    }

    String getJsScript() {
        return jsScript;
    }

    String getFunctionName() {
        return functionName;
    }

    ArrayList<Test> getTests() {
        return tests;
    }
}
