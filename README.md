# Calculator REST API — Spring Boot Mini Project

A simple REST calculator service exposing arithmetic endpoints with structured JSON responses, input validation, and a Spring Boot Actuator health probe.

---

## Prerequisites

- Java 21+
- Maven 3.8+

---

## Build

```bash
cd miniproject
mvn clean package -DskipTests
```

## Run

```bash
mvn spring-boot:run
```

The server starts on **http://localhost:8080**.

---

## API Endpoints

All endpoints accept query parameters `a` and `b` (integers).

### Add
```
GET /api/v1/add?a=2&b=3
```
**Response 200:**
```json
{ "operation": "add", "a": 2, "b": 3, "result": 5 }
```

### Subtract
```
GET /api/v1/subtract?a=10&b=4
```
**Response 200:**
```json
{ "operation": "subtract", "a": 10, "b": 4, "result": 6 }
```

### Multiply
```
GET /api/v1/multiply?a=3&b=7
```
**Response 200:**
```json
{ "operation": "multiply", "a": 3, "b": 7, "result": 21 }
```

### Divide
```
GET /api/v1/divide?a=10&b=2
```
**Response 200:**
```json
{ "operation": "divide", "a": 10, "b": 2, "result": 5.0 }
```

**Divide by zero — Response 400:**
```json
{ "status": 400, "error": "Bad Request", "message": "Divison By zero is not allowed" }
```

### Health
```
GET /actuator/health
```
**Response 200:**
```json
{ "status": "UP" }
```

---

## Error Responses

All errors return HTTP **400** with a structured body:

| Scenario              | Message |
|-----------------------|---------|
| Missing parameter     | `Missing required parameter: b` |
| Non-numeric parameter | `Invalid value for parameter 'a': must be a number` |
| Division by zero      | `Divison By zero is not allowed` |

---

## Run Tests

```bash
mvn test
```
