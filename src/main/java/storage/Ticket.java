package storage;

/**
 * Класс, реализующий тикеты для пользователя
 */
public class Ticket {
    /**
     * ID тикета
     */
    private final long id;

    /**
     * Айди чата пользователя, который создал тикет
     */
    private final long ownerId;

    /**
     * Описание тикета
     */
    private String description;

    /**
     * Статус тикета
     */
    private TicketStatus status = TicketStatus.OPEN;

    /**
     * Конструктор тикета
     * @param id айди билета
     * @param ownerId айди чата автора билета
     * @param description описание проблемы билета
     */
    public Ticket(long id, long ownerId, String description){
        this.id = id;
        this.ownerId = ownerId;
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
     * Возвращает айди чата автора билета
     * @return айди чата автора
     */
    public long getOwnerId(){
        return this.ownerId;
    }

    /**
     * Возвращает описание билета
     * @return описание
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

    /**
     * Возвращает статус билета
     * @return статус
     */
    public TicketStatus getStatus(){
        return this.status;
    }

    /**
     * меняет статус
     * @param status новый статус тикета
     */
    public void changeStatus(TicketStatus status){
        this.status = status;
    }
}
