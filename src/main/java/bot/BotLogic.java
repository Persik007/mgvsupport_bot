package bot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import storage.Storage;
import storage.Ticket;

/**
 * Класс, реализующий логику работы бота.
 * Получает сообщение от пользователя и возвращает ответ.
 */
public class BotLogic {

    /**
     * Сообщение помощи.
     */
    private static final String HELP_MESSAGE =
            """
            Команды:\n"
            "/new — создать обращение\n"
            "/list — мои обращения\n"
            "/ticket N — показать обращение с номером N\n"
            "/edit N — изменить описание обращения с номером N\n"
            "/delete N — удалить обращение с номером N\n"
            "N — номер обращения (натуральное число)
            """;

    /**
     * Хранилище.
     */
    private final Storage storage;

    /**
     * Словарь сессий с пользователями.
     */
    private final Map<Long, UserSession> sessions = new HashMap<>();

    /**
     * Конструктор логики бота.
     * @param storage хранилище
     */
    public BotLogic(Storage storage) {
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
            case "/new" -> startNewTicket(session);
            case "/list" -> listTickets(chatId);
            case "/ticket" -> showTicket(chatId, command, argument);
            case "/edit" -> startEdit(chatId, session, command, argument);
            case "/delete" -> deleteTicket(chatId, command, argument);
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
            case WAITING_DESCRIPTION -> createTicket(chatId, session, text);
            case WAITING_NEW_DESCRIPTION -> updateTicket(chatId, session, text);
            case WAITING_COMMAND -> "Чтобы создать обращение, напишите /new. Список команд: /help";
        };
    }

    /**
     * Метод создания тикета.
     * @param session сессия с пользователем
     * @return ответ бота
     */
    private String startNewTicket(UserSession session) {
        session.setState(DialogState.WAITING_DESCRIPTION);
        return "Опишите проблему";
    }

    /**
     * Метод, сохраняющий новый тикет.
     * @param chatId айди чата
     * @param session сессия с пользователем
     * @param description описание тикета
     * @return ответ бота
     */
    private String createTicket(long chatId, UserSession session, String description) {
        long id = storage.addUserTicket(chatId, description);
        session.reset();
        return "Обращение №" + id + " создано";
    }

    /**
     * Показать список всех тикетов.
     * @param chatId айди чата
     * @return ответ бота
     */
    private String listTickets(long chatId) {
        List<Ticket> tickets = storage.getAllUserTickets(chatId);
        if (tickets.isEmpty()) {
            return "У вас пока нет обращений";
        }
        StringBuilder result = new StringBuilder("Ваши обращения:");
        for (Ticket ticket : tickets) {
            result.append("\n").append(format(ticket));
        }
        return result.toString();
    }

    /**
     * Показать тикет по его айди.
     * @param chatId айди чата
     * @param command команда
     * @param argument номер тикета
     * @return ответ бота
     */
    private String showTicket(long chatId, String command, String argument) {
        Integer id = parseId(argument);
        String error = ticketCheck(id, command, chatId);
        if (error != null)
            return error;
        Ticket ticket = storage.getUserTicket(chatId, id);
        return format(ticket);
    }

    /**
     * Изменить тикет по его айдишнику.
     * @param chatId айди чата
     * @param session сессия с пользователем
     * @param command команда
     * @param argument номер тикета
     * @return ответ бота
     */
    private String startEdit(long chatId, UserSession session, String command, String argument) {
        Integer id = parseId(argument);
        String error = ticketCheck(id, command, chatId);
        if (error != null)
            return error;
        session.setEditingTicketId(id);
        session.setState(DialogState.WAITING_NEW_DESCRIPTION);
        return "Введите новое описание обращения №" + id;
    }

    /**
     * Метод, сохраняющий новое описание тикета.
     * @param chatId айди чата
     * @param session сессия с пользователем
     * @param description новое описание тикета
     * @return ответ бота
     */
    private String updateTicket(long chatId, UserSession session, String description) {
        int id = session.getEditingTicketId();
        session.reset();
        Ticket ticket = storage.getUserTicket(chatId, id);
        if (ticket == null) {
            return notFound(id);
        }
        ticket.changeDescription(description);
        return "Обращение №" + id + " обновлено";
    }

    /**
     * Удалить тикет по его айдишнику.
     * @param chatId айди чата
     * @param command команда
     * @param argument номер тикета
     * @return ответ бота
     */
    private String deleteTicket(long chatId, String command, String argument) {
        Integer id = parseId(argument);
        String error = ticketCheck(id, command, chatId);
        if (error != null)
            return error;
        storage.deleteUserTicket(chatId, id);
        return "Обращение №" + id + " удалено";
    }

    /**
     * Метод, превращающий тикет в строку.
     * @param ticket тикет
     * @return строка вида «№1 Описание»
     */
    private String format(Ticket ticket) {
        return "№" + ticket.getId() + " " + ticket.getDescription();
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
     * Метод, превращающий текст в номер тикета.
     * @param argument текст после команды
     * @return номер тикета или null, если это не натуральное число
     */
    private Integer parseId(String argument) {
        try {
            int id = Integer.parseInt(argument);
            if (id <= 0) {
                return null;
            }
            return id;
        } catch (NumberFormatException e) {
            return null;
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
    private String ticketCheck(Integer id, String command, Long chatId){
        if (id == null) {
            return usageHint(command);
        }
        if (storage.getUserTicket(chatId, id) == null) {
            return notFound(id);
        }
        return null;
    }
}