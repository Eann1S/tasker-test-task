# Tasker App

## Overview

Tasker is a Spring Boot application designed to manage tasks with features such as user authentication, task assignment, and status updates. The application leverages PostgreSQL for database storage, Redis for caching, and includes a Docker setup for easy deployment.

---

## Features

- **User Management**: Authentication and user profiles.
- **Task Management**: Task creation, updates, assignment, and commenting.
- **Comment Management**: Add or delete comments on tasks.
- **Admin Controls**: Role-based permissions for task and user management.

---

## Prerequisites

- **Java 17**
- **Maven 3.8+**
- **Docker** and **Docker Compose**

---

## Project Structure

- **REST Controllers**: Handle API requests (`/api/users`, `/api/tasks`, `/api/comments`, `/api/auth`).
- **Services**: Encapsulate business logic.
- **Repositories**: Database interaction.
- **Models and DTOs**: Data representation and transfer.
- **Security**: Role-based access control with JWT authentication.

---

## How to Run Locally

### 1. Clone the Repository
```bash
git clone <repository_url>
cd tasker-test-task
```

### 2. Configure Environment Variables
Create a `.env` file in the project root with the following content:
```env
POSTGRES_USER=
POSTGRES_PASSWORD=
POSTGRES_DB=
DB_URL=

JWT_SECRET=

ADMIN_EMAIL=
ADMIN_PASSWORD=

REDIS_HOST=
REDIS_PORT=
```

### 3. Build Docker Images
Run the following command to build and spin up the application:
```bash
docker-compose up --build
```

This will start:
- The Spring Boot app on `http://localhost:8080`
- PostgreSQL on port `5432`
- Redis on port `6379`

### 4. Access the API Documentation
Visit `http://localhost:8080/swagger-ui.html` for API documentation.

---

## Key Endpoints

### User API
- `GET /api/users/me` - Get authenticated user profile.

### Task API
- `POST /api/tasks` - Create a new task (Admin only).
- `GET /api/tasks` - Retrieve all tasks (Admin only).
- `PUT /api/tasks/{taskId}` - Update a task (Admin only).
- `DELETE /api/tasks/{taskId}` - Delete a task (Admin only).

### Comment API
- `POST /api/tasks/{taskId}/comment` - Add a comment to a task.
- `DELETE /api/comments/{commentId}` - Delete a comment (Admin or Author only).

### Authentication API
- `POST /api/auth/login` - Login and obtain a JWT.
- `POST /api/auth/register` - Register a new user.
- `POST /api/auth/logout` - Logout the authenticated user.

---

## Testing

This project uses JUnit and Testcontainers for integration testing.

### Running Tests
```bash
mvn clean test
```

---

## Deployment

To package the application for deployment:
```bash
mvn clean package
```

The resulting JAR file will be located in the `target/` directory and can be run with:
```bash
java -jar target/tasker-app-1.0.0.jar
```

---

## Technologies Used

- **Spring Boot**: Core framework.
- **PostgreSQL**: Relational database.
- **Redis**: Caching.
- **Docker**: Containerization.
- **OpenAPI**: API documentation.
- **JWT**: Secure authentication.