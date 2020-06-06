package ru.hse.cs.java2020.task03;

import org.glassfish.grizzly.utils.Pair;
import org.junit.Test;
import org.junit.jupiter.api.Tag;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MainTest {
    class BotModifiered extends Bot {
        public ArrayList<Pair<String, String>> sendedMessage = new ArrayList<>();
        public ArrayList<String> sendedButtons;
        private List<ArrayList<String>> updates = new ArrayList<>() {};

        public synchronized void sendMsg(String chatId, String s, ArrayList<String> buttons) {
            Pair<String, String> toAdd = new Pair<>();
            toAdd.setFirst(chatId);
            toAdd.setSecond(s);
            System.out.println(chatId + " " +  s);
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
        DataBaseResponse dataBaseResponse = new DataBaseResponse();
        String chatId;
        String changeField;
        String changeData;

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
    public void getFullTask() {
        DataBaseModi dataBaseModi = new DataBaseModi();
        BotModifiered botModifiered = new BotModifiered();
        MassageManagerModi massageManagerModi = new MassageManagerModi();
        massageManagerModi.init(dataBaseModi, botModifiered, new Tracker());

        // Not existing chart
        botModifiered.addUpdate("1", "Get tasks");
        DataBaseResponse dataBaseResponse = new DataBaseResponse();
        dataBaseResponse.setState("IN_MAIN_MENU");
        dataBaseResponse.setOrgId("3950619");
        dataBaseResponse.setOathToken("AgAAAAAEHRaeAAZa1OL5ToMVW0NBm4KDvb6uoPk");
        dataBaseModi.setResponse(dataBaseResponse);

        massageManagerModi.run(botModifiered);
        System.out.println(botModifiered.sendedMessage.get(0).getFirst());
        System.out.println(botModifiered.sendedMessage.get(0).getSecond());
    }

    @Test
    @Tag("tracker")
    public void getMinTask() {}

    @Test
    @Tag("tracker")
    public void getNotExistingTask() {}

    @Test
    @Tag("tracker")
    public void getTaskListTwoLists() {}

    @Test
    @Tag("tracker")
    public void getTaskList() {}

    @Test
    @Tag("tracker")
    public void createTask() {}

    @Test
    @Tag("tracker")
    public void checkCreatedTask() {}

    @Test
    @Tag("tracker")
    public void createTaskWithSelf() {}

    @Test
    @Tag("tracker")
    public void checkCreatedTaskWithSelf() {}

    @Test
    @Tag("tracker")
    public void createTaskWithWrongData() {}

    @Test
    @Tag("tracker")
    public void getTaskByWrongData() {}

}
