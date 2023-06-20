package pro.sky.telegrambot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambot.model.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.NotificationService;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TelegramBotUpdatesListenerTests {
    private TelegramBotUpdatesListener telegramBotUpdatesListener;
    @Mock
    private NotificationService notificationService;
    @Mock
    private NotificationTaskRepository taskRepository;
    @Mock
    private TelegramBot telegramBot;

    @BeforeEach
    public void init() {
        telegramBotUpdatesListener = new TelegramBotUpdatesListener(telegramBot, taskRepository, notificationService);
    }

    @Test
    void process_skipNullMessage() {
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        String text = null;
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn(text);
        telegramBotUpdatesListener.process(List.of(update));
        verifyNoInteractions(taskRepository);
    }

    @Test
    void process_sendWelcomeMessage() {
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        Chat chat = Mockito.mock(Chat.class);
        String text = "/start";
        long chatId = 1238L;
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn(text);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(chatId);
        telegramBotUpdatesListener.process(List.of(update));
        verifyNoInteractions(taskRepository);
        verify(notificationService).sendWelcomeMessage(chatId);
    }

    @Test
    void process_taskSave() {
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        Chat chat = Mockito.mock(Chat.class);
        String text = "19.06.2023 17:45 Сделать домашнюю работу";
        long chatId = 1238L;
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn(text);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(chatId);
        telegramBotUpdatesListener.process(List.of(update));
        verify(notificationService).sendNotification(chatId, "Напоминание успешно создано.");
        verify(taskRepository).save(any(NotificationTask.class));
    }

    @Test
    void process_sendErrorMessage() {
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        Chat chat = Mockito.mock(Chat.class);
        String text = "06.19.2023 17:45 Сделать домашнюю работу";
        long chatId = 1238L;
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn(text);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(chatId);
        telegramBotUpdatesListener.process(List.of(update));
        verifyNoInteractions(taskRepository);
        verify(notificationService).sendErrorMessage(chatId);
    }

}
