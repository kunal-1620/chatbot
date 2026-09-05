# Agentic Support Chatbot

Interview project showing a small local system with REST, gRPC, PostgreSQL + pgvector, Redis, Kafka, and React.

## Architecture

```text
React UI
  -> REST -> chat-service :8080
             -> REST -> kb-service :8081
             -> gRPC -> ticket-service :9093

chat-service   -> Redis localhost:6379
kb-service     -> PostgreSQL localhost:5432
ticket-service -> PostgreSQL localhost:5432
ticket-service -> Kafka localhost:9092 topic ticket-created
```

## Manual Infrastructure Commands

Run these manually. Codex should not mutate PostgreSQL, Redis, or Kafka state unless you explicitly ask.

PostgreSQL:

```sql
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS knowledge_articles (
    id BIGSERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    body TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS support_tickets (
    id BIGSERIAL PRIMARY KEY,
    user_id TEXT NOT NULL,
    question TEXT NOT NULL,
    reason TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO knowledge_articles (title, body) VALUES
('Reset password', 'To reset your password, open Account Settings, choose Security, and click Reset Password.'),
('Change email address', 'To change your email address, open Profile Settings and verify the new email address.'),
('Refund policy', 'Refunds are available within 30 days when the order has not been used.');
```

Kafka:

```bash
/opt/homebrew/opt/kafka/bin/kafka-topics --bootstrap-server localhost:9092 --create --if-not-exists --topic ticket-created --partitions 1 --replication-factor 1
```

Redis smoke test:

```bash
/opt/homebrew/opt/redis/bin/redis-cli ping
```

## Run Backend

From the project root:

```bash
mvn clean package
mvn -pl kb-service spring-boot:run
mvn -pl ticket-service spring-boot:run
mvn -pl chat-service spring-boot:run
```

Run each service in a separate terminal tab.

## Run Frontend

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`.
