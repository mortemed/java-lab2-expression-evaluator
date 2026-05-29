package ru.vsu.polichnoy.lab2;

/**
 * Хранилище значений переменных.
 * <p>
 * Реализовано на основе массивов. Встроенные коллекции Java не используются.
 * Конечно для красоты сюда просится map, но я подумал, что может условие из первой лабы распространяется и сюда?
 */
public class VariableStorage {

    private static final int DEFAULT_CAPACITY = 4;

    private String[] names;
    private double[] values;
    private int size;

    /**
     * Создаёт пустое хранилище переменных.
     */
    public VariableStorage() {
        names = new String[DEFAULT_CAPACITY];
        values = new double[DEFAULT_CAPACITY];
        size = 0;
    }

    /**
     * Сохраняет значение переменной.
     * Если переменная уже существует, её значение обновляется.
     *
     * @param name имя переменной
     * @param value значение переменной
     */
    public void set(String name, double value) {
        validateName(name);

        int index = indexOf(name);

        if (index != -1) {
            values[index] = value;
            return;
        }

        if (size == names.length) {
            grow();
        }

        names[size] = name;
        values[size] = value;
        size++;
    }

    /**
     * Проверяет, сохранено ли значение переменной.
     *
     * @param name имя переменной
     * @return true, если переменная есть в хранилище, иначе false
     */
    public boolean contains(String name) {
        validateName(name);
        return indexOf(name) != -1;
    }

    /**
     * Возвращает сохранённое значение переменной.
     *
     * @param name имя переменной
     * @return значение переменной
     * @throws ExpressionException если переменная не найдена
     */
    public double get(String name) {
        validateName(name);

        int index = indexOf(name);

        if (index == -1) {
            throw new ExpressionException("Не задано значение переменной: " + name);
        }

        return values[index];
    }

    /**
     * Возвращает значение переменной.
     * Если значение ещё не было задано, оно запрашивается у provider и сохраняется.
     *
     * @param name имя переменной
     * @param provider источник значения переменной
     * @return значение переменной
     */
    public double getOrRequest(String name, VariableProvider provider) {
        validateName(name);

        if (contains(name)) {
            return get(name);
        }

        if (provider == null) {
            throw new ExpressionException("Не задан источник значения переменной: " + name);
        }

        double value = provider.requestValue(name);
        set(name, value);

        return value;
    }

    private int indexOf(String name) {
        for (int i = 0; i < size; i++) {
            if (names[i].equals(name)) {
                return i;
            }
        }

        return -1;
    }
    /**
     * из прошлой лабы)
     * увеличивает ёмкость хранилища-контейнера, если нет места для переменных
     */
    private void grow() {
        int newCapacity = names.length * 2;

        String[] newNames = new String[newCapacity];
        double[] newValues = new double[newCapacity];

        for (int i = 0; i < size; i++) {
            newNames[i] = names[i];
            newValues[i] = values[i];
        }

        names = newNames;
        values = newValues;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя переменной не должно быть пустым");
        }
    }
}