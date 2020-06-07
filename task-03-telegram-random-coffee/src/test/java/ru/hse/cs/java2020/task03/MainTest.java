package ru.hse.cs.java2020.task03;

import org.glassfish.grizzly.utils.Pair;
import org.junit.Test;
import org.junit.jupiter.api.Tag;

import java.util.*;

import static org.junit.Assert.*;

public class MainTest {
    class BotModifiered extends Bot {
        private List<Pair<String, String>> sendedMessage = new ArrayList<>();
        private List<String> sendedButtons;
        private List<ArrayList<String>> updates = new ArrayList<>();

        public synchronized void sendMsg(String chatId, String s, ArrayList<String> buttons) {
            Pair<String, String> toAdd = new Pair<>();
            toAdd.setFirst(chatId);
            toAdd.setSecond(s);
            sendedMessage.add(toAdd);
            sendedButtons = buttons;
        }

        public List<ArrayList<String>> getUpdates() {
            ArrayList<ArrayList<String>> tmp = new ArrayList<ArrayList<String>>(updates);
            updates.clear();
            return tmp;
        }

        public void addUpdate(String chatId, String text) {
            ArrayList<String> message = new ArrayList<>();
            message.add(chatId);
            message.add(text);
            updates.add(message);
        }
    }

    class MassageManagerModi extends MassageManager {
        public void run(BotModifiered bot) {
            try {
                Thread.sleep(TIME_UPDATE);
                List<ArrayList<String>> updates = bot.getUpdates();
                for (ArrayList<String> update : updates) {
                    maessageManage(update);
                }
            } catch (Exception e) {
                System.out.println("program ended");
                e.printStackTrace();
            }
        }
    }

    class DataBaseModi extends DataBase {
        private DataBaseResponse dataBaseResponse = new DataBaseResponse();
        private String chatId;
        private String changeField;
        private String changeData;

        public void setResponse(DataBaseResponse response) {
            dataBaseResponse = response;
        }

        @Override
        public DataBaseResponse getInfo(String chatId) {
            if (chatId.equals("1")) {
                return dataBaseResponse;
            } else {
                return null;
            }
        }

        @Override
        public void addNewChart(String chatId) {
            this.chatId = chatId;
        }

        @Override
        public void updateChart(String chatId, String changeField, String changeData)  {
            this.chatId = chatId;
            this.changeField = changeField;
            this.changeData = changeData;
        }
    }

/*                  Tests begun!                */

    @Test
    @Tag("test")
    public void testTrueIsTrue() {
        System.out.println("this part is working");
        assertTrue(true);
    }

    @Test
    @Tag("pretests")
    public void testBotEmulator() {
        BotModifiered b = new BotModifiered();
        b.addUpdate("1", "new mess");
        List<ArrayList<String>> up = b.getUpdates();
        assertEquals("1", up.get(0).get(0));
        assertEquals("new mess", up.get(0).get(1));
    }

    @Test
    @Tag("pretests")
    public void testDataBaseEmulator() {
        DataBaseModi db = new DataBaseModi();
        DataBaseResponse response = new DataBaseResponse();
        response.setOathToken("1");
        response.setOrgId("2");
        response.setTmpInfo("3");
        response.setState("IN_MAIN_MENU");
        db.setResponse(response);
        assertEquals("1", db.getInfo("1").getOathToken());
        assertEquals("2", db.getInfo("1").getOrgId());
        assertEquals("3", db.getInfo("1").getTmpInfo());
        assertEquals("IN_MAIN_MENU", db.getInfo("1").getState());
        assertNull(db.getInfo("3"));
    }

    @Test
    @Tag("tracker")
    public void initData() {
        DataBaseModi dataBaseModi = new DataBaseModi();
        BotModifiered botModifiered = new BotModifiered();
        MassageManagerModi massageManagerModi = new MassageManagerModi();
        massageManagerModi.init(dataBaseModi, botModifiered, new Tracker());

        // Not existing chart
        botModifiered.addUpdate("75", "text");
        massageManagerModi.run(botModifiered);
        assertEquals("75", botModifiered.sendedMessage.get(0).getFirst());
        assertEquals("Authorise", botModifiered.sendedMessage.get(0).getSecond());
        assertEquals("OAuth", botModifiered.sendedButtons.get(0));
        assertEquals("Org-id", botModifiered.sendedButtons.get(1));
    }

    @Test
    @Tag("tracker")
    public void getListTaskTwoPages() {
        DataBaseModi dataBaseModi = new DataBaseModi();
        BotModifiered botModifiered = new BotModifiered();
        MassageManagerModi massageManagerModi = new MassageManagerModi();
        massageManagerModi.init(dataBaseModi, botModifiered, new Tracker());

        // Setup chart
        botModifiered.addUpdate("1", "Get tasks");
        DataBaseResponse dataBaseResponse = new DataBaseResponse();
        dataBaseResponse.setState("IN_MAIN_MENU");
        dataBaseResponse.setOrgId("3950619");
        dataBaseResponse.setOathToken("AgAAAAAEHRaeAAZa1OL5ToMVW0NBm4KDvb6uoPk");
        dataBaseModi.setResponse(dataBaseResponse);
        // Get response
        massageManagerModi.run(botModifiered);

        // Check response
        List<Pair<String, String>> expectationMessage = new ArrayList<>();
        expectationMessage.add(new Pair<>("1", "ORG-13 full"));
        expectationMessage.add(new Pair<>("1", "ORG-10 min"));
        expectationMessage.add(new Pair<>("1", "ORG-16 jh"));
        expectationMessage.add(new Pair<>("1", "Get next?"));

        List<String> expectationButtons = new ArrayList<>();
        expectationButtons.add("next");
        expectationButtons.add("back");

        for (Integer i = 0; i < expectationMessage.size(); i++) {
            assertEquals(expectationMessage.get(i).getFirst(), botModifiered.sendedMessage.get(i).getFirst());
            assertEquals(expectationMessage.get(i).getSecond(), botModifiered.sendedMessage.get(i).getSecond());
        }
        assertEquals(expectationButtons, botModifiered.sendedButtons);

        botModifiered.sendedMessage.clear();
        expectationMessage.clear();
        // second page
        botModifiered.addUpdate("1", "next");
        dataBaseResponse.setState("GETTING_TASKS");
        dataBaseResponse.setTmpInfo(dataBaseModi.changeData);
        dataBaseModi.setResponse(dataBaseResponse);
        massageManagerModi.run(botModifiered);
        // Check response
        expectationMessage.add(new Pair<>("1", "ONER-3 full add"));
        expectationMessage.add(new Pair<>("1", "ORG-9 no description"));
        expectationMessage.add(new Pair<>("1", "ONER-1 no comments"));
        expectationMessage.add(new Pair<>("1", "Get next?"));

        for (Integer i = 0; i < expectationMessage.size(); i++) {
            assertEquals(expectationMessage.get(i).getFirst(), botModifiered.sendedMessage.get(i).getFirst());
            assertEquals(expectationMessage.get(i).getSecond(), botModifiered.sendedMessage.get(i).getSecond());
        }
        assertEquals(expectationButtons, botModifiered.sendedButtons);
    }

    @Test
    @Tag("tracker")
    public void getFullTask() {
        DataBaseModi dataBaseModi = new DataBaseModi();
        BotModifiered botModifiered = new BotModifiered();
        MassageManagerModi massageManagerModi = new MassageManagerModi();
        massageManagerModi.init(dataBaseModi, botModifiered, new Tracker());

        // Setup chart
//        botModifiered.addUpdate("1", "Get tasks");
        DataBaseResponse dataBaseResponse = new DataBaseResponse();
        dataBaseResponse.setState("CHECKING_TASK");
        dataBaseResponse.setOrgId("3950619");
        dataBaseResponse.setOathToken("AgAAAAAEHRaeAAZa1OL5ToMVW0NBm4KDvb6uoPk");
        dataBaseModi.setResponse(dataBaseResponse);


        botModifiered.addUpdate("1", "ORG-13");
        // Get response
        massageManagerModi.run(botModifiered);

        // Check response
        List<Pair<String, String>> expectationMessage = new ArrayList<>();
        expectationMessage.add(new Pair<>("1", "Task name: full"));
        expectationMessage.add(new Pair<>("1", "Task author: Fedor Tropin"));
        expectationMessage.add(new Pair<>("1", "Task executor: Fedor Tropin"));
        expectationMessage.add(new Pair<>("1", "Task description: descrip"));
        expectationMessage.add(new Pair<>("1", "Comments:\nfirst\nsecond\n"));
        expectationMessage.add(new Pair<>("1", "Watchers:\nFedor Tropin\nKotik IsInterneta\n"));
        expectationMessage.add(new Pair<>("1", "Welcome to menu"));

        List<String> expectationButtons = new ArrayList<>();
        expectationButtons.add("Add task");
        expectationButtons.add("Get tasks");
        expectationButtons.add("Check task");
        expectationButtons.add("Exit");

        for (Integer i = 0; i < expectationMessage.size(); i++) {
            assertEquals(expectationMessage.get(i).getFirst(), botModifiered.sendedMessage.get(i).getFirst());
            assertEquals(expectationMessage.get(i).getSecond(), botModifiered.sendedMessage.get(i).getSecond());
        }
//        assertTrue(isEqual);
        assertEquals(expectationButtons, botModifiered.sendedButtons);
    }

    @Test
    @Tag("tracker")
    public void getMinTask() {
        DataBaseModi dataBaseModi = new DataBaseModi();
        BotModifiered botModifiered = new BotModifiered();
        MassageManagerModi massageManagerModi = new MassageManagerModi();
        massageManagerModi.init(dataBaseModi, botModifiered, new Tracker());

        // Setup chart
//        botModifiered.addUpdate("1", "Get tasks");
        DataBaseResponse dataBaseResponse = new DataBaseResponse();
        dataBaseResponse.setState("CHECKING_TASK");
        dataBaseResponse.setOrgId("3950619");
        dataBaseResponse.setOathToken("AgAAAAAEHRaeAAZa1OL5ToMVW0NBm4KDvb6uoPk");
        dataBaseModi.setResponse(dataBaseResponse);

        botModifiered.addUpdate("1", "ORG-10");
        // Get response
        massageManagerModi.run(botModifiered);
        // Check response
        List<Pair<String, String>> expectationMessage = new ArrayList<>();
        expectationMessage.add(new Pair<>("1", "Task name: min"));
        expectationMessage.add(new Pair<>("1", "Task author: Fedor Tropin"));
        expectationMessage.add(new Pair<>("1", "Task executor: Fedor Tropin"));
        expectationMessage.add(new Pair<>("1", "Welcome to menu"));

        List<String> expectationButtons = new ArrayList<>();
        expectationButtons.add("Add task");
        expectationButtons.add("Get tasks");
        expectationButtons.add("Check task");
        expectationButtons.add("Exit");

        for (Integer i = 0; i < expectationMessage.size(); i++) {
            assertEquals(expectationMessage.get(i).getFirst(), botModifiered.sendedMessage.get(i).getFirst());
            assertEquals(expectationMessage.get(i).getSecond(), botModifiered.sendedMessage.get(i).getSecond());
        }
//        assertTrue(isEqual);
        assertEquals(expectationButtons, botModifiered.sendedButtons);
    }
}
