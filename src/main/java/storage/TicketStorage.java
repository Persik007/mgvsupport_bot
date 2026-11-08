package storage;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Класс, реализующий хранение тикетов
 */
public class TicketStorage {

    /**
     * Хранение тикетов(слоаврь словарей)
     * chatId -> (номер тикета -> содержание тикета)
     */
    private final Map<Long, Map<Long, Ticket>> tickets;

    /**
     * Отвечает за просчитывание ID
     */
    private final AtomicLong counter = new AtomicLong(0);

    /**
     * Конструктор хранилища
     */
    public TicketStorage(){
        this.tickets = new HashMap<>();
    }

    /**
     * Метод, возвращающий все билеты пользователя
     * @param chatId айди чата пользователя
     * @return лист билетов
     */
    public Collection<Ticket> getAllUserTickets(Long chatId){
         return tickets.getOrDefault(chatId, Map.of()).values();
    }

    /**
     * Метод, возвращающий билет пользователя
     * @param chatId айди чата пользователя
     * @param ticketId айди билета
     * @return билет или null, если такого билета нет
     */
    public Optional<Ticket> getUserTicket(long chatId, long ticketId) {
        return Optional.ofNullable(tickets.getOrDefault(chatId, Map.of()).get(ticketId));
    }

    /**
     * Метод, удаляющий тикет по айди
     * @param chatId айди чата пользователя,
     * @param ticketId айди билета
     */
    public void deleteUserTicket(long chatId, long ticketId){
        Map<Long, Ticket> chatTickets = tickets.get(chatId);
        if (chatTickets != null)
            chatTickets.remove(ticketId);
    }

    /**
     * Метод, удаляющий тикет по айди
     * @param chatId айди чата пользователя,
     * @param description описание тикета,
     */
    public long addUserTicket(long chatId, String description){
        long ticketId = counter.addAndGet(1);
        Ticket ticket = new Ticket(ticketId, description);
        tickets.computeIfAbsent(chatId, k -> new HashMap<>()).put(ticketId, ticket);
        return ticketId;
    }
}
