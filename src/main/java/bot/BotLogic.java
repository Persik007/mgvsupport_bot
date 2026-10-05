package bot;

/**
 * Класс, реализующий логику работы бота
 * Получает сообщение от пользователя и возвращает ответ
 */
public class BotLogic {
    /**
     * Сообщение помощи
     */
    private static final String HELP_MESSAGE = "Привет, я эхо-бот. Я повторяю все, что вы напишите. Напишите, любое сообщение. Команда /help покажет эту справку еще раз";

    /**
     * Метод, возвращающий ответ
     * @param message сообщение от пользователя
     * @return ответ бота
     */
    public String getAnswer(String message) {
        if (message.equals("/help") || message.equals("/start")) {
            return HELP_MESSAGE;
        }
        return "Вы набрали " + message;
    }

}
