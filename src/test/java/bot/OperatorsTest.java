package bot;
 
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
 
/**
 * Тесты для класса Operators.
 */
class OperatorsTest {
 
    /**
     * Айди через запятую или с пробелами распознаются.
     */
    @Test
    void testParsesIds() {
        Operators operators = new Operators("100, 200");
        Assertions.assertTrue(operators.isOperator(100));
        Assertions.assertTrue(operators.isOperator(200));
        Assertions.assertFalse(operators.isOperator(300));
    }
 
    /**
     * Пустая строка и неправильные значения не ломают работу
     */
    @Test
    void testEmptyAndWrongValues() {
        Assertions.assertFalse(new Operators("").isOperator(1));
        Assertions.assertTrue(new Operators("abc,5").isOperator(5));
    }
}