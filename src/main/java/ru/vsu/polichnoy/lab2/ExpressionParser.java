package ru.vsu.polichnoy.lab2;

/**
 * Парсер математического выражения.
 * <p>
 * Использует рекурсивный спуск.
 */
public class ExpressionParser {

    private static final double ZERO_EPS = 1e-12;

    private final String expression;
    private final VariableStorage variableStorage;
    private final VariableProvider variableProvider;

    private int position;

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

    private double parseUnary() {
        if (match('+')) {
            return parseUnary();
        }

        if (match('-')) {
            return -parseUnary();
        }

        return parsePower();
    }

    private double parsePower() {
        double base = parsePrimary();

        if (match('^')) {
            double exponent = parseUnary();
            return Math.pow(base, exponent);
        }

        return base;
    }

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

    private boolean match(char expected) {
        skipWhitespace();

        if (!isAtEnd() && currentChar() == expected) {
            position++;
            return true;
        }

        return false;
    }

    private void skipWhitespace() {
        while (!isAtEnd() && Character.isWhitespace(currentChar())) {
            position++;
        }
    }

    private boolean isAtEnd() {
        return position >= expression.length();
    }

    private char currentChar() {
        return expression.charAt(position);
    }

    private boolean isIdentifierStart(char ch) {
        return Character.isLetter(ch) || ch == '_';
    }

    private boolean isIdentifierPart(char ch) {
        return Character.isLetterOrDigit(ch) || ch == '_';
    }
}