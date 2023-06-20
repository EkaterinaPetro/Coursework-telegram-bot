package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.NotificationService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;
    private final NotificationTaskRepository taskRepository;
    private final NotificationService notificationService;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskRepository taskRepository, NotificationService notificationService) {
        this.telegramBot = telegramBot;
        this.taskRepository = taskRepository;
        this.notificationService = notificationService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            if (update.message().text() == null) {
                logger.warn("Skip message because text is null");
                return;
            }
            // Process your updates here
            if (update.message().text().equals("/start")) {
                long chatId = update.message().chat().id();
                notificationService.sendWelcomeMessage(chatId);
            } else {
                processUserMessage(update.message());
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void processUserMessage(Message message) {
        String text = message.text();
        String regex = "([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.matches()) {
            String dateTimeStr = matcher.group(1);
            String taskText = matcher.group(3);

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);

                NotificationTask task = new NotificationTask();
                task.setDateTime(dateTime);
                task.setMessageText(taskText);
                long chatId = message.chat().id();
                task.setChatId(chatId);

                taskRepository.save(task);

                notificationService.sendNotification(chatId, "Напоминание успешно создано.");
            } catch (DateTimeParseException e) {
                logger.error("Error parsing date/time: {}", e.getMessage());
                notificationService.sendErrorMessage(message.chat().id());
            }
        } else {
            notificationService.sendErrorMessage(message.chat().id());
        }
    }
}
