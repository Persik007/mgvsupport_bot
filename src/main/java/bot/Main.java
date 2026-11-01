package bot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import io.github.cdimascio.dotenv.Dotenv;
import storage.Storage;

/**
 * Читает имя и токен бота из файла .env и запускает телеграм-бота.
 */
public class Main {

    /**
     * Метод, создающий нового бота и получающий ник бота и его токен.
     * @param args аргументы командной строки (не используются)
     * @throws TelegramApiException если не удалось подключиться к Telegram
     */
    public static void main(String[] args) throws TelegramApiException {
        Dotenv dotenv = Dotenv.load();
        String botUsername = dotenv.get("BOT_NAME");
        String botToken = dotenv.get("BOT_TOKEN");

        Storage storage = new Storage();
        BotLogic botLogic = new BotLogic(storage);

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new TelegramBot(botUsername, botToken, botLogic));
    }
}