package ru.vsu.polichnoy.lab2;

/**
 * Парсер математического выражения.
 * <p>
 * Использует рекурсивный спуск.
 */
public class ExpressionParser {

    private final String expression;
    private int position;

    public ExpressionParser(String expression) {
        if (expression == null) {
            throw new ExpressionException("Выражение не должно быть null");
        }

        this.expression = expression;
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
        double result = parseNumber();

        while (true) {
            if (match('*')) {
                result *= parseNumber();
            } else if (match('/')) {
                double divisor = parseNumber();

                if (Math.abs(divisor) < 1e-12) {
                    throw new ExpressionException("Деление на ноль");
                }

                result /= divisor;
            } else {
                break;
            }
        }

        return result;
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
            throw new ExpressionException("Ожидалось число");
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
}