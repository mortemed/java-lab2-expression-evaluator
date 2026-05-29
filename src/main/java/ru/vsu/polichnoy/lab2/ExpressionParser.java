package ru.vsu.polichnoy.lab2;

/**
 * Парсер математического выражения.
 * <p>
 * Класс выполняет разбор строки с математическим выражением и сразу вычисляет
 * его значение. Для разбора используется метод рекурсивного спуска.
 */
public class ExpressionParser {

    private static final double ZERO_EPS = 1e-12;

    private final String expression;
    private final VariableStorage variableStorage;
    private final VariableProvider variableProvider;

    private int position;

    /**
     * Создаёт парсер для переданного выражения.
     *
     * @param expression строка с математическим выражением
     * @param variableStorage хранилище значений переменных
     * @param variableProvider источник значений переменных, если переменная ещё не была задана
     * @throws ExpressionException если выражение равно {@code null}
     * @throws IllegalArgumentException если хранилище переменных равно {@code null}
     */
    public ExpressionParser(String expression, VariableStorage variableStorage, VariableProvider variableProvider) {
        if (expression == null) {
            throw new ExpressionException("Выражение не должно быть null");
        }

        if (variableStorage == null) {
            throw new IllegalArgumentException("Хранилище переменных не должно быть null");
        }

        this.expression = expression;
        this.variableStorage = variableStorage;
        this.variableProvider = variableProvider;
        this.position = 0;
    }

    /**
     * Запускает разбор выражения и возвращает вычисленный результат.
     * <p>
     * После вычисления метод проверяет, что вся строка была разобрана полностью.
     * Если в конце выражения остались лишние символы, выражение считается некорректным.
     *
     * @return вычисленное значение выражения
     * @throws ExpressionException если выражение пустое или записано некорректно
     */
    public double parse() {
        skipWhitespace();

        if (isAtEnd()) {
            throw new ExpressionException("Выражение пустое");
        }

        double result = parseExpression();

        skipWhitespace();

        if (!isAtEnd()) {
            throw new ExpressionException("Неожиданный символ: '" + currentChar() + "'");
        }

        return result;
    }

    /**
     * Разбирает уровень сложения и вычитания.
     * <p>
     * Этот уровень имеет самый низкий приоритет среди поддерживаемых операций.
     *
     * <pre>
     * expression = term { ("+" | "-") term }
     * </pre>
     *
     * @return значение выражения после обработки операций сложения и вычитания
     */
    private double parseExpression() {
        double result = parseTerm();

        while (true) {
            if (match('+')) {
                result += parseTerm();
            } else if (match('-')) {
                result -= parseTerm();
            } else {
                break;
            }
        }

        return result;
    }

    /**
     * Разбирает уровень умножения и деления.
     * <p>
     * Операции умножения и деления имеют более высокий приоритет,
     * чем сложение и вычитание.
     *
     * <pre>
     * term = unary { ("*" | "/") unary }
     * </pre>
     *
     * @return значение выражения после обработки операций умножения и деления
     * @throws ExpressionException если происходит деление на ноль
     */
    private double parseTerm() {
        double result = parseUnary();

        while (true) {
            if (match('*')) {
                result *= parseUnary();
            } else if (match('/')) {
                double divisor = parseUnary();

                if (Math.abs(divisor) < ZERO_EPS) {
                    throw new ExpressionException("Деление на ноль");
                }

                result /= divisor;
            } else {
                break;
            }
        }

        return result;
    }

    /**
     * Разбирает одиночный унарный плюс или одиночный унарный минус.
     * <p>
     * Унарный знак может стоять перед числом, переменной, функцией или выражением
     * в скобках. Последовательности из нескольких унарных знаков подряд,
     * например {@code --5}, {@code -+5} или {@code ++5}, считаются некорректными.
     *
     * <pre>
     * unary = ["+" | "-"] power
     * </pre>
     *
     * @return значение выражения после обработки унарного знака
     */
    private double parseUnary() {
        if (match('+')) {
            return parsePower();
        }

        if (match('-')) {
            return -parsePower();
        }

        return parsePower();
    }

    /**
     * Разбирает операцию возведения в степень.
     * <p>
     * Степень обрабатывается как правоассоциативная операция.
     * Поэтому выражение {@code 2 ^ 3 ^ 2} вычисляется как {@code 2 ^ (3 ^ 2)}.
     *
     * <pre>
     * power = primary [ "^" unary ]
     * </pre>
     *
     * @return значение выражения после обработки возведения в степень
     */
    private double parsePower() {
        double base = parsePrimary();

        if (match('^')) {
            double exponent = parseUnary();
            return Math.pow(base, exponent);
        }

        return base;
    }

    /**
     * Разбирает первичные элементы выражения.
     * <p>
     * К первичным элементам относятся:
     * <ul>
     *     <li>числа;</li>
     *     <li>переменные;</li>
     *     <li>константы;</li>
     *     <li>функции;</li>
     *     <li>выражения в скобках.</li>
     * </ul>
     *
     * <pre>
     * primary = number
     *         | variable
     *         | constant
     *         | function "(" expression ")"
     *         | "(" expression ")"
     * </pre>
     *
     * @return значение первичного элемента выражения
     * @throws ExpressionException если скобки расставлены некорректно
     */
    private double parsePrimary() {
        skipWhitespace();

        if (match('(')) {
            double result = parseExpression();

            if (!match(')')) {
                throw new ExpressionException("Ожидалась закрывающая скобка");
            }

            return result;
        }

        if (!isAtEnd() && isIdentifierStart(currentChar())) {
            return parseIdentifierOrFunction();
        }

        return parseNumber();
    }

    /**
     * Разбирает имя переменной, константы или функции.
     * <p>
     * Если имя равно {@code pi} или {@code e}, возвращается значение соответствующей
     * математической константы. Если после имени идёт открывающая скобка, имя
     * считается названием функции. В остальных случаях имя считается переменной.
     *
     * @return значение переменной, константы или результата функции
     * @throws ExpressionException если функция неизвестна или выражение функции некорректно
     */
    private double parseIdentifierOrFunction() {
        String name = parseIdentifier();

        if (name.equals("pi")) {
            return Math.PI;
        }

        if (name.equals("e")) {
            return Math.E;
        }

        skipWhitespace();

        if (match('(')) {
            double argument = parseExpression();

            if (!match(')')) {
                throw new ExpressionException("Ожидалась закрывающая скобка после аргумента функции");
            }

            return applyFunction(name, argument);
        }

        return variableStorage.getOrRequest(name, variableProvider);
    }

    /**
     * Вычисляет значение поддерживаемой математической функции.
     *
     * @param name имя функции
     * @param argument значение аргумента функции
     * @return результат применения функции
     * @throws ExpressionException если функция неизвестна или аргумент находится вне области определения
     */
    private double applyFunction(String name, double argument) {
        switch (name) {
            case "sin":
                return Math.sin(argument);
            case "cos":
                return Math.cos(argument);
            case "tan":
                return Math.tan(argument);
            case "sqrt":
                if (argument < 0) {
                    throw new ExpressionException("Корень из отрицательного числа");
                }
                return Math.sqrt(argument);
            case "abs":
                return Math.abs(argument);
            case "ln":
                if (argument <= 0) {
                    throw new ExpressionException("Логарифм от неположительного числа");
                }
                return Math.log(argument);
            case "log":
                if (argument <= 0) {
                    throw new ExpressionException("Логарифм от неположительного числа");
                }
                return Math.log10(argument);
            default:
                throw new ExpressionException("Неизвестная функция: " + name);
        }
    }

    /**
     * Считывает имя переменной, константы или функции.
     * <p>
     * Имя должно начинаться с буквы или символа подчёркивания.
     * Остальные символы могут быть буквами, цифрами или символом подчёркивания.
     *
     * @return считанное имя
     * @throws ExpressionException если в текущей позиции нет корректного имени
     */
    private String parseIdentifier() {
        skipWhitespace();

        if (isAtEnd() || !isIdentifierStart(currentChar())) {
            throw new ExpressionException("Ожидалось имя переменной или функции");
        }

        int start = position;
        position++;

        while (!isAtEnd() && isIdentifierPart(currentChar())) {
            position++;
        }

        return expression.substring(start, position);
    }

    /**
     * Считывает число из выражения.
     * <p>
     * Поддерживаются целые и дробные числа с точкой в качестве разделителя.
     *
     * @return считанное числовое значение
     * @throws ExpressionException если в текущей позиции нет корректного числа
     */
    private double parseNumber() {
        skipWhitespace();

        int start = position;
        boolean hasDigit = false;
        boolean hasDot = false;

        while (!isAtEnd()) {
            char ch = currentChar();

            if (Character.isDigit(ch)) {
                hasDigit = true;
                position++;
            } else if (ch == '.' && !hasDot) {
                hasDot = true;
                position++;
            } else {
                break;
            }
        }

        if (!hasDigit) {
            throw new ExpressionException("Ожидалось число, переменная, функция или открывающая скобка");
        }

        String numberText = expression.substring(start, position);

        try {
            return Double.parseDouble(numberText);
        } catch (NumberFormatException exception) {
            throw new ExpressionException("Некорректное число: " + numberText);
        }
    }

    /**
     * Проверяет, находится ли в текущей позиции ожидаемый символ.
     * <p>
     * Если символ найден, позиция парсера сдвигается на один символ вперёд.
     *
     * @param expected ожидаемый символ
     * @return true, если символ найден, иначе false
     */
    private boolean match(char expected) {
        skipWhitespace();

        if (!isAtEnd() && currentChar() == expected) {
            position++;
            return true;
        }

        return false;
    }

    /**
     * Пропускает пробельные символы в текущей позиции.
     */
    private void skipWhitespace() {
        while (!isAtEnd() && Character.isWhitespace(currentChar())) {
            position++;
        }
    }

    /**
     * Проверяет, достигнут ли конец выражения.
     *
     * @return true, если текущая позиция находится за последним символом выражения
     */
    private boolean isAtEnd() {
        return position >= expression.length();
    }

    /**
     * Возвращает текущий символ выражения.
     *
     * @return символ в текущей позиции
     */
    private char currentChar() {
        return expression.charAt(position);
    }

    /**
     * Проверяет, может ли символ быть первым символом имени.
     *
     * @param ch проверяемый символ
     * @return true, если символ может начинать имя переменной или функции
     */
    private boolean isIdentifierStart(char ch) {
        return Character.isLetter(ch) || ch == '_';
    }

    /**
     * Проверяет, может ли символ быть частью имени.
     *
     * @param ch проверяемый символ
     * @return true, если символ может входить в имя переменной или функции
     */
    private boolean isIdentifierPart(char ch) {
        return Character.isLetterOrDigit(ch) || ch == '_';
    }
}