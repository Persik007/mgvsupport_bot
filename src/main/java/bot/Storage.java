package bot;

import java.util.*;


/**
 * Класс, реализующий хранение тикетов
 */
public class Storage {
    private Map<Integer, Map<Integer, Ticket>> tickets;

    public Storage(){
        this.tickets = new HashMap<>();
    }

    /**
     * Метод, возвращающий все билеты пользователя
     * @param chatId айди чата пользователя
     * @return лист билетов
     */
    public List<Ticket> getAllUserTickets(int chatId){
        return tickets.get(chatId);
    }

    /**
     * Метод, возвращающий билет пользователя
     * @param chatId айди чата пользователя,
     * @param ticketId айди билета
     * @return билет
     */
    public Ticket getUserTicker(int chatId, int ticketId){
        return tickets.get(chatId).get(ticketId);
    }

    public void deleteUserTicket(int chatId, int ticketId){
        tickets.remove(chatId);
    }

    public void putUserTicket(int chatId, )
}
