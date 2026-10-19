package bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Класс для работы с Telegram.
 * Получает сообщения от юзера и отправляет ему ответы.
 */
public class TelegramBot extends TelegramLongPollingBot {

    /**
     * Имя бота.
     */
    private final String botUsername;

    /**
     * Логика ответов бота.
     */
    private final BotLogic botLogic;

    /**
     * Создаёт бота.
     * @param botUsername имя бота
     * @param botToken токен
     * @param botLogic логика ответов бота
     */
    public TelegramBot(String botUsername, String botToken, BotLogic botLogic) {
        super(botToken);
        this.botUsername = botUsername;
        this.botLogic = botLogic;
    }

    /**
     * Вызывается, когда пользователь присылает сообщение, и отправляет ответ.
     * @param update новое событие из Telegram
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String answer = botLogic.getAnswer(chatId, update.getMessage().getText());
            try {
                execute(new SendMessage(String.valueOf(chatId), answer));
            } catch (TelegramApiException e) {
                System.out.println("Ошибка отправки: " + e.getMessage());
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}