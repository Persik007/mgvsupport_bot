package bot;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import storage.Storage;

/**
 * Тесты для класса BotLogic.
 */
class BotLogicTest {

    /**
     * id 1 пользователя.
     */
    private static final long USER = 1;

    /**
     * id 2 пользователя.
     */
    private static final long OTHER_USER = 2;

    /**
     * id оператора
     */
    private static final long OPERATOR = 123;

    /**
     * Логика бота.
     */
    private final BotLogic botLogic = new BotLogic(new Storage(), new Operators("123"));

    /**
     * Создаёт обращение: /new и описание.
     *
     * @param userId      id пользователя
     * @param description описание проблемы
     * @return последний ответ бота
     */
    private String createTicket(long userId, String description) {
        botLogic.getAnswer(userId, "/new");
        return botLogic.getAnswer(userId, description);
    }

    /**
     * /help показывает список команд.
     */
    @Test
    void testHelpShowsCommands() {
        Assertions.assertEquals( """
            Команды:\n"
            "/new — создать обращение\n"
            "/list — мои обращения\n"
            "/ticket N — показать обращение с номером N\n"
            "/edit N — изменить описание обращения с номером N\n"
            "/delete N — удалить обращение с номером N\n"
            "N — номер обращения (натуральное число)
            """,
                botLogic.getAnswer(USER, "/help"));
    }

    /**
     * Сценарий создания обращения.
     */
    @Test
    void testTicketIsCreated() {
        Assertions.assertEquals("Обращение №1 создано", createTicket(USER, "Списали деньги"));
    }

    /**
     * /list показывает обращения 
     */
    @Test
    void testListShowsTickets() {
        createTicket(USER, "Списали деньги, подписка не пришла");
        createTicket(USER, "Не работает на телефоне");
        Assertions.assertEquals("Ваши обращения:\n"
                        + "№1 Списали деньги, подписка не пришла\n"
                        + "№2 Не работает на телефоне",
                botLogic.getAnswer(USER, "/list"));
    }

    /**
     * /list не показывает чужие обращения.
     */
    @Test
    void testListHidesForeignTickets() {
        createTicket(OTHER_USER, "Чужое обращение");
        Assertions.assertEquals("У вас пока нет обращений", botLogic.getAnswer(USER, "/list"));
    }

    /**
     * /edit меняет описание обращения.
     */
    @Test
    void testTicketIsEdited() {
        createTicket(USER, "Списали деньги");
        Assertions.assertEquals("Введите новое описание обращения №1",
                botLogic.getAnswer(USER, "/edit 1"));
        Assertions.assertEquals("Обращение №1 обновлено",
                botLogic.getAnswer(USER, "Списали 189₽, подписка не активировалась"));
        Assertions.assertEquals("№1 Списали 189₽, подписка не активировалась",
                botLogic.getAnswer(USER, "/ticket 1"));
    }

    /**
     * После /delete обращение не найдено.
     */
    @Test
    void testDeletedTicketIsNotFound() {
        createTicket(USER, "Первое");
        createTicket(USER, "Второе");
        Assertions.assertEquals("Обращение №2 удалено", botLogic.getAnswer(USER, "/delete 2"));
        Assertions.assertEquals("Обращение №2 не найдено", botLogic.getAnswer(USER, "/ticket 2"));
    }

    /**
     * Несуществующее обращение не найдено.
     */
    @Test
    void testMissingTicketIsNotFound() {
        Assertions.assertEquals("Обращение №7 не найдено", botLogic.getAnswer(USER, "/ticket 7"));
    }

    /**
     * Чужое обращение нельзя посмотреть, изменить или удалить,
     */
    @Test
    void testForeignTicketIsNotFound() {
        createTicket(OTHER_USER, "Чужое обращение");
        String expected = "Обращение №1 не найдено";
        Assertions.assertEquals(expected, botLogic.getAnswer(USER, "/ticket 1"));
        Assertions.assertEquals(expected, botLogic.getAnswer(USER, "/edit 1"));
        Assertions.assertEquals(expected, botLogic.getAnswer(USER, "/delete 1"));
        Assertions.assertEquals("№1 Чужое обращение", botLogic.getAnswer(OTHER_USER, "/ticket 1"));
    }

    /**
     * Команда без номера или с неправильным номером — бот подсказывает, как её писать.
     */
    @Test
    void testWrongNumberShowsHint() {
        Assertions.assertEquals("Укажите номер обращения, например: /edit 1",
                botLogic.getAnswer(USER, "/edit"));
        Assertions.assertEquals("Укажите номер обращения, например: /ticket 1",
                botLogic.getAnswer(USER, "/ticket abc"));
        Assertions.assertEquals("Укажите номер обращения, например: /delete 1",
                botLogic.getAnswer(USER, "/delete 0"));
    }


    /**
     * Команда помощи для оператора
     */
    @Test
    void testHelpOperatorShowsCommands() {
        Assertions.assertEquals( """
            Команды:\n"
            "/open — видеть все созданные обращение\n"
            "/take N — взять в работу обращение с номером N\n"
            "/reply N — ответить пользователю с обращением с номером N\n"
            "/close N — закрыть обращение с номером N\n"
            "N — номер обращения (натуральное число)
            """,
                botLogic.getAnswer(OPERATOR, "/help"));
    }

    /**
     * /open без обращений
     */
    @Test
    void testOpenTicketsEmpty() {
        Assertions.assertEquals("Открытых заявок нет",
                botLogic.getAnswer(OPERATOR, "/open"));
    }

    /**
     * /open показывает открытые обращения всех пользователей
     */
    @Test
    void testOpenTickets() {
        createTicket(USER, "Первая заявка");
        createTicket(OTHER_USER, "Вторая заявка");
        Assertions.assertEquals("Сейчас открыты заявки:\n"
                        + "№1 Первая заявка (пользователь 1)\n"
                        + "№2 Вторая заявка (пользователь 2)",
                botLogic.getAnswer(OPERATOR, "/open"));
    }

    /**
     * /open не показывает обращения, взятые в работу
     */
    @Test
    void testOpenHidesTakenTickets() {
        createTicket(USER, "Первая заявка");
        createTicket(USER, "Вторая заявка");
        botLogic.getAnswer(OPERATOR, "/take 1");
        Assertions.assertEquals("Сейчас открыты заявки:\n"
                        + "№2 Вторая заявка (пользователь 1)",
                botLogic.getAnswer(OPERATOR, "/open"));
    }

    /**
     * Проверяем, что у пользователя нет доступа к командам оператора
     */
    @Test
    void testUserTryOperatorCommand() {
        createTicket(USER, "Списали деньги");
        Assertions.assertEquals("Команда недоступна", botLogic.getAnswer(USER, "/open"));
        Assertions.assertEquals("Команда недоступна", botLogic.getAnswer(USER, "/take 1"));
        Assertions.assertEquals("Команда недоступна", botLogic.getAnswer(USER, "/reply 1"));
        Assertions.assertEquals("Команда недоступна", botLogic.getAnswer(USER, "/close 1"));
    }


    /**
     * /take берёт открытое обращение в работу, повторно взять его нельзя
     */
    @Test
    void testTakeTicket() {
        createTicket(USER, "Первая заявка");
        Assertions.assertEquals("Ваше обращение №1 взято в работу",
                botLogic.getAnswer(OPERATOR, "/take 1"));
        Assertions.assertEquals("Обращение №1 уже в работе или закрыто",
                botLogic.getAnswer(OPERATOR, "/take 1"));
    }

    /**
     * Команды оператора с неправильным или несуществующим номером
     */
    @Test
    void testOperatorWrongNumber() {
        Assertions.assertEquals("Укажите номер обращения, например: /take 1",
                botLogic.getAnswer(OPERATOR, "/take"));
        Assertions.assertEquals("Укажите номер обращения, например: /reply 1",
                botLogic.getAnswer(OPERATOR, "/reply abc"));
        Assertions.assertEquals("Укажите номер обращения, например: /close 1",
                botLogic.getAnswer(OPERATOR, "/close 0"));
        Assertions.assertEquals("Обращение №7 не найдено",
                botLogic.getAnswer(OPERATOR, "/take 7"));
    }

    /**
     * /reply: бот просит текст и отправляет его автору обращения
     */
    @Test
    void testReplyTicket() {
        createTicket(USER, "Списали деньги");
        botLogic.getAnswer(OPERATOR, "/take 1");
        Assertions.assertEquals("Введите ответ на обращение №1",
                botLogic.getAnswer(OPERATOR, "/reply 1"));
        Assertions.assertEquals("Ответ на обращение №1 отправлен",
                botLogic.getAnswer(OPERATOR, "Деньги вернутся в течение 3 дней"));

        List<BotMessage> messages = botLogic.takeOutgoingMessages();
        Assertions.assertEquals(1, messages.size());
        Assertions.assertEquals(USER, messages.getFirst().getChatId());
        Assertions.assertEquals("Ответ оператора по обращению №1: Деньги вернутся в течение 3 дней",
                messages.getFirst().getText());
    }

    /**
     * На обращение, которое не взято в работу, ответить нельзя
     */
    @Test
    void testReplyNotTakenTicket() {
        createTicket(USER, "Списали деньги");
        Assertions.assertEquals("Обращение №1 не в работе",
                botLogic.getAnswer(OPERATOR, "/reply 1"));
        Assertions.assertTrue(botLogic.takeOutgoingMessages().isEmpty());
    }

    /**
     * /close закрывает обращение в работе, после этого ответить на него нельзя
     */
    @Test
    void testCloseTicket() {
        createTicket(USER, "Списали деньги");
        botLogic.getAnswer(OPERATOR, "/take 1");
        Assertions.assertEquals("Ваше обращение №1 закрыто",
                botLogic.getAnswer(OPERATOR, "/close 1"));
        Assertions.assertEquals("Обращение №1 не в работе",
                botLogic.getAnswer(OPERATOR, "/reply 1"));
        Assertions.assertEquals("Обращение №1 еще не было взято в работу",
                botLogic.getAnswer(OPERATOR, "/close 1"));
    }

    /**
     * Обращение, которое не взято в работу, закрыть нельзя
     */
    @Test
    void testCloseNotTakenTicket() {
        createTicket(USER, "Списали деньги");
        Assertions.assertEquals("Обращение №1 еще не было взято в работу",
                botLogic.getAnswer(OPERATOR, "/close 1"));
    }
}