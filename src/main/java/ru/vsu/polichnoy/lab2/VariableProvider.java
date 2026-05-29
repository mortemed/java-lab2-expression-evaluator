package ru.vsu.polichnoy.lab2;

/**
 * Источник значений переменных.
 * <p>
 * В консольной программе значение запрашивается у пользователя.
 * В тестах значение может быть задано заранее.
 */
public interface VariableProvider {

    /**
     * Возвращает значение переменной.
     *
     * @param variableName имя переменной
     * @return значение переменной
     */
    double requestValue(String variableName);
}