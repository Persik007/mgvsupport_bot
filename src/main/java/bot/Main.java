package bot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Читает имя и токен бота из файла .env и запускает телеграм-бота.
 */
public class Main {

    /**
    * Метод, создающий нового бота и получающий ник бота и его токен
    */
    public static void main() throws TelegramApiException{
        Dotenv dotenv = Dotenv.load();
        String botUsername = dotenv.get("BOT_NAME");
        String botToken = dotenv.get("BOT_TOKEN");

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new TelegramBot(botUsername, botToken));
    }
}