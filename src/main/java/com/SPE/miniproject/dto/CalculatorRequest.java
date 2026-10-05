package com.SPE.miniproject.dto;

import jakarta.validation.constraints.NotNull;

public class CalculatorRequest {

    @NotNull(message = "Value 'a' must not be null")
    private Integer a;

    @NotNull(message = "Value 'b' must not be null")
    private Integer b;

    
    public Integer getA() { return a; }
    public void setA(Integer a) { this.a = a; }

    public Integer getB() { return b; }
    public void setB(Integer b) { this.b = b; }
}
