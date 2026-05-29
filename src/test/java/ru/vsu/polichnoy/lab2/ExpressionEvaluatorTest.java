package ru.vsu.polichnoy.lab2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionEvaluatorTest {

    private static final double EPS = 1e-9;

    @Test
    void evaluateShouldParseSingleInteger() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("42");

        assertEquals(42.0, result, EPS);
    }

    @Test
    void evaluateShouldParseSingleDecimalNumber() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("3.14");

        assertEquals(3.14, result, EPS);
    }

    @Test
    void evaluateShouldIgnoreSpacesAroundNumber() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("   15.5   ");

        assertEquals(15.5, result, EPS);
    }

    @Test
    void evaluateShouldRejectEmptyExpression() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        assertThrows(ExpressionException.class, () -> evaluator.evaluate(""));
        assertThrows(ExpressionException.class, () -> evaluator.evaluate("   "));
    }

    @Test
    void evaluateShouldRejectInvalidNumber() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        assertThrows(ExpressionException.class, () -> evaluator.evaluate("."));
    }

    @Test
    void evaluateShouldRejectUnexpectedCharactersAfterNumber() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        assertThrows(ExpressionException.class, () -> evaluator.evaluate("42abc"));
    }

    @Test
    void evaluateShouldAddNumbers() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("2 + 3");

        assertEquals(5.0, result, EPS);
    }

    @Test
    void evaluateShouldSubtractNumbers() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("10 - 4");

        assertEquals(6.0, result, EPS);
    }

    @Test
    void evaluateShouldProcessSeveralAdditiveOperationsFromLeftToRight() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("10 - 4 + 2");

        assertEquals(8.0, result, EPS);
    }

    @Test
    void evaluateShouldRejectMissingRightOperandForAddition() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        assertThrows(ExpressionException.class, () -> evaluator.evaluate("2 +"));
    }

    @Test
    void evaluateShouldMultiplyNumbers() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("6 * 7");

        assertEquals(42.0, result, EPS);
    }

    @Test
    void evaluateShouldDivideNumbers() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("10 / 2");

        assertEquals(5.0, result, EPS);
    }

    @Test
    void evaluateShouldRespectMultiplicationPriority() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("2 + 3 * 4");

        assertEquals(14.0, result, EPS);
    }

    @Test
    void evaluateShouldRespectDivisionPriority() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("10 / 2 + 3");

        assertEquals(8.0, result, EPS);
    }

    @Test
    void evaluateShouldProcessMultiplicationAndDivisionFromLeftToRight() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("24 / 3 * 2");

        assertEquals(16.0, result, EPS);
    }

    @Test
    void evaluateShouldRejectDivisionByZero() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        assertThrows(ExpressionException.class, () -> evaluator.evaluate("10 / 0"));
    }

    @Test
    void evaluateShouldProcessParentheses() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("(2 + 3) * 4");

        assertEquals(20.0, result, EPS);
    }

    @Test
    void evaluateShouldProcessNestedParentheses() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("2 * (3 + (4 - 1))");

        assertEquals(12.0, result, EPS);
    }

    @Test
    void evaluateShouldProcessUnaryMinus() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("-5 + 2");

        assertEquals(-3.0, result, EPS);
    }

    @Test
    void evaluateShouldProcessUnaryPlus() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("+5 + 2");

        assertEquals(7.0, result, EPS);
    }

    @Test
    void evaluateShouldProcessUnaryMinusBeforeParentheses() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("-(2 + 3)");

        assertEquals(-5.0, result, EPS);
    }

    @Test
    void evaluateShouldProcessPower() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("2 ^ 3");

        assertEquals(8.0, result, EPS);
    }

    @Test
    void evaluateShouldProcessRightAssociativePower() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("2 ^ 3 ^ 2");

        assertEquals(512.0, result, EPS);
    }

    @Test
    void evaluateShouldProcessNegativePowerArgument() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("2 ^ -2");

        assertEquals(0.25, result, EPS);
    }

    @Test
    void evaluateShouldRejectMissingClosingParenthesis() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        assertThrows(ExpressionException.class, () -> evaluator.evaluate("(2 + 3"));
    }

    @Test
    void evaluateShouldRejectExtraClosingParenthesis() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        assertThrows(ExpressionException.class, () -> evaluator.evaluate("2 + 3)"));
    }

    @Test
    void evaluateShouldUseVariableValue() {
        VariableStorage storage = new VariableStorage();
        storage.set("x", 5.0);

        ExpressionEvaluator evaluator = new ExpressionEvaluator(storage);

        double result = evaluator.evaluate("x + 2");

        assertEquals(7.0, result, EPS);
    }

    @Test
    void evaluateShouldUseSeveralVariables() {
        VariableStorage storage = new VariableStorage();
        storage.set("a", 3.0);
        storage.set("b", 4.0);

        ExpressionEvaluator evaluator = new ExpressionEvaluator(storage);

        double result = evaluator.evaluate("a + b * 2");

        assertEquals(11.0, result, EPS);
    }

    @Test
    void evaluateShouldUseVariableSeveralTimes() {
        VariableStorage storage = new VariableStorage();
        storage.set("x", 5.0);

        ExpressionEvaluator evaluator = new ExpressionEvaluator(storage);

        double result = evaluator.evaluate("x * x + x");

        assertEquals(30.0, result, EPS);
    }

    @Test
    void evaluateShouldRequestUnknownVariableOnlyOnce() {
        VariableStorage storage = new VariableStorage();

        final int[] requestCount = {0};

        VariableProvider provider = name -> {
            requestCount[0]++;
            return 5.0;
        };

        ExpressionEvaluator evaluator = new ExpressionEvaluator(storage, provider);

        double result = evaluator.evaluate("x + x + x");

        assertEquals(15.0, result, EPS);
        assertEquals(1, requestCount[0]);
    }

    @Test
    void evaluateShouldRejectUnknownVariableWithoutProvider() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        assertThrows(ExpressionException.class, () -> evaluator.evaluate("x + 2"));
    }

    @Test
    void evaluateShouldProcessSqrtFunction() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("sqrt(25)");

        assertEquals(5.0, result, EPS);
    }

    @Test
    void evaluateShouldProcessAbsFunction() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("abs(-10)");

        assertEquals(10.0, result, EPS);
    }

    @Test
    void evaluateShouldProcessSinFunction() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("sin(pi / 2)");

        assertEquals(1.0, result, EPS);
    }

    @Test
    void evaluateShouldProcessCosFunction() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("cos(0)");

        assertEquals(1.0, result, EPS);
    }

    @Test
    void evaluateShouldProcessNaturalLogFunction() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("ln(e)");

        assertEquals(1.0, result, EPS);
    }

    @Test
    void evaluateShouldProcessDecimalLogFunction() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("log(100)");

        assertEquals(2.0, result, EPS);
    }

    @Test
    void evaluateShouldProcessConstants() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("pi + e");

        assertEquals(Math.PI + Math.E, result, EPS);
    }

    @Test
    void evaluateShouldProcessFunctionInsideExpression() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        double result = evaluator.evaluate("2 + sqrt(16) * 3");

        assertEquals(14.0, result, EPS);
    }

    @Test
    void evaluateShouldRejectUnknownFunction() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        assertThrows(ExpressionException.class, () -> evaluator.evaluate("unknown(10)"));
    }

    @Test
    void evaluateShouldRejectSqrtOfNegativeNumber() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        assertThrows(ExpressionException.class, () -> evaluator.evaluate("sqrt(-1)"));
    }

    @Test
    void evaluateShouldRejectLogOfNonPositiveNumber() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        assertThrows(ExpressionException.class, () -> evaluator.evaluate("log(0)"));
        assertThrows(ExpressionException.class, () -> evaluator.evaluate("ln(-5)"));
    }
}