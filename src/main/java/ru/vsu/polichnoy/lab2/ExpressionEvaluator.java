package ru.vsu.polichnoy.lab2;

/**
 * Класс для вычисления значения математического выражения.
 */
public class ExpressionEvaluator {

    private final VariableStorage variableStorage;
    private final VariableProvider variableProvider;

    /**
     * Создаёт вычислитель без интерактивного запроса переменных.
     */
    public ExpressionEvaluator() {
        this(
                new VariableStorage(),
                name -> {
                    throw new ExpressionException("Не задано значение переменной: " + name);
                }
        );
    }

    /**
     * Создаёт вычислитель с заранее подготовленным хранилищем переменных.
     *
     * @param variableStorage хранилище значений переменных
     */
    public ExpressionEvaluator(VariableStorage variableStorage) {
        this(
                variableStorage,
                name -> {
                    throw new ExpressionException("Не задано значение переменной: " + name);
                }
        );
    }

    /**
     * Создаёт вычислитель с хранилищем переменных и источником значений.
     *
     * @param variableStorage хранилище значений переменных
     * @param variableProvider источник значений переменных
     */
    public ExpressionEvaluator(VariableStorage variableStorage, VariableProvider variableProvider) {
        if (variableStorage == null) {
            throw new IllegalArgumentException("Хранилище переменных не должно быть null");
        }

        this.variableStorage = variableStorage;
        this.variableProvider = variableProvider;
    }

    /**
     * Вычисляет значение выражения.
     *
     * @param expression строка с математическим выражением
     * @return вычисленное значение
     * @throws ExpressionException если выражение некорректно
     */
    public double evaluate(String expression) {
        ExpressionParser parser = new ExpressionParser(expression, variableStorage, variableProvider);
        return parser.parse();
    }
}