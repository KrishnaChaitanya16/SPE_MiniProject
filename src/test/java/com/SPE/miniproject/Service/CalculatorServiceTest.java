package com.SPE.miniproject.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.SPE.miniproject.exception.DivisionByZeroException;

class CalculatorServiceTest {

    private CalculatorService service;

    @BeforeEach
    void setUp() {
        service = new CalculatorService();
    }

    @Test
    void addReturnsSum() {
        assertEquals(5, service.add(2, 3));
    }

    @Test
    void addHandlesNegativeNumbers() {
        assertEquals(-1, service.add(2, -3));
    }

    @Test
    void subtractReturnsDifference() {
        assertEquals(6, service.sub(10, 4));
    }

    @Test
    void subtractCanReturnNegative() {
        assertEquals(-4, service.sub(1, 5));
    }

    @Test
    void multiplyReturnsProduct() {
        assertEquals(21, service.mul(3, 7));
    }

    @Test
    void multiplyByZeroReturnsZero() {
        assertEquals(0, service.mul(9, 0));
    }

    @Test
    void divideReturnsQuotient() {
        assertEquals(5.0, service.div(10, 2));
    }

    @Test
    void divideReturnsFractionalResult() {
        assertEquals(2.5, service.div(5, 2));
    }

    @Test
    void divideByZeroThrowsException() {
        DivisionByZeroException ex = assertThrows(DivisionByZeroException.class, () -> service.div(10, 0));
        assertEquals("Division By zero is not allowed", ex.getMessage());
    }
}
