package storage;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Класс, реализующий хранение тикетов
 */
public class Storage {

    /**
     * Хранение тикетов
     */
    private Map<Long, Map<Long, Ticket>> tickets;

    /**
     * Отвечает за просчитывание ID
     */
    private final AtomicLong counter = new AtomicLong(0);

    /**
     * Конструктор хранилища
     */
    public Storage(){
        this.tickets = new HashMap<>();
    }

    /**
     * Метод, возвращающий все билеты пользователя
     * @param chatId айди чата пользователя
     * @return лист билетов
     */
    public List<Ticket> getAllUserTickets(Long chatId){
        return Optional.ofNullable(
                tickets.get(chatId))
                .map(it -> new ArrayList<>(it.values()))
                .orElse(new ArrayList<>());
    }

    /**
     * Метод, возвращающий билет пользователя
     * @param chatId айди чата пользователя,
     * @param ticketId айди билета
     * @return билет
     */
    public Ticket getUserTicket(long chatId, long ticketId){
        return Optional.ofNullable(
                tickets.get(chatId))
                .map(it -> it.get(ticketId))
                .orElse(Ticket.EMPTY);
    }

    /**
     * Метод, удаляющий тикет по айди
     * @param chatId айди чата пользователя,
     * @param ticketId айди билета
     */
    public void deleteUserTicket(long chatId, long ticketId){
        Optional.ofNullable(tickets.get(chatId))
                .ifPresent(it -> it.remove(ticketId));
    }


    /**
     * Метод, удаляющий тикет по айди
     * @param chatId айди чата пользователя,
     */
    public void addUserTicket(long chatId, String topic, String description){
        long ticketId = counter.addAndGet(1);
        Ticket ticket = new Ticket(ticketId, topic, description);
        tickets.computeIfAbsent(chatId, k -> new HashMap<>()).put(ticketId, ticket);
    }
}
