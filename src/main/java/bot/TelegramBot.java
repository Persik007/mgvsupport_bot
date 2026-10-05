package bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Класс для работы с Telegram
 * Получает сообщения от юзера и отправляет ему ответы
 */
public class TelegramBot extends TelegramLongPollingBot {

    /**
     * Имя бота 
     */
    private final String botUsername;

    /**
     * Логика ответов бота
     */
    private final BotLogic botLogic;

    /**
     * Создаёт бота.
     * @param botUsername имя бота
     * @param botToken токен 
     */
    public TelegramBot(String botUsername, String botToken) {
        super(botToken);
        this.botUsername = botUsername;
        this.botLogic = new BotLogic();
    }

    /**
     * Вызывается, когда пользователь присылает сообщение, и отправляет ответ
     * @param update новое событие из Telegram
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            String responseText = botLogic.getAnswer(messageText);
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(responseText);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                System.err.println("Ошибка при отправке сообщения: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    /**
     * Возвращает имя бота
     * @return имя бота
     */
    @Override
    public String getBotUsername() {
        return botUsername;
    }
}