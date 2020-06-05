package ru.hse.cs.java2020.task03;

public class DataBaseResponse {
    private String oathToken;
    private String orgId;
    private String state;
    private String tmpInfo;

    public void setOathToken(String s) {
        oathToken = s;
    }

    public void setOrgId(String s) {
        orgId = s;
    }

    public void setState(String s) {
        state = s;
    }

    public void setTmpInfo(String s) {
        tmpInfo = s;
    }

    public String getOathToken() {
        return oathToken;
    }

    public String getOrgId() {
        return orgId;
    }

    public String getState() {
        return state;
    }

    public String getTmpInfo() {
        return tmpInfo;
    }
}
