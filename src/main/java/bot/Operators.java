package bot;

import java.util.ArrayList;
import java.util.List;

/**
 * Список операторов 
 */
public class Operators {
    /**
     * Список айди операторов
     */
    private final List<Long> ids = new ArrayList<>();

    /**
     * Конструктор списка операторов
     * Пустые и неправильные айди игнорируются
     * @param operatorChatIds айди операторов, разделенных запятыми
     */ 
    public Operators(String operatorChatIds) {
        for (String part : operatorChatIds.split(",")) {
            if (part.isBlank()) {
                continue;
            }
            try {
                long id = Long.parseLong(part.trim());
                ids.add(id);
            } catch (NumberFormatException e) {
                System.out.println("Неправильный id оператора в .env: " + part.trim());
            }
        }
    }
    /**
     * Метод, проверяющий, является ли пользователь оператором
     * @param chatId айди чата пользователя
     * @return true, если оператор
     */
    public boolean isOperator(long chatId) {
        return ids.contains(chatId);
    }
    
}