package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

@Service
public class NotificationService {
    private TelegramBot telegramBot;
    private final NotificationTaskRepository taskRepository;

    public NotificationService(TelegramBot telegramBot, NotificationTaskRepository taskRepository) {
        this.telegramBot = telegramBot;
        this.taskRepository = taskRepository;
    }

    public void sendNotification(long chatId, String text) {
        SendMessage message = new SendMessage(chatId, text);
        telegramBot.execute(message);
    }

    public void sendErrorMessage(long chatId) {
        String errorMessage = "Некорректный формат сообщения. Пожалуйста, используйте формат 'дд.мм.гггг чч:мм Текст напоминания'.";
        sendNotification(chatId, errorMessage);
    }

    public void sendWelcomeMessage(long chatId) {
        String welcomeText = "Привет! Я бот, и я готов помочь тебе.";
        sendNotification(chatId, welcomeText);
    }
}
