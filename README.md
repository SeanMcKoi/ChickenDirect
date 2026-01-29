# Chicken Direct
## Backend Final Exam 2025 (Grade: A)
---

Chicken Direct is a Spring Boot 4 REST API with PostgreSQL, Flyway migrations and Thymeleaf views.

This README explains how to run the app locally using Docker for the database.

---

## Prerequisites

Make sure you have installed:

* Java 21 (JDK)
* Maven 3.x
* Docker & Docker Compose

---

## 1. Start PostgreSQL with Docker Compose

Start PostgreSQL:

```bash
docker-compose up -d
```

This will:

* Start a Postgres container
* Expose it on `localhost:5432`
* Create the database `appdb` with user `appuser` / password `pirate`

---

## 2. Run the Application

### Option A: Run with Maven

```bash
mvn spring-boot:run
```

### Option B: Build and run the JAR

```bash
mvn clean package
java -jar target/chicken-direct-0.0.1-SNAPSHOT.jar
```

Application starts at:

```
http://localhost:8080
```

---

## 3. API & Endpoints

### Base API URL

```
http://localhost:8080/api
```

### Order Receipts

```
GET http://localhost:8080/orders/{orderId}/receipt
```

Example:

```bash
curl http://localhost:8080/orders/123/receipt
```

---

## 4. Running Tests

Run the full test suite:

```bash
mvn test
```

Includes:

* Spring Boot Test
* Testcontainers (PostgreSQL)
* JUnit 5
* Jacoco coverage (70% minimum line coverage)

---

## 5. Typical Developer Workflow

1. Start DB

   ```bash
   docker-compose up -d
   ```
2. Run the app

   ```bash
   mvn spring-boot:run
   ```
3. Test endpoints

    * `http://localhost:8080/api/...`
    * `http://localhost:8080/orders/{orderId}/receipt`
4. Run tests

   ```bash
   mvn test
   ```

---

## 6. Troubleshooting

### App cannot connect to the database

* Make sure the container is running:

  ```bash
  docker ps
  ```
* Ensure credentials match both Docker and Spring configs.

### Port 8080 already in use

Change:

```properties
server.port=8081
```

### Flyway errors

* Ensure migration scripts in `db/migration` are valid
* Ensure database schema matches entity definitions

---
