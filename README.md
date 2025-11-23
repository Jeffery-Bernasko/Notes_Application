# Notes_Application

A full-stack Spring Boot + PostgreSQL Notes Web App with JWT Authentication, Pagination, Search, Docker, Flyway DB migrations, and Swagger API docs.

## 🔧 Tech Stack

- **Backend:** Java 17, Spring Boot 3.x+, Spring Security, Spring Data JPA
- **Database:** PostgreSQL + Flyway (for migrations)
- **Frontend:** HTML + Vanilla JS + Bootstrap (Thymeleaf rendered)
- **Testing:** JUnit 5, MockMvc, H2/Testcontainers
- **API Docs:** OpenAPI/Swagger (via springdoc-openapi)
- **Containerization:** Docker, Docker Compose

---

##  Getting Started

###  Run the app with Docker

```bash
docker-compose up --build
```

> This will spin up:
> - PostgreSQL DB (with migrations applied using Flyway)
> - Spring Boot app (available at [http://localhost:8001](http://localhost:8001))

###  Run Tests

```bash

---

## 🌐 API Documentation

Once running, access Swagger UI at:

> [http://localhost:8001/swagger-ui.html](http://localhost:8001/swagger-ui.html)

API spec is available at:

> [http://localhost:8001/v3/api-docs](http://localhost:8001/v3/api-docs)

---

##  Design Decisions

### Security
- Stateless JWT-based authentication with custom `JwtAuthenticationFilter`
- Passwords hashed using `BCryptPasswordEncoder`
- `/api/**` protected, `/ui/**` and static assets allowed without auth

### ✅ Persistence
- PostgreSQL in production, 
- Flyway handles DB migrations via Docker volume
- Pagination & search built with Spring Data


### ✅ Deployment
- Docker Compose ensures DB and app are properly networked
- Wait strategies ensure Flyway finishes before app starts

---

## 👤 Default Credentials (Dev Only)

You can register via the Sign Up page (no hardcoded users).

---

## 📁 Project Structure

```
.
├── src/main/java/org/example/notes_application
│   ├── controller
│   ├── service
│   ├── security
│   ├── model
│   ├── dto
├── src/main/resources/db/migration       # Flyway SQL migrations
├── src/main/resources/templates/         # Thymeleaf HTML files
├── Dockerfile
├── docker-compose.yaml
├── README.md
```

