package bot;

import java.util.*;

import storage.Ticket;
import storage.TicketStorage;

/**
 * Класс, реализующий логику работы бота.
 * Получает сообщение от пользователя и возвращает ответ.
 */
public class BotLogic {

    /**
     * Сообщение помощи.
     */
    private static final String HELP_MESSAGE = """
            Команды:
            /new — создать обращение
            /list — мои обращения
            /ticket N — показать обращение с номером N
            /edit N — изменить описание обращения с номером N
            /delete N — удалить обращение с номером N
            N — номер обращения (натуральное число)
            """;

    /**
     * Хранилище тикетов, где словарь (chatId -> dict(ticketId, Ticket))
     */
    private final TicketStorage storage;

    /**
     * Словарь сессий с пользователями.
     */
    private final Map<Long, UserSession> sessions = new HashMap<>();

    /**
     * Конструктор логики бота.
     * @param storage хранилище
     */
    public BotLogic(TicketStorage storage) {

        this.storage = storage;
    }

    /**
     * Метод, возвращающий ответ.
     * @param chatId айди чата
     * @param message сообщение от пользователя
     * @return ответ бота
     */
    public String getAnswer(long chatId, String message) {
        String text = message.trim();
        UserSession session = getSession(chatId);
        if (text.startsWith("/")) {
            return handleCommand(chatId, session, text);
        }
        return handleText(chatId, session, text);
    }

    /**
     * Метод, возвращающий сессию с пользователем (создаёт её, если сессии ещё нет).
     * @param chatId айди чата
     * @return сессия с пользователем
     */
    private UserSession getSession(long chatId) {
        UserSession session = sessions.get(chatId);
        if (session == null) {
            session = new UserSession();
            sessions.put(chatId, session);
        }
        return session;
    }

    /**
     * Метод, отправляющий команду на обработку.
     * @param chatId айди чата
     * @param session сессия с пользователем
     * @param text сообщение от пользователя
     * @return ответ бота
     */
    private String handleCommand(long chatId, UserSession session, String text) {
        String[] parts = text.split(" ", 2);
        String command = parts[0];
        String argument = parts.length > 1 ? parts[1].trim() : "";
        session.reset();

        return switch (command) {
            case "/start", "/help" -> HELP_MESSAGE;
            case "/new" -> handleStartNewTicket(session);
            case "/list" -> handleShowAllTickets(chatId);
            case "/ticket" -> handleShowTicket(chatId, command, argument);
            case "/edit" -> handleEditCommand(chatId, session, command, argument);
            case "/delete" -> handleDelete(chatId, command, argument);
            default -> "Неизвестная команда. Список команд: /help";
        };
    }

    /**
     * Метод, определяющий, что делать с текстом в зависимости от стадии сессии.
     * @param chatId айди чата
     * @param session сессия с пользователем
     * @param text сообщение от пользователя
     * @return ответ бота
     */
    private String handleText(long chatId, UserSession session, String text) {
        return switch (session.getState()) {
            case WAITING_DESCRIPTION -> handleCreateTicket(chatId, session, text);
            case WAITING_NEW_DESCRIPTION -> handleUpdateTicket(chatId, session, text);
            case WAITING_COMMAND -> "Чтобы создать обращение, напишите /new. Список команд: /help";
        };
    }

    /**
     * Метод создания тикета.
     * @param session сессия с пользователем
     */
    private void startNewTicket(UserSession session) {
        session.setState(DialogState.WAITING_DESCRIPTION);
    }

    /**
     * Метод создания тикета.
     * @param session сессия с пользователем
     */
    private String handleStartNewTicket(UserSession session) {
        startNewTicket(session);
        return "Опишите проблему";
    }

    /**
     * Метод, сохраняющий новый тикет.
     * @param chatId айди чата
     * @param description описание тикета
     * @return возвращает айдишник тикета
     */
    private long createTicket(long chatId, String description) {
        return storage.addUserTicket(chatId, description);
    }

    /**
     * Метод, сохраняющий новый тикет.
     * @param chatId айди чата
     * @param session сессия с пользователем
     * @param description описание тикета
     * @return ответ бота
     */
    private String handleCreateTicket(long chatId, UserSession session, String description) {
        long id = createTicket(chatId, description);
        session.reset();
        return "Обращение №" + id + " создано";
    }

    /**
     * Показать список всех тикетов.
     * @param chatId айди чата
     * @return ответ бота
     */
    private String showAllTickets(long chatId) {
        Collection<Ticket> tickets = storage.getAllUserTickets(chatId);
        StringBuilder result = new StringBuilder("Ваши обращения:");
        for (Ticket ticket : tickets) {
            result.append("\n").append(ticket.toString());
        }
        return result.toString();
    }

    /**
     * Обработчик для метода showAllTickets
     * @param chatId айди чата
     * @return ответ бота
     */
    private String handleShowAllTickets(long chatId) {
        Collection<Ticket> tickets = storage.getAllUserTickets(chatId);
        if (tickets.isEmpty()) {
            return "У вас пока нет обращений";
        }
        return showAllTickets(chatId);
    }

    /**
     * Показать тикет по его айди.
     * @param chatId айди чата
     * @return Ticket
     */
    private Ticket showTicket(long chatId, long id) {
        Optional<Ticket> resultTicket = storage.getUserTicket(chatId, id);
        Ticket ticket = resultTicket.get();
        return ticket;
    }

    /**
     * Обработчик метода showTicket
     * @param chatId айди чата
     * @param command команда
     * @param argument номер тикета
     * @return ответ бота
     */
    private String handleShowTicket(long chatId, String command, String argument) {
        Optional<Integer> result = parseId(argument);
        try{
            validate(result, command, chatId);
        }
        catch (IllegalArgumentException e){
            return e.getMessage();
        }
        Integer id = result.get();
        Ticket ticket = showTicket(chatId, id);
        return ticket.toString();
    }

    /**
     * Меняет состояние сессии на WAITING_NEW_DESCRIPTION
     * @param session сессия с пользователем
     * @param id айди чата
     */
    private void startEdit(UserSession session, int id) {
        session.setEditingTicketId(id);
        session.setState(DialogState.WAITING_NEW_DESCRIPTION);
    }

    /**
     * Изменить тикет по его айдишнику.
     * @param chatId айди чата
     * @param session сессия с пользователем
     * @param command команда
     * @param argument номер тикета
     * @return id сообщения
     */
    private String handleEditCommand(long chatId, UserSession session, String command, String argument) {
        Optional<Integer> result = parseId(argument);
        try{
            validate(result, command, chatId);
        }
        catch (IllegalArgumentException e){
            return e.getMessage();
        }
        startEdit(session, result.get());
        return "Введите новое описание обращения №" + result.get();
    }

    /**
     * Метод, сохраняющий новое описание тикета.
     * @param chatId айди чата
     * @param id айди тикета
     * @param description новое описание тикета
     * @return айди тикета
     */
    private Long updateTicket(long chatId, long id, String description) {
        Optional<Ticket> result = storage.getUserTicket(chatId, id);
        Ticket ticket = result.get();
        ticket.setDescription(description);
        return id;
    }

    /**
     * Обработчик для метода updateTicket
     * @param chatId айди чата
     * @param session сессия с пользователем
     * @param description новое описание тикета
     * @return ответ бота
     */
    private String handleUpdateTicket(long chatId, UserSession session, String description) {
        int id = session.getEditingTicketId();
        session.reset();
        Optional<Ticket> result = storage.getUserTicket(chatId, id);
        if (result.isEmpty()) {
            return notFound(id);
        }
        return "Обращение №" + updateTicket(chatId, id, description) + " обновлено";
    }

    /**
     * Удалить тикет по его айдишнику.
     * @param chatId айди чата
     */
    private void deleteTicket(long chatId, long id) {
        storage.deleteUserTicket(chatId, id);
    }

    /**
     * Обработчик для метода deleteTicket
     * @param chatId айди чата
     * @param command команда
     * @param argument номер тикета
     * @return ответ бота
     */
    private String handleDelete(long chatId, String command, String argument) {
        Optional<Integer> result = parseId(argument);
        try{
            validate(result, command, chatId);
        }
        catch (IllegalArgumentException e){
            return e.getMessage();
        }
        Integer id = result.get();
        deleteTicket(chatId, id);
        return "Обращение №" + id + " удалено";
    }

    /**
     * Метод, превращающий текст в номер тикета.
     * @param argument текст после команды
     * @return номер тикета или null, если это не натуральное число
     */
    private Optional<Integer> parseId(String argument) {
        try {
            int id = Integer.parseInt(argument);
            if (id <= 0) {
                return Optional.empty();
            }
            return Optional.of(id);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Подсказка, как правильно написать команду с номером.
     * @param command команда
     * @return ответ бота
     */
    private String usageHint(String command) {
        return "Укажите номер обращения, например: " + command + " 1";
    }

    /**
     * Сообщение о том, что тикет не найден.
     * @param id айди тикета
     * @return ответ бота
     */
    private String notFound(long id) {
        return "Обращение №" + id + " не найдено";
    }

    /**
     * @param result номер тикета
     * @param command команда, которую выбрали
     * @param chatId - id чата
     * @throws IllegalArgumentException если неправильно указан номер или такого номера не существует
     */
    private void validate(Optional<Integer> result, String command, Long chatId) throws IllegalArgumentException {
        if (result.isEmpty())
            throw new IllegalArgumentException(usageHint(command));
        int id = result.get();
        if (storage.getUserTicket(chatId, id).isEmpty()) {
            throw new IllegalArgumentException(notFound(id));
        }
    }
}