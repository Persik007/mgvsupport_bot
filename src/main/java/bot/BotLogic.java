package bot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import storage.Storage;
import storage.Ticket;
import storage.TicketStatus;

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
     * Сообщение помощи оператору
     */
    private static final String HELP_OPERATORS_MESSAGE =
            """
            Команды:\n"
            "/open — видеть все созданные обращение\n"
            "/take N — взять в работу обращение с номером N\n"
            "/reply N — ответить пользователю с обращением с номером N\n"
            "/close N — закрыть обращение с номером N\n"
            "N — номер обращения (натуральное число)
            """;

    /**
     * Хранилище
     */
    private final Storage storage;

    /**
     * Словарь сессий с пользователями
     */
    private final Map<Long, UserSession> sessions = new ConcurrentHashMap<>();

    /**
     * Список операторов
     */
    private final Operators operatorIds;

    /**
     * Сообщения для других чатов (например, ответ оператора пользователю),
     * которые нужно отправить вместе с ответом бота
     */
    private final List<BotMessage> outgoingMessages = new ArrayList<>();

    /**
     * Конструктор логики бота.
     * @param storage хранилище
     */
    public BotLogic(Storage storage, Operators operatorIds) {

        this.storage = storage;
        this.operatorIds = operatorIds;
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
     * Метод, отдающий сообщения для других чатов и очищающий их список.
     * Вызывается после getAnswer.
     * @return сообщения, которые нужно отправить другим пользователям
     */
    public List<BotMessage> takeOutgoingMessages() {
        List<BotMessage> messages = List.copyOf(outgoingMessages);
        outgoingMessages.clear();
        return messages;
    }

    /**
     * Метод, добавляющий сообщение для другого чата.
     * @param chatId айди чата получателя
     * @param text текст сообщения
     */
    private void sendTo(long chatId, String text) {
        outgoingMessages.add(new BotMessage(chatId, text));
    }
    private void notifyOwner(long operatorChatId, Ticket ticket, String text) {
        if (ticket.getOwnerId() != operatorChatId) {
            sendTo(ticket.getOwnerId(), text);
        }
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
            case "/start", "/help" -> operatorIds.isOperator(chatId) ? HELP_OPERATORS_MESSAGE : HELP_MESSAGE;
            case "/new" -> startNewTicket(session);
            case "/list" -> listTickets(chatId);
            case "/ticket" -> showTicket(chatId, command, argument);
            case "/edit" -> startEdit(chatId, session, command, argument);
            case "/delete" -> deleteTicket(chatId, command, argument);
            case "/open" -> openTickets(chatId);
            case "/take" -> takeTicket(chatId, command, argument);
            case "/reply" -> replyTicket(chatId, session, command, argument);
            case "/close" -> closeTicket(chatId, command, argument);
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
            case WAITING_NEW_REPLY -> sendReply(chatId, session, text);
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
     * Получить открытые обращения всех пользователей.
     * @param chatId айди чата оператора
     * @return список открытых обращений по возрастанию номера
     */
    private String openTickets(long chatId) {
        if (!operatorIds.isOperator(chatId)) return "Команда недоступна";
        List<Ticket> tickets = storage.getOpenTickets();
        if (tickets.isEmpty()) {
            return "Открытых заявок нет";
        }
        StringBuilder sb = new StringBuilder("Сейчас открыты заявки:");
        for (Ticket ticket : tickets) {
            sb.append("\n").append(format(ticket))
                    .append(" (пользователь ").append(ticket.getOwnerId()).append(")");
        }
        return sb.toString();
    }

    /**
     * Взять обращение в работу.
     * @param chatId айди чата оператора
     * @param command команда
     * @param argument номер тикета
     * @return ответ бота
     */
    private String takeTicket(long chatId, String command, String argument) {
        if (!operatorIds.isOperator(chatId)) return "Команда недоступна";
        Integer id = parseId(argument);
        String error = operatorTicketCheck(id, command);
        if (error != null)
            return error;
        Ticket ticket = storage.getTicket(id);
        if (ticket.getStatus() != TicketStatus.OPEN)
            return "Обращение №" + id + " уже в работе или закрыто";
        ticket.changeStatus(TicketStatus.IN_PROGRESS);
        notifyOwner(chatId, ticket, "Ваше обращение №" + id + " взято в работу");
        return "Ваше обращение №" + id + " взято в работу";
    }   

    /**
     * Ответить на обращение: запоминает номер и просит оператора ввести текст.
     * @param chatId айди чата оператора
     * @param session сессия оператора
     * @param command команда
     * @param argument номер тикета
     * @return ответ бота
     */
    private String replyTicket(long chatId, UserSession session, String command, String argument) {
        if (!operatorIds.isOperator(chatId)) return "Команда недоступна";
        Integer id = parseId(argument);
        String error = operatorTicketCheck(id, command);
        if (error != null)
            return error;
        Ticket ticket = storage.getTicket(id);
        if (ticket.getStatus() != TicketStatus.IN_PROGRESS)
            return "Обращение №" + id + " не в работе";
        session.setEditingTicketId(id);
        session.setState(DialogState.WAITING_NEW_REPLY);
        return "Введите ответ на обращение №" + id;
    }

    /**
     * Отправить текст оператора автору обращения.
     * @param chatId айди чата оператора
     * @param session сессия оператора
     * @param text текст ответа
     * @return ответ бота оператору
     */
    private String sendReply(long chatId, UserSession session, String text) {
        int id = session.getEditingTicketId();
        session.reset();
        Ticket ticket = storage.getTicket(id);
        if (ticket == null) {
            return notFound(id);
        }
        if (ticket.getStatus() != TicketStatus.IN_PROGRESS) {
            return "Обращение №" + id + " не в работе";
        }
        notifyOwner(chatId, ticket, "Ответ оператора по обращению №" + id + ": " + text);
        return "Ответ на обращение №" + id + " отправлен";
    }

    /**
     * закрыть обращение
     * @param chatId айди чата оператора
     * @param command команда
     * @param argument номер тикета
     * @return ответ бота
     */
    private String closeTicket(long chatId, String command, String argument) {
        if (!operatorIds.isOperator(chatId)) return "Команда недоступна";
        Integer id = parseId(argument);
        String error = operatorTicketCheck(id, command);
        if (error != null)
            return error;
        Ticket ticket = storage.getTicket(id);
        if (ticket.getStatus() != TicketStatus.IN_PROGRESS)
            return "Обращение №" + id + " еще не было взято в работу";
        ticket.changeStatus(TicketStatus.CLOSED);
        notifyOwner(chatId, ticket, "Ваше обращение №" + id + " закрыто");
        return "Ваше обращение №" + id + " закрыто";
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

    /**
     * Подсказка, как правильно написать команду с номером для оператора
     * @param command команда
     * @return ответ бота
     */
    private String operatorTicketCheck(Integer id, String command) {
        if (id == null) {
            return usageHint(command);
        }
        if (storage.getTicket(id) == null) {
            return notFound(id);
        }
        return null;
    }
}

