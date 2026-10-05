package com.SPE.miniproject.dto;

public class CalculatorResponse {

    private String operation;
    private Number a;
    private Number b;
    private Number result;

    public CalculatorResponse(String operation, Number a, Number b, Number result) {
        this.operation = operation;
        this.a = a;
        this.b = b;
        this.result = result;
    }

    public String getOperation() { return operation; }
    public Number getA()         { return a; }
    public Number getB()         { return b; }
    public Number getResult()    { return result; }
}
