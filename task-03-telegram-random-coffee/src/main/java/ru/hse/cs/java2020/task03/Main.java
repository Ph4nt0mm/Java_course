package ru.hse.cs.java2020.task03;

import org.glassfish.grizzly.utils.Pair;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    static final String BOT_TOKEN = "608443447:AAEOm0etZfuo0TD6aI_b63Y7Tl3Xx5kPqcg";
    static final String BOT_NAME = "AIBot";
    static final String TRACKER_TOKEN = "AgAAAAAEHRaeAAZa1OL5ToMVW0NBm4KDvb6uoPk";
    static final String TRACKER_ID = "3950619";
    static final long TIME_UPDATE = 100L;

    private static DataBase dataBase = null;
    private static Bot bot = null;
    private static Tracker tracker = null;

    enum States {
        LOGINING,
        IN_MAIN_MENU,
        ADDING_TASK,
        GETTING_TASKS,
        CHECKING_TASK,
        ENTERING_OA_TOKEN,
        ENTERING_ORG_ID,
        ENTERING_QUEUE,
        ENTERING_NAME,
        ENTERING_DESCRIPTION,
        ENTERING_RELEASER,
    }

    public static void main(String[] args) {
        tracker = new Tracker();
        bot = Bot.init(BOT_TOKEN, BOT_NAME);
        dataBase = new DataBase();

        while (true) {
            try {
                Thread.sleep(TIME_UPDATE);
                List<ArrayList<String>> updates = bot.getUpdates();
                for (ArrayList<String> update : updates) {
                    maessageManage(update);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void maessageManage(ArrayList<String> message) throws SQLException, MalformedURLException {
        String chatId = message.get(0);
        String messageText = message.get(1);
        DataBaseResponse chatInfo = dataBase.getInfo(chatId);

        if (chatInfo == null) {
            dataBase.addNewChart(chatId);
            bot.sendMsg(chatId, "Authorise", new ArrayList<String>(Arrays.asList("OAuth", "Org-id")));
        } else {
            switch (States.valueOf(chatInfo.getState())) {
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
                            bot.sendMsg(chatId, "Welcome", new ArrayList<>(Arrays.asList("Add task",
                                    "Get tasks",
                                    "Check task")));
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
                            bot.sendMsg(chatId, "get?", new ArrayList<>(Arrays.asList("get", "get")));
                            getNextTaskPage(chatId, null, chatInfo);
                            break;
                        case "Check task":
                            dataBase.updateChart(chatId, "statem", "CHECKING_TASK");
                            bot.sendMsg(chatId, "Enter task id", null);
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
                            tracker.createTask(chatInfo.getTmpInfo(), chatInfo.getOathToken(), chatInfo.getOrgId());
                            dataBase.updateChart(chatId, "tmp_info", "NULL");
                            dataBase.updateChart(chatId, "statem", "IN_MAIN_MENU");
                            bot.sendMsg(chatId, "Welcome", new ArrayList<>(Arrays.asList("Add task",
                                    "Get tasks",
                                    "Check task")));
                            break;
                        default:
                            bot.sendMsg(chatId, "You have entered something wrong", null);
                            break;
                    }
                    break;
                case GETTING_TASKS:
                    String nextPage;
                    if (messageText.equals("get")) {
                        getNextTaskPage(chatId, null, chatInfo);
                    } else {
                        nextPage = chatInfo.getTmpInfo();
                        if (!nextPage.equals("no") && messageText.equals("next")) {
                            getNextTaskPage(chatId, nextPage, chatInfo);
                        } else {
                            getNextTaskPage(chatId, "back", chatInfo);
                        }
                    }
                    break;
                case CHECKING_TASK:
                    getTask(chatId, messageText, chatInfo);
                    dataBase.updateChart(chatId, "statem", "IN_MAIN_MENU");
                    bot.sendMsg(chatId, "Welcome", new ArrayList<>(Arrays.asList("Add task",
                            "Get tasks",
                            "Check task")));
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

    private static void addJsonToTmpInfoField(String chatId, String currentJson,
                                          String field, String value,
                                              DataBaseResponse chatInfo) throws SQLException, MalformedURLException {
        if (field.equals("assignee") && value.equals("self")) {
            value = tracker.getSelfUid(chatInfo.getOathToken(), chatInfo.getOrgId());
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

    static void getNextTaskPage(String chatId, String todo, DataBaseResponse chatInfo) throws MalformedURLException, SQLException {
        PageJobRet pageJobres;
        if (todo == null || todo != "back") {
            pageJobres = tracker.requestPageJobs(todo, chatInfo.getOathToken(), chatInfo.getOrgId());
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
            dataBase.updateChart(chatId, "tmp_info", "NULL");
            dataBase.updateChart(chatId, "statem", "IN_MAIN_MENU");
            bot.sendMsg(chatId, "Welcome", new ArrayList<>(Arrays.asList("Add task",
                    "Get tasks",
                    "Check task")));
        }
    }

    static void getTask(String chatId, String taskId, DataBaseResponse chatInfo) throws MalformedURLException {
        TaskInfo toSend = tracker.getTask(taskId, chatInfo.getOathToken(), chatInfo.getOrgId());
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
