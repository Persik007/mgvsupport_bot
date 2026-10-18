package storage;

/**
 * Класс, реализующий тикеты для пользователя
 */
public class Ticket {
    /**
     * Инстанс пустого тикета
     */
    public static final Ticket EMPTY = new Ticket(0, "", "");

    /**
     * ID тикета
     */
    private long id;

    /**
     * Тема тикета
     */
    private String topic;

    /**
     * Описание тикета
     */
    private String description;

    /**
     * Конструктор тикета
     * @param id айди билет
     * @param topic тема билета
     * @param description описание проблемы билета
     */
    public Ticket(long id, String topic, String description){
        this.id = id;
        this.topic = topic;
        this.description = description;
    }

    /**
     * Возвращает тему тикета
     * @return тема тикета
     */
    public String getTopic(){
        return this.topic;
    }

    /**
     * Возвращает айди билета
     * @return айди
     */
    public long getId(){
        return this.id;
    }

    /**
     * Возвращает айди билета
     * @return айди
     */
    public String getDescription(){
        return this.description;
    }

    /**
     * Меняют билеты
     * @param another билет чьи данные мы должны взять
     */
    public void update(Ticket another){
        this.topic = another.topic;
        this.description = another.description;
    }

    /**
     * меняет описание
     * @param description описание тикета
     */
    public void changeDescription(String description){
        this.description = description;
    }

    /**
     * меняет тему тикета
     * @param topic тему тикета
     */
    public void changeTopic(String topic){
        this.topic = topic;
    }
}
