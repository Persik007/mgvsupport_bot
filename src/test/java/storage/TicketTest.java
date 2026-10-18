package storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TicketTest {

    private Ticket ticket;


    @BeforeEach
    public void SetUp(){
        ticket = new Ticket(1, "checktest");
    }

    @Test
    void testGetId() {

        Assertions.assertEquals(1, ticket.getId());
    }

    @Test
    void testChangeDescription() {
        ticket.changeDescription("hello hello");
        Assertions.assertEquals("hello hello", ticket.getDescription());
    }
}
