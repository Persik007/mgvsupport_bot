package storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;


/**
 * Тесты для класса Storage
 */
public class StorageTest {
    /**
     * Storage для хранения тикетов
     */
    private TicketStorage storage;


    /**
     * Метод, который создает каждый раз хранилище и два тикета в нем
     */
    @BeforeEach
    public void SetUp(){
        storage = new TicketStorage();
        storage.addUserTicket(1,  "hellooooo");
        storage.addUserTicket(1,  "byeeeee");

    }

    /**
     * Метод для выдачи аргументов для теста на проверку получения каждого тикета
     * Чтобы воспользовать функциональностью @ParameterizedTest, куда можно передавать аргументы, нужно чтобы метод, выдающий аргументы был static, иначе не сработает
     */
    private static List<Arguments> provideCommandGetUserTicket(){
        return List.of(
                Arguments.of(1, 1, "hello", "hellooooo"),
                Arguments.of(1, 2, "bye", "byeeeee")
        );
    }

    /**
     * Тест, проверяющий, что тикеты сохраннненые в хранилище,
     */
    @ParameterizedTest
    @MethodSource("provideCommandGetUserTicket")
    void testCommandGetUserTicket(long chatId, long ticketId, String topic, String description) {
        Ticket ticket1 =  storage.getUserTicket(chatId, ticketId).get();
        Assertions.assertEquals(description, ticket1.getDescription());
    }

    /**
     * Тест, делающий проверку удаления тикета
     */
    @Test
    void testDeleteTicket() {
        storage.deleteUserTicket(1, 2);
        Assertions.assertEquals(1, storage.getAllUserTickets(1L).size());
        Assertions.assertIterableEquals(List.of(new Ticket(1, "hellooooo")), storage.getAllUserTickets(1L));
    }

    /**
     * Тест, делающий проверку создания тикета
     */
    @Test
    void addNewTicket() {
        storage.addUserTicket(1, "and ever");
        Assertions.assertEquals(3, storage.getAllUserTickets(1L).size());
        Assertions.assertIterableEquals(List.of(
                new Ticket(1, "hellooooo"),
                new Ticket(2, "byeeeee"),
                new Ticket(3, "and ever")), storage.getAllUserTickets(1L));
    }
}


