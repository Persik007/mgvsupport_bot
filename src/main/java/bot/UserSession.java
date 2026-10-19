package bot;
/**
 * Состояние диалога с одним пользователем.
 * <p>Бот не может «дождаться» ответа, поэтому запоминает:</p>
 * <ul>
 *     <li>на каком шаге диалога пользователь;</li>
 *     <li>какое обращение он редактирует.</li>
 * </ul>
 */
public class UserSession {
    /**
     * текущее состояние диалога
     */
    private DialogState state = DialogState.WAITING_COMMAND;
    /**
     * id редактируемого тикета
     */
    private int editingTicketId;
    /**
     * Возвращает текущее состояние диалога
     * @return текущее состояние диалога
     */
    public DialogState getState() {
        return state;
    }
    /**
     * Устанавливает текущее состояние диалога
     * @param state новое состояние диалога
     */
    public void setState(DialogState state) {
        this.state = state;
    }
    /**
     * Возвращает id редактируемого тикета
     * @return id редактируемого тикета
     */
    public int getEditingTicketId() {
        return editingTicketId;
    }
    /**
     * Устанавливает id редактируемого тикета
     * @param editingTicketId id редактируемого тикета
     */
    public void setEditingTicketId(int editingTicketId) {
        this.editingTicketId = editingTicketId;
    }
    /**
     * Сбрасывает состояние диалога и id редактируемого тикета, бот снова ждет команду от пользователя
     */
    public void reset() {
        this.state = DialogState.WAITING_COMMAND;
        this.editingTicketId = 0;
    }
}