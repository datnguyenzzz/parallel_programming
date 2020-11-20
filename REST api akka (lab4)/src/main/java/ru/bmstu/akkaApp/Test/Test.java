package ru.bmstu.akkaApp.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Test {
    private final String TEST_NAME = "testName";
    private final String EXPECTED_RESULT = "expectedResult";
    private final String PARAMS = "params";
    private final String RESULT = "result";

    @JsonProperty(TEST_NAME)
    private String testName;

    @JsonProperty(EXPECTED_RESULT)
    private String expectedResult;

    @JsonProperty(PARAMS)
    private ArrayList<Integer> params;

    @JsonProperty(RESULT)
    private boolean result;

    @JsonCreator
    Test(@JsonProperty(TEST_NAME) String testName, @JsonProperty(EXPECTED_RESULT) String expectedResult,
                @JsonProperty(PARAMS) ArrayList<Integer> params) {
        this.testName = testName;
        this.expectedResult = expectedResult;
        this.params = params;
        this.result = false;
    }

    Test(String testName, String expectedResult, ArrayList<Integer> params, boolean result) {
        this.testName = testName;
        this.expectedResult = expectedResult;
        this.params = params;
        this.result = result;
    }

    String getTestName() {
        return this.testName;
    }

    String getExpectedResult() {
        return expectedResult;
    }

    ArrayList<Integer> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "testName = " + testName + " result = " + result;
    }
}
