# Calculator REST API — Spring Boot Mini Project

A simple REST calculator service exposing arithmetic endpoints with structured JSON responses, input validation, and a Spring Boot Actuator health probe.

---

## Prerequisites

- Java 21+
- Maven 3.8+

---

## Build

```bash
./mvnw clean package -DskipTests
```

## Run

```bash
./mvnw spring-boot:run
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
{ "status": 400, "error": "Bad Request", "message": "Division By zero is not allowed" }
```

### Health
```
GET /actuator/health
```
**Response 200:**
```json
{ "groups": ["liveness", "readiness"], "status": "UP" }
```

---

## Error Responses

All errors return HTTP **400** with a structured body:

| Scenario              | Message |
|-----------------------|---------|
| Missing parameter     | `Missing required parameter: b` |
| Non-numeric parameter | `Invalid value for parameter 'a': must be a number` |
| Division by zero      | `Division By zero is not allowed` |

---

## Run Tests

```bash
./mvnw test
```

The suite has two layers:

- **Unit tests (JUnit 5)** in `CalculatorServiceTest` cover add, subtract, multiply, divide and the divide-by-zero case.
- **Integration tests (Spring MVC, `MockMvc`)** in `CalculatorControllerTest` call the real endpoints and check status codes, JSON bodies, validation errors and `/actuator/health`.

JUnit XML reports are written to `target/surefire-reports/`, which is what Jenkins publishes.

---

## Project Structure

```
.
├── src/main/java/...      # Spring Boot application (controller, service, dto, exception)
├── src/test/java/...      # JUnit 5 and MockMvc tests
├── pom.xml                # Maven build
├── Dockerfile             # multi-stage image build
├── .dockerignore
├── docker-compose.yml     # calculator replicas + nginx proxy
├── nginx/nginx.conf       # reverse proxy / load balancer config
├── Jenkinsfile            # declarative CI/CD pipeline
└── README.md
```


---

## Docker

The `Dockerfile` is multi-stage: a Maven/JDK 21 stage builds the JAR (dependencies are resolved from `pom.xml` first so they are cached as their own layer), and a slim JRE 21 Alpine stage copies only the JAR. The container runs as a non-root user and exposes port 8080.

```bash
docker build -t krishnachaitanya1605/calculator-api:latest .
docker run -d --name calculator -p 8080:8080 krishnachaitanya1605/calculator-api:latest
curl "localhost:8080/api/v1/add?a=2&b=3"
docker rm -f calculator
```

> Quote URLs that contain `?` and `&`, otherwise the shell will interpret them.

Published image: https://hub.docker.com/r/krishnachaitanya1605/calculator-api

---

## Docker Compose

`docker-compose.yml` brings up the whole stack with one command:

```bash
docker compose up -d
```

| Component | Role |
|---|---|
| `calculator` (2 replicas) | The Spring Boot API, with a health check on `/actuator/health` and `restart: unless-stopped` |
| `nginx` | Reverse proxy and load balancer in front of the replicas; starts only after the API is healthy |
| `calc-net` | Named bridge network shared by both services |

The proxy listens on **http://localhost:8080** by default. Set `APP_PORT` to use another host port, and `IMAGE_TAG` to choose the image tag:

```bash
APP_PORT=8081 IMAGE_TAG=12 docker compose up -d
```

Nginx resolves the `calculator` service through Docker DNS for every request, so traffic is spread across the replicas and recreated replicas are picked up without restarting Nginx. Its access log records which replica served each request:

```bash
docker compose logs nginx | grep -- '->'
```

Stop the stack with `docker compose down`.

---

## CI/CD Pipeline (Jenkins)

The declarative pipeline in the `Jenkinsfile` runs automatically on every push to `main` through a GitHub webhook:

| Stage | What it does |
|---|---|
| Checkout | Pulls the latest code from GitHub |
| Build | `./mvnw clean package -DskipTests` |
| Test | `./mvnw test`, publishes JUnit results, fails the build on test failure |
| Docker Build | Builds the image tagged with the build number and `latest` |
| Push | Pushes both tags to Docker Hub using the Jenkins credential `dockerhub-credentials` |
| Deploy | `docker compose up -d` on port 8081, then polls `/actuator/health` until the stack reports `UP` |

A success or failure email is sent when the build finishes.

The deploy stage is idempotent: re-running with the same image tag leaves the running containers alone, and a new tag recreates only the replicas.

### Jenkins setup

1. Install the Pipeline, Git, GitHub, JUnit and Mailer plugins.
2. Add a **Username with password** credential with ID `dockerhub-credentials` (Docker Hub username and an access token). No secrets are stored in this repository.
3. Configure SMTP under *Manage Jenkins -> System -> E-mail Notification*.
4. Create a Pipeline job using *Pipeline script from SCM*, pointing at this repository, branch `*/main`, script path `Jenkinsfile`, and tick *GitHub hook trigger for GITScm polling*.
5. Add a GitHub webhook to `http://<jenkins-host>/github-webhook/` for push events.

After a successful run the API is available at http://localhost:8081.
