package ru.hse.cs.java2020.task03;

public class Main {
    static final String BOT_TOKEN = "608443447:AAEOm0etZfuo0TD6aI_b63Y7Tl3Xx5kPqcg";
    static final String BOT_NAME = "AIBot";
    static final String TRACKER_TOKEN = "AgAAAAAEHRaeAAZa1OL5ToMVW0NBm4KDvb6uoPk";
    static final String TRACKER_ID = "3950619";

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
        DataBase dataBase = new DataBase("jdbc:postgresql://localhost:5432/postgres", "postgres", " ");
        Bot bot = Bot.init(BOT_TOKEN, BOT_NAME);
        MassageManager massageManager = new MassageManager();
        massageManager.init(dataBase, bot, new Tracker());
        massageManager.run();

        dataBase.stop();
        System.out.println("Application Terminating ...");
    }
}
