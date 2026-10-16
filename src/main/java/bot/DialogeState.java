package bot;
public enum DialogeState {
    /**
     * Бот ничего не ждет, пользователь может вводить команды
     */
    WATING_COMMAND,
    /**
     * Бот ждет описание проблемы для нового обращения
     */
    WAITING_FOR_MESSAGE,
    /**
     * Бот ждет новое описание для редактируемого сообщения
     */
    WAITING_FOR_HELP_COMMAND
}