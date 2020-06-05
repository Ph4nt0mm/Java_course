package ru.hse.cs.java2020.task03;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.ArrayList;
import java.util.List;


public class Bot extends TelegramLongPollingBot {
    private static String token = null;
    private static List<ArrayList<String>> updates = new ArrayList<ArrayList<String>>();
    private static String username = null;


    public static Bot init(String tgToken, String tgUsrname) {
        token = tgToken;
        username = tgUsrname;

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        Bot bot = new Bot();

        try {
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            System.out.println("err");
        }
        return bot;
    }

    public List<ArrayList<String>> getUpdates() {
        ArrayList<ArrayList<String>> tmp = new ArrayList<ArrayList<String>>(updates);
        updates.clear();
        return tmp;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            // Проверяем вошел ли пользователь
            ArrayList<String> message = new ArrayList<>();
            message.add(update.getMessage().getChatId().toString());
            message.add(update.getMessage().getText());
            updates.add(message);
        }
    }

    public synchronized void sendMsg(String chatId, String s, ArrayList<String> buttons) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(s);

        if (buttons != null) {
            setButtons(sendMessage, buttons);
        }

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private synchronized void setButtons(SendMessage sendMessage, ArrayList<String> buttons) {
        // Создаем клавиуатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строчка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Добавляем кнопки в первую строчку клавиатуры
        for (String s : buttons) {
            keyboardFirstRow.add(new KeyboardButton(s));
        }

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
//        keyboard.add(keyboardSecondRow);
        // и устанваливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

}
