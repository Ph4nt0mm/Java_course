package ru.hse.cs.java2020.task03;

import java.util.ArrayList;

public class TaskInfo {
    private String description;
    private String name;
    private String author;
    private String executor;
    private ArrayList<String> watchers = new ArrayList<>();
    private ArrayList<String> comments = new ArrayList<>();

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getExecutor() {
        return executor;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getWatchers() {
        return watchers;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addWatcher(String watcher) {
        watchers.add(watcher);
    }

    public void addComment(String comment) {
        comments.add(comment);
    }
}
