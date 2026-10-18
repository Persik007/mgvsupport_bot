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
        ticket = new Ticket(1, "checktest");
    }

    /**
     * Проверяем айдишник
     */
    @Test
    void testGetId() {Assertions.assertEquals(1, ticket.getId());}

    /**
     * Меняем описание тикета
     */
    @Test
    void testChangeDescription() {
        ticket.changeDescription("hello hello");
        Assertions.assertEquals("hello hello", ticket.getDescription());
    }
}
