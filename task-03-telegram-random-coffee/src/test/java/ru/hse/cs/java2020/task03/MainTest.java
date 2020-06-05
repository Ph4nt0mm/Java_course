//package ru.hse.cs.java2020.task03;
//
//import org.junit.Test;
//import org.junit.jupiter.api.Tag;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//import java.util.ArrayList;
//
//import static org.junit.Assert.assertTrue;
//
//public class MainTest {
//    class botModifiered extends Bot {
//        private
//        @Override
//        public synchronized void sendMsg(String chatId, String s, ArrayList<String> buttons) {
//            SendMessage sendMessage = new SendMessage();
//            sendMessage.enableMarkdown(true);
//            sendMessage.setChatId(chatId);
//            sendMessage.setText(s);
//
//            if (buttons != null) {
//                setButtons(sendMessage, buttons);
//            }
//
//            try {
//                execute(sendMessage);
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//    @Test
//    @Tag("test")
//    public void testTrueIsTrue() {
//        System.out.println("this part is working");
//        assertTrue(true);
//    }
//
//    @Test
//    @Tag("tracker")
//    public void initData() {
//
//    }
//
//    @Test
//    @Tag("tracker")
//    public void getFullTask() {
//
//    }
//
//    @Test
//    @Tag("tracker")
//    public void getMinTask() {}
//
//    @Test
//    @Tag("tracker")
//    public void getNotExistingTask() {}
//
//    @Test
//    @Tag("tracker")
//    public void getTaskListTwoLists() {}
//
//    @Test
//    @Tag("tracker")
//    public void getTaskList() {}
//
//    @Test
//    @Tag("tracker")
//    public void createTask() {}
//
//    @Test
//    @Tag("tracker")
//    public void checkCreatedTask() {}
//
//    @Test
//    @Tag("tracker")
//    public void createTaskWithSelf() {}
//
//    @Test
//    @Tag("tracker")
//    public void checkCreatedTaskWithSelf() {}
//
//    @Test
//    @Tag("tracker")
//    public void createTaskWithWrongData() {}
//
//    @Test
//    @Tag("tracker")
//    public void getTaskByWrongData() {}
//
//}
