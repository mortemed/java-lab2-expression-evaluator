package ru.vsu.polichnoy.lab2;

/**
 * Исключение, возникающее при ошибке разбора или вычисления выражения.
 */
public class ExpressionException extends RuntimeException {

    public ExpressionException(String message) {
        super(message);
    }
}