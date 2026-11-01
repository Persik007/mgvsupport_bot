package storage;

/**
 * Статус обращения: открыто - в работе - закрыто
 */
public enum TicketStatus {
    /**
     * Обращение создано
     */
    OPEN("Открыто"),
    /**
     * Оператор взял обращение в работу
     */
    IN_PROGRESS("В процессе"),
    /**
     * Оператор закрыл обращение
     */
    CLOSED("Закрыто");
    /**
     * Название статуса, которое видит пользователь
     */    
    private final String status;
    /**
     * Конструктор статуса обращения
     * @param status название статуса
     */
    TicketStatus(String status) {
        this.status = status;
    }
    /**
     * Метод, возвращающий название статуса
     * @return название статуса
     */
    public String getStatus() {
        return status;
    }
    
}