# Shop API -- Production-Oriented Spring Boot Backend

RESTful backend for an e-commerce system built with Spring Boot.
Designed with production practices in mind: layered architecture, JWT
security, transaction consistency, Redis caching, and strict commit
standards.

------------------------------------------------------------------------

## 🧠 Project Objective

-   Build a production-style backend system
-   Implement JWT authentication & role-based authorization (USER /
    ADMIN)
-   Standardize API response & centralized error handling
-   Ensure transaction consistency in checkout flow
-   Integrate Redis for performance optimization
-   Designed to work with Angular frontend (separate repository)

------------------------------------------------------------------------

## 🛠 Tech Stack

-   Java 17
-   Spring Boot
-   Spring Security + JWT
-   Spring Data JPA
-   MySQL
-   Redis (Cache layer)
-   Maven
-   Docker (Redis container)
-   Postman (API testing)

------------------------------------------------------------------------

## 📦 Architecture Overview

Layered architecture:

Controller → Service → Repository → Database

### Design Principles

-   Business logic isolated in Service layer
-   DTO separation (no entity exposure)
-   Global exception handling
-   Response wrapper standardization
-   Transaction boundary defined at Service level
-   Cache layer separated from source of truth
-   Stateless authentication design

------------------------------------------------------------------------

## 📂 Project Structure

com.shop ├─ auth \# authentication & registration ├─ security \# spring
security, jwt filter, config ├─ user \# user profile & user service ├─
catalog \# product, category, admin product ├─ cart \# shopping cart
logic ├─ order \# checkout & transaction logic ├─ common \# response
wrapper, exception, util └─ config \# shared configuration (cache, etc.)

------------------------------------------------------------------------

## 🔐 Authentication & Authorization

-   JWT-based authentication
-   Bearer token required for protected endpoints
-   Role-based authorization:
    -   USER → cart, checkout
    -   ADMIN → product management
-   SecurityContext injected per request via custom JWT filter
-   Stateless design (no server-side session)

------------------------------------------------------------------------

## ⚡ Caching Strategy (Redis)

-   Pattern: Cache Aside
-   TTL: 5 minutes (performance vs data freshness trade-off)
-   Cached endpoints:
    -   Product detail
-   Cache invalidation on update/delete
-   Redis acts as performance layer
-   MySQL remains source of truth

------------------------------------------------------------------------

## 💳 Transaction & Consistency

-   Checkout flow wrapped in @Transactional
-   Ensures atomic order creation and inventory update
-   Prevents partial writes in case of failure
-   Avoids inconsistent system state

------------------------------------------------------------------------

## ❗ Error Handling Strategy

-   Centralized via GlobalExceptionHandler
-   Structured JSON response
-   Validation errors mapped clearly
-   BindException & MethodArgumentNotValidException handled explicitly
-   Consistent error codes (ERR_VALIDATION, ERR_NOT_FOUND,
    ERR_UNAUTHORIZED, ERR_INTERNAL)

------------------------------------------------------------------------

## 🔍 Architectural Decisions

### Why Cache Aside?

-   Keeps database as source of truth
-   Simple invalidation strategy
-   Avoids tight coupling between DB and cache layer

### Why Stateless JWT?

-   Horizontal scalability
-   No server-side session storage
-   Clear separation of authentication logic

### Why Service-Level Transaction?

-   Clear transaction boundary
-   Ensures business consistency
-   Prevents partial writes during checkout

------------------------------------------------------------------------

## ▶️ How to Run

1.  Configure database in application.properties
2.  Run Redis: docker run -d -p 6379:6379 redis:7-alpine
3.  Start application: mvn spring-boot:run

------------------------------------------------------------------------

## 🛠 Commit Convention

Format: type(scope): subject

Allowed Types: feat, fix, refactor, perf, test, docs, style, chore

Allowed Scopes: auth, security, product, category, cart, order, common,
config, exception, validation, db, build, ci

Commit will be rejected if scope is invalid.

------------------------------------------------------------------------

## 🧠 Engineering Notes

This repository documents real production-level debugging scenarios.

Topics include: - Redis serialization issues - Spring Cache proxy
behavior - Transaction consistency debugging - Validation & type
mismatch handling

See mistakes.md for detailed root-cause analysis.

------------------------------------------------------------------------

## 📌 Project Status

Backend: Stable (Core modules completed) Frontend: Angular client
(separate repository)

------------------------------------------------------------------------

## 👤 Author

Thanh Java Backend Developer
