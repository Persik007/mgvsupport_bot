package storage;

/**
 * Класс, реализующий тикеты для пользователя
 */
public class Ticket {
    /**
     * Инстанс пустого тикета
     */
    public static final Ticket EMPTY = new Ticket(0, "");

    /**
     * ID тикета
     */
    private long id;

    /**
     * Описание тикета
     */
    private String description;

    /**
     * Конструктор тикета
     * @param id айди билет
     * @param description описание проблемы билета
     */
    public Ticket(long id, String description){
        this.id = id;
        this.description = description;
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
     * меняет описание
     * @param description описание тикета
     */
    public void changeDescription(String description){
        this.description = description;
    }
}
