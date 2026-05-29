package ru.vsu.polichnoy.lab2;

/**
 * Класс для вычисления значения математического выражения.
 */
public class ExpressionEvaluator {

    /**
     * Вычисляет значение выражения.
     *
     * @param expression строка с математическим выражением
     * @return вычисленное значение
     * @throws ExpressionException если выражение некорректно
     */
    public double evaluate(String expression) {
        ExpressionParser parser = new ExpressionParser(expression);
        return parser.parse();
    }
}