package ru.hse.cs.java2020.task03;

import org.glassfish.grizzly.utils.Pair;

import java.util.ArrayList;

public class PageJobRet {
    private ArrayList<Pair<String, String>> jobsInfo = new ArrayList<>();
    private String nextPage;

    public ArrayList<Pair<String, String>> getJobsInfo() {
        return jobsInfo;
    }

    public String getNextPage() {
        return nextPage;
    }

    public void addJobsInfo(Pair<String, String> p) {
        jobsInfo.add(p);
    }

    public void setNextPage(String s) {
        nextPage = s;
    }
}
