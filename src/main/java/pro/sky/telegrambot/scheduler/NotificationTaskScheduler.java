package pro.sky.telegrambot.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambot.model.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.NotificationService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificationTaskScheduler {
    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final NotificationTaskRepository taskRepository;
    private final NotificationService notificationService;

    public NotificationTaskScheduler(NotificationTaskRepository taskRepository, NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    private void searchTasksForCurrentMinute() {
        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> tasks = taskRepository.findByDateTime(currentDateTime);
        //logger.info(currentDateTime.toString());
        tasks.forEach(notificationTask -> {
            long chatId = notificationTask.getChatId();
            String messageText = notificationTask.getMessageText();
            notificationService.sendNotification(chatId, messageText);
        });
    }
}
