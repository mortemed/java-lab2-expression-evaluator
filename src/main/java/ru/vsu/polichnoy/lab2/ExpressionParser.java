package ru.vsu.polichnoy.lab2;

/**
 * Парсер математического выражения.
 * <p>
 * Использует рекурсивный спуск.
 */
public class ExpressionParser {

    private static final double ZERO_EPS = 1e-12;

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

        return parseNumber();
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
            throw new ExpressionException("Ожидалось число или открывающая скобка");
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