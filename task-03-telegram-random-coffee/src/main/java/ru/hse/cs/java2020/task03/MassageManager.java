package ru.hse.cs.java2020.task03;

import org.glassfish.grizzly.utils.Pair;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MassageManager {
    private DataBase dataBase = null;
    private Bot bot = null;
    private Tracker tracker = null;
    static final long TIME_UPDATE = 1000L;

    public void init(DataBase dataBase, Bot bot, Tracker tracker) {
        this.bot = bot;
        this.tracker = tracker;
        this.dataBase = dataBase;
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(TIME_UPDATE);
                System.out.println("!");
                List<ArrayList<String>> updates = bot.getUpdates();
                for (ArrayList<String> update : updates) {
                    maessageManage(update);
                    System.out.println(update.get(0));
                }
            } catch (Exception e) {
                System.out.println("program ended");
                e.printStackTrace();
            }
        }
    }

    void maessageManage(ArrayList<String> message) throws SQLException, MalformedURLException {
        String chatId = message.get(0);
        String messageText = message.get(1);
        DataBaseResponse chatInfo = dataBase.getInfo(chatId);

        if (chatInfo == null) {
            dataBase.addNewChart(chatId);
            bot.sendMsg(chatId, "Authorise", new ArrayList<String>(Arrays.asList("OAuth", "Org-id")));
        } else {
            switch (Main.States.valueOf(chatInfo.getState())) {
                case LOGINING:
                    switch (messageText) {
                        case "OAuth":
                            dataBase.updateChart(chatId, "statem", "ENTERING_OA_TOKEN");
                            bot.sendMsg(chatId, "Enter Oauth token", null);
                            break;
                        case "Org-id":
                            dataBase.updateChart(chatId, "statem", "ENTERING_ORG_ID");
                            bot.sendMsg(chatId, "Enter Org-id", null);
                            break;
                        case "Send":
                            dataBase.updateChart(chatId, "statem", "IN_MAIN_MENU");
                            bot.sendMsg(chatId, "Welcome to menu", new ArrayList<>(Arrays.asList("Add task",
                                    "Get tasks",
                                    "Check task",
                                    "Exit")));
                            break;
                        default:
                            bot.sendMsg(chatId, "You have entered something wrong", null);
                            break;
                    }
                    break;
                case IN_MAIN_MENU:
                    switch (messageText) {
                        case "Add task":
                            dataBase.updateChart(chatId, "statem", "ADDING_TASK");
                            bot.sendMsg(chatId, "Add task data", new ArrayList<>(Arrays.asList("Enter task name",
                                    "Enter task queue",
                                    "Enter task description",
                                    "Enter task executor")));
                            break;
                        case "Get tasks":
                            dataBase.updateChart(chatId, "statem", "GETTING_TASKS");
                            getNextTaskPage(chatId, null, chatInfo);
                            break;
                        case "Check task":
                            dataBase.updateChart(chatId, "statem", "CHECKING_TASK");
                            bot.sendMsg(chatId, "Enter task id", null);
                            break;
                        case "Exit":
                            dataBase.deleteChart(chatId);
                            bot.sendMsg(chatId, "By", null);
                            break;
                        default:
                            bot.sendMsg(chatId, "You have entered something wrong", null);
                            break;
                    }
                    break;
                case ADDING_TASK:
                    if (chatInfo.getTmpInfo() == null) {
                        dataBase.updateChart(chatId, "tmp_info", "{}");
                    }
                    switch (messageText) {
                        case "Enter task name":
                            dataBase.updateChart(chatId, "statem", "ENTERING_NAME");
                            bot.sendMsg(chatId, "Enter task name", null);
                            break;
                        case "Enter task queue":
                            dataBase.updateChart(chatId, "statem", "ENTERING_QUEUE");
                            bot.sendMsg(chatId, "Enter queue name", null);
                            break;
                        case "Enter task description":
                            dataBase.updateChart(chatId, "statem", "ENTERING_DESCRIPTION");
                            bot.sendMsg(chatId, "Enter task description", null);
                            break;
                        case "Enter task executor":
                            dataBase.updateChart(chatId, "statem", "ENTERING_RELEASER");
                            bot.sendMsg(chatId, "Enter \"self\" to set yourelf as executor or anything else "
                                    + "if you want to left this field free", null);
                            break;
                        case "Send":
                            try {
                                tracker.createTask(chatInfo.getTmpInfo(), chatInfo.getOathToken(), chatInfo.getOrgId());
                            } catch (LoginEx e) {
                                toMenu(chatId, "Something wrong with your login data");
                                return;
                            } catch (IOException e) {
                                bot.sendMsg(chatId, "Something went wrong. Try again", null);
                                e.printStackTrace();
                                return;
                            }
                            toMenu(chatId, "Welcome to menu");
                            break;
                        default:
                            bot.sendMsg(chatId, "You have entered something wrong", null);
                            break;
                    }
                    break;
                case GETTING_TASKS:
                    String nextPage;
                    nextPage = chatInfo.getTmpInfo();
                    if (!nextPage.equals("no") && messageText.equals("next")) {
                        getNextTaskPage(chatId, nextPage, chatInfo);
                    } else {
                        getNextTaskPage(chatId, "back", chatInfo);
                    }
                    break;
                case CHECKING_TASK:
                    getTask(chatId, messageText, chatInfo);
                    toMenu(chatId, "Welcome to menu");
                    break;
                case ENTERING_OA_TOKEN:
                    dataBase.updateChart(chatId, "oath", messageText);
                    dataBase.updateChart(chatId, "statem", "LOGINING");
                    bot.sendMsg(chatId, "Enter last data", new ArrayList<>(Arrays.asList("Org-id", "Send")));
                    break;
                case ENTERING_ORG_ID:
                    dataBase.updateChart(chatId, "org_id", messageText);
                    dataBase.updateChart(chatId, "statem", "LOGINING");
                    bot.sendMsg(chatId, "Enter last data", new ArrayList<>(Arrays.asList("OAuth", "Send")));
                    break;
                case ENTERING_QUEUE:
                    addJsonToTmpInfoField(chatId, chatInfo.getTmpInfo(), "queue", messageText, chatInfo);
                    break;
                case ENTERING_NAME:
                    addJsonToTmpInfoField(chatId, chatInfo.getTmpInfo(), "summary", messageText, chatInfo);
                    break;
                case ENTERING_DESCRIPTION:
                    addJsonToTmpInfoField(chatId, chatInfo.getTmpInfo(), "description", messageText, chatInfo);
                    break;
                case ENTERING_RELEASER:
                    addJsonToTmpInfoField(chatId, chatInfo.getTmpInfo(), "assignee", messageText, chatInfo);
                    break;
                default:
                    break;
            }
        }
    }

    private void addJsonToTmpInfoField(String chatId, String currentJson,
                                              String field, String value,
                                              DataBaseResponse chatInfo) throws SQLException, MalformedURLException {
        if (field.equals("assignee") && value.equals("self")) {
            try {
                value = tracker.getSelfUid(chatInfo.getOathToken(), chatInfo.getOrgId());
            } catch (LoginEx e) {
                toMenu(chatId, "Something wrong with your login data");
                return;
            } catch (IOException e) {
                bot.sendMsg(chatId, "Something went wrong. Try again", null);
                e.printStackTrace();
                return;
            }
        }

        JSONObject jsonobject = new JSONObject(currentJson);
        jsonobject.put(field, value);

        dataBase.updateChart(chatId, "tmp_info", jsonobject.toString());
        dataBase.updateChart(chatId, "statem", "ADDING_TASK");

        bot.sendMsg(chatId, "Add task data", new ArrayList<>(Arrays.asList("Enter task name",
                "Enter task queue",
                "Enter task description",
                "Enter task executor",
                "Send")));
    }

    private void getNextTaskPage(String chatId, String todo, DataBaseResponse chatInfo) throws MalformedURLException,
            SQLException {
        PageJobRet pageJobres;
        if (todo == null || todo != "back") {
            try {
                pageJobres = tracker.requestPageJobs(todo, chatInfo.getOathToken(), chatInfo.getOrgId());
            } catch (LoginEx e) {
                toMenu(chatId, "Something wrong with your login data");
                return;
            } catch (IOException e) {
                bot.sendMsg(chatId, "Something went wrong. Try again", null);
                e.printStackTrace();
                return;
            }


            for (Pair<String, String> i : pageJobres.getJobsInfo()) {
                bot.sendMsg(chatId, i.getFirst() + " " + i.getSecond(), null);
            }
            if (pageJobres.getNextPage() != null) {
                dataBase.updateChart(chatId, "tmp_info", pageJobres.getNextPage());
                bot.sendMsg(chatId, "Get next?", new ArrayList<>(Arrays.asList("next", "back")));
            } else {
                dataBase.updateChart(chatId, "tmp_info", "no");
                bot.sendMsg(chatId, "There is no next page, press back", new ArrayList<>(Arrays.asList("back")));
            }
        } else {
            toMenu(chatId, "Welcome to menu");
        }
    }


    private void toMenu(String chatId, String message) throws SQLException {
        bot.sendMsg(chatId, message, new ArrayList<>(Arrays.asList("Add task",
                "Get tasks",
                "Check task",
                "Exit")));
        dataBase.updateChart(chatId, "tmp_info", "NULL");
        dataBase.updateChart(chatId, "statem", "IN_MAIN_MENU");
    }

    private void getTask(String chatId, String taskId, DataBaseResponse chatInfo) throws MalformedURLException, SQLException {
        TaskInfo toSend;
        try {
            toSend = tracker.getTask(taskId, chatInfo.getOathToken(), chatInfo.getOrgId());
        } catch (LoginEx e) {
            toMenu(chatId, "Something wrong with your login data");
            return;
        } catch (IOException e) {
            bot.sendMsg(chatId, "Something went wrong. Try again", null);
            e.printStackTrace();
            return;
        }

        bot.sendMsg(chatId, "Task name: " + toSend.getName(), null);
        bot.sendMsg(chatId, "Task author: " + toSend.getAuthor(), null);
        if (toSend.getExecutor() != null) {
            bot.sendMsg(chatId, "Task executor: " + toSend.getExecutor(), null);
        }
        if (toSend.getDescription() != null) {
            bot.sendMsg(chatId, "Task description: " + toSend.getDescription(), null);
        }
        if (toSend.getComments().size() > 0) {
            String comments = new String();
            comments += "Comments:\n";
            for (String s : toSend.getComments()) {
                comments += s + "\n";
            }
            bot.sendMsg(chatId, comments, null);
        }
        if (toSend.getWatchers().size() > 0) {
            String watchers = new String();
            watchers += "Watchers:\n";
            for (String s : toSend.getWatchers()) {
                watchers += s + "\n";
            }
            bot.sendMsg(chatId, watchers, null);
        }
    }
}
