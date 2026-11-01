package storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Тесты для класса Ticket
 */
public class TicketTest {
    /**
     * Ticket
     */
    private Ticket ticket;

    /**
     * Создаем один ticket, на котором будем проверять функциональность
     */
    @BeforeEach
    public void SetUp(){
        ticket = new Ticket(1, 1, "checktest");
    }

    /**
     * Проверяем айдишник
     */
    @Test
    void testGetId() {Assertions.assertEquals(1, ticket.getId());}

    /**
     * Проверяем айдишник пользователя
     */
    @Test
    void testGetOwnerId() {Assertions.assertEquals(1, ticket.getOwnerId());}

    /**
     * Проверяем описание тикета
     */
    @Test
    void testGetDescription() {Assertions.assertEquals("checktest", ticket.getDescription());}

    /**
     * Меняем описание тикета
     */
    @Test
    void testChangeDescription() {
        ticket.changeDescription("hello hello");
        Assertions.assertEquals("hello hello", ticket.getDescription());
    }

    /**
     * Меняем статус тикета
     */
    @Test
    void testChangeStatus() {
        ticket.changeStatus(TicketStatus.IN_PROGRESS);
        Assertions.assertEquals(TicketStatus.IN_PROGRESS, ticket.getStatus());
    }
}
