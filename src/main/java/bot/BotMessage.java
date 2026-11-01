package bot;

/**
 * Сообщение, которое бот должен отправить: кому и что.
 * Бот может ответить не только тому, кто написал сообещние
 * но и оператор отвечает пользователю
 */
public class BotMessage{
    /**
     * Айди чата получателя
     */
    private final long chatId;
    /**
     * Текст сообщения
     */
    private final String text;
    /**
     * Конструктор сообщения
     * @param chatId айди чата получателя
     * @param text текст сообщения
     */
    public BotMessage(long chatId, String text){
        this.chatId = chatId;
        this.text = text;
    }
    /**
     * Метод, возвращающий айди чата получателя
     * @return айди чата
     */
    public long getChatId() {
        return chatId;
    }
    /**
     * Метод, возвращающий текст сообщения
     * @return текст сообщения
     */
    public String getText() {
        return text;
    }
}