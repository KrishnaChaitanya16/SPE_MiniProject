package com.SPE.miniproject.Service;

import org.springframework.stereotype.Service;

import com.SPE.miniproject.exception.DivisionByZeroException;

@Service 
public class CalculatorService {
    public int add(int a,int b){
        return a+b;
    }
    public int sub(int a, int b){
        return a-b;
    }
    public int mul(int a,int b){
        return a*b;
    }
    public double div(int a, int b){
        if(b==0){
            throw new DivisionByZeroException("Division By zero is not allowed");
        }
        return (double)a/b;
    }
    
}
