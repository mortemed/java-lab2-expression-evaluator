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
}