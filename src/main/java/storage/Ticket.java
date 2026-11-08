package storage;

import java.util.Objects;

/**
 * Класс, реализующий тикеты для пользователя
 */
public class Ticket {
    /**
     * ID тикета
     */
    private final long id;

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
     * Override метода equals
     * @param obj объект, который сравниваем
     * @return boolean в зависимости от того, совпали ли они
     */
    @Override
    public boolean equals(Object obj){
        if (this == obj){
            return true;
        }
        else if (obj instanceof Ticket ticket) {
            return ticket.id == this.id && ticket.description.equals(this.description);
        }
        else{
            return false;
        }
    }

    /**
     * Override метода hashCode
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, description);
    }

    /**
     * Override метода toString
     * @return строковое представление
     */
    @Override
    public String toString() {
        return "№" + this.id + " " + this.description;
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
    public void setDescription(String description){
        this.description = description;
    }
}
