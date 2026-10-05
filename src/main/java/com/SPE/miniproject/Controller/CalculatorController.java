package com.SPE.miniproject.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.SPE.miniproject.Service.CalculatorService;
import com.SPE.miniproject.dto.CalculatorResponse;

@RestController
@RequestMapping("/api/v1")
public class CalculatorController {

    private final CalculatorService service;

    public CalculatorController(CalculatorService service) {
        this.service = service;
    }

    @GetMapping("/add")
    public ResponseEntity<CalculatorResponse> add(
            @RequestParam int a,
            @RequestParam int b) {
        int result = service.add(a, b);
        return ResponseEntity.ok(new CalculatorResponse("add", a, b, result));
    }

    @GetMapping("/subtract")
    public ResponseEntity<CalculatorResponse> subtract(
            @RequestParam int a,
            @RequestParam int b) {
        int result = service.sub(a, b);
        return ResponseEntity.ok(new CalculatorResponse("subtract", a, b, result));
    }

    @GetMapping("/multiply")
    public ResponseEntity<CalculatorResponse> multiply(
            @RequestParam int a,
            @RequestParam int b) {
        int result = service.mul(a, b);
        return ResponseEntity.ok(new CalculatorResponse("multiply", a, b, result));
    }

    @GetMapping("/divide")
    public ResponseEntity<CalculatorResponse> divide(
            @RequestParam int a,
            @RequestParam int b) {
        double result = service.div(a, b);
        return ResponseEntity.ok(new CalculatorResponse("divide", a, b, result));
    }
}
