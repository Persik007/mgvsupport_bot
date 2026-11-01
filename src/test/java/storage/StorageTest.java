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
    private Storage storage;


    /**
     * Метод, который создает каждый раз хранилище и два тикета в нем
     */
    @BeforeEach
    public void setUp(){
        storage = new Storage();
        storage.addUserTicket(1,  "hellooooo");
        storage.addUserTicket(1,  "byeeeee");

    }

    /**
     * Тест, проверяющий, что при получении getUserTickets массив тикетов равнялся
     */
    @Test
    void testGetUserTickets() {
        Assertions.assertEquals(2, storage.getAllUserTickets(1L).size());
    }


    /**
     * Метод для выдачи аргументов для теста на проверку получения каждого тикета
     * @return наборы аргументов: айди чата, айди тикета, тема, ожидаемое описание
     */
    private static List<Arguments> provideCommandGetUserTicket(){
        return List.of(
                Arguments.of(1, 1, "hello", "hellooooo"),
                Arguments.of(1, 2, "bye", "byeeeee")
        );
    }

    /**
     * Тест, проверяющий, что тикеты сохраннненые в хранилище,
     * @param chatId айди чата пользователя
     * @param ticketId айди тикета
     * @param topic тема (не используется)
     * @param description ожидаемое описание
     */
    @ParameterizedTest
    @MethodSource("provideCommandGetUserTicket")
    void testCommandGetUserTicket(long chatId, long ticketId, String topic, String description) {
        Ticket ticket1 =  storage.getUserTicket(chatId, ticketId);
        Assertions.assertEquals(description, ticket1.getDescription());
    }

    /**
     * Тест, делающий проверку удаления тикета
     */
    @Test
    void testDeleteTicket() {
        storage.deleteUserTicket(1, 2);
        Assertions.assertEquals(1, storage.getAllUserTickets(1L).size());
    }

    /**
     * Тест, делающий проверку создания тикета
     */
    @Test
    void testAddNewTicket() {
        storage.addUserTicket(1, "and ever");
        Assertions.assertEquals(3, storage.getAllUserTickets(1L).size());
    }

    /**
     * Тест, проверяющий, что getTicket находит тикет любого пользователя и знает его автора
     */
    @Test
    void testGetTicketOfAnyUser() {
        storage.addUserTicket(2, "чужой");
        Ticket ticket = storage.getTicket(3);
        Assertions.assertEquals("чужой", ticket.getDescription());
        Assertions.assertEquals(2, ticket.getOwnerId());
        Assertions.assertNull(storage.getTicket(99));
    }

    /**
     * Тест, проверяющий, что getOpenTickets возвращает только открытые тикеты всех пользователей
     */
    @Test
    void testGetOpenTickets() {
        storage.addUserTicket(2, "чужой");
        storage.getTicket(1).changeStatus(TicketStatus.IN_PROGRESS);
        List<Ticket> open = storage.getOpenTickets();
        Assertions.assertEquals(2, open.size());
        Assertions.assertEquals(2, open.get(0).getId());
        Assertions.assertEquals(3, open.get(1).getId());
    }
}
