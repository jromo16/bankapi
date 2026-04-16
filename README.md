# 🏦 BankAPI — Banking REST API

A production-ready REST API for banking operations built with **Spring Boot 3**, **Spring Security + JWT**, and **PostgreSQL**.

---

## 🚀 Features

- **JWT Authentication** — Register and login with stateless token-based auth
- **Account Management** — Create savings/checking accounts with auto-generated account numbers
- **Money Transfers** — ACID-compliant transfers with validations (balance, account status)
- **Transaction History** — Paginated history with optional date range filter
- **Global Error Handling** — Custom exceptions with structured JSON error responses
- **API Documentation** — Interactive Swagger UI at `/swagger-ui.html`
- **Unit Tests** — JUnit 5 + Mockito test coverage for core services

---

## 🛠️ Tech Stack

| Layer          | Technology                        |
|----------------|-----------------------------------|
| Framework      | Spring Boot 3.2                   |
| Security       | Spring Security + JWT (JJWT 0.11) |
| Database       | PostgreSQL + Spring Data JPA      |
| Build Tool     | Maven                             |
| Documentation  | SpringDoc OpenAPI (Swagger UI)    |
| Testing        | JUnit 5 + Mockito                 |
| Java Version   | Java 17                           |

---

## 📋 API Endpoints

### Auth
| Method | Endpoint            | Description        | Auth |
|--------|---------------------|--------------------|------|
| POST   | `/api/auth/register`| Register new user  | No   |
| POST   | `/api/auth/login`   | Login, get token   | No   |

### Accounts
| Method | Endpoint                    | Description              | Auth |
|--------|-----------------------------|--------------------------|------|
| POST   | `/api/accounts`             | Create account           | Yes  |
| GET    | `/api/accounts`             | Get my accounts          | Yes  |
| GET    | `/api/accounts/{number}`    | Get account by number    | Yes  |

### Transactions
| Method | Endpoint                              | Description             | Auth |
|--------|---------------------------------------|-------------------------|------|
| POST   | `/api/transactions/transfer`          | Transfer funds          | Yes  |
| GET    | `/api/transactions/history/{number}`  | Get history (paginated) | Yes  |

---

## ⚙️ Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 15+

### 1. Clone the repository
```bash
git clone https://github.com/YOUR_USERNAME/bankapi.git
cd bankapi
```

### 2. Create the database
```sql
CREATE DATABASE bankapi;
```

### 3. Configure credentials
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.username=YOUR_USER
spring.datasource.password=YOUR_PASSWORD
```

### 4. Run the application
```bash
mvn spring-boot:run
```

### 5. Access Swagger UI
```
http://localhost:8080/swagger-ui.html
```

---

## 🧪 Running Tests

```bash
mvn test
```

Tests use an **H2 in-memory database** — no PostgreSQL needed for testing.

---

## 📐 Architecture

```
com.bankapi
├── config/         # Security, OpenAPI beans
├── controller/     # REST endpoints
├── service/        # Business logic
├── repository/     # Data access (Spring Data JPA)
├── model/          # JPA Entities (User, Account, Transaction)
├── dto/            # Request / Response objects
├── exception/      # Custom exceptions + GlobalExceptionHandler
└── security/       # JWT filter and service
```

---

## 🔐 Example Usage

**Register:**
```json
POST /api/auth/register
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "dni": "12345678A"
}
```

**Transfer:**
```json
POST /api/transactions/transfer
Authorization: Bearer <token>
{
  "sourceAccountNumber": "1234567890",
  "targetAccountNumber": "0987654321",
  "amount": 250.00,
  "description": "Rent payment"
}
```

---

## 📄 License

MIT License — feel free to use this project as a portfolio piece.
