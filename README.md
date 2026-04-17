# Seren AI 💙 — AI Mom Companion

A warm, empathetic AI companion rebuilt with **Java 17 · Spring Boot 3 · PostgreSQL · React.js**.

---

## Tech Stack

| Layer        | Technology                        |
|--------------|-----------------------------------|
| Backend      | Java 17, Spring Boot 3.2          |
| LLM          | Groq API (llama3-8b-8192)         |
| Database     | PostgreSQL                        |
| ORM          | Spring Data JPA / Hibernate       |
| Frontend     | React 18, CSS Modules             |
| HTTP Client  | Spring WebFlux WebClient          |
| Build        | Maven                             |

---

## Prerequisites

- Java 17+
- Maven 3.8+
- Node.js 18+ and npm
- PostgreSQL running locally (or a remote DB URL)
- A [Groq API key](https://console.groq.com/)

---

## Quick Start

### 1. Clone / unzip the project

```bash
cd seren-ai
```

### 2. Configure environment

Create `backend/src/main/resources/application-local.properties` or set environment variables:

```properties
GROQ_API_KEY=your_groq_api_key_here
DATABASE_URL=jdbc:postgresql://localhost:5432/serenai
DB_USERNAME=postgres
DB_PASSWORD=postgres
```

Or export as environment variables before running:

```bash
export GROQ_API_KEY=your_groq_api_key_here
export DATABASE_URL=jdbc:postgresql://localhost:5432/serenai
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
```

### 3. Create the PostgreSQL database

```sql
CREATE DATABASE serenai;
```

Spring Boot will auto-create the `conversations` table on first run.

### 4. Run the backend

```bash
cd backend
mvn spring-boot:run
```

Backend starts at: **http://localhost:8080**

### 5. Run the frontend

```bash
cd frontend
npm install
npm start
```

Frontend starts at: **http://localhost:3000**

---

## API Reference

### POST `/api/chat`
Send a message and receive Seren's reply.

**Request:**
```json
{
  "message": "I'm feeling stressed today",
  "sessionId": "optional-existing-session-id"
}
```

**Response:**
```json
{
  "reply": "Oh dear, stress can feel so heavy sometimes...",
  "sessionId": "uuid-string",
  "conversationId": 42
}
```

### GET `/api/history/{sessionId}`
Retrieve full conversation history for a session.

### DELETE `/api/history/{sessionId}`
Clear all conversation history for a session.

### GET `/api/health`
Simple health check endpoint.

---

## Project Structure

```
seren-ai/
├── backend/
│   ├── pom.xml
│   └── src/main/java/com/serenai/
│       ├── SerenAiApplication.java
│       ├── controller/ChatController.java
│       ├── service/
│       │   ├── ChatService.java
│       │   └── GroqService.java
│       ├── model/Conversation.java
│       ├── repository/ConversationRepository.java
│       ├── dto/ChatDto.java
│       └── config/
│           ├── AppConfig.java
│           └── GlobalExceptionHandler.java
└── frontend/
    ├── package.json
    ├── public/index.html
    └── src/
        ├── App.js / App.css
        ├── index.js
        ├── services/api.js
        └── pages/
            ├── ChatPage.js
            └── ChatPage.module.css
```

---

## Author

**Boshini TP** — Aspiring Software Developer | AI/ML Enthusiast | Backend Developer
