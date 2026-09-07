import React, { useState } from "react";
import { createRoot } from "react-dom/client";
import "./styles.css";

function App() {
  const [messages, setMessages] = useState([
    {
      role: "assistant",
      text: "Ask a support question. If the knowledge base is confident, I answer. Otherwise I create a ticket through gRPC.",
      meta: "ready"
    }
  ]);
  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);

  async function sendMessage(event) {
    event.preventDefault();
    const text = input.trim();
    if (!text || loading) return;

    setMessages((current) => [...current, { role: "user", text }]);
    setInput("");
    setLoading(true);

    try {
      const response = await fetch("http://localhost:8080/api/chat", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ userId: "demo-user", message: text })
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || `Chat service returned ${response.status}`);
      }

      const data = await response.json();
      const answer = data.answer ?? "The chat service returned an unexpected response.";
      const source = data.source ?? "unknown";
      const confidence = data.confidence ?? 0;
      setMessages((current) => [
        ...current,
        {
          role: "assistant",
          text: answer,
          meta: `${source} · confidence ${confidence}%`
        }
      ]);
    } catch (error) {
      setMessages((current) => [
        ...current,
        { role: "assistant", text: "Chat service is not reachable yet.", meta: error.message }
      ]);
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="app-shell">
      <section className="chat-panel">
        <header>
          <p>Agentic Support Chatbot</p>
          <span>REST + gRPC + PostgreSQL + Redis + Kafka</span>
        </header>

        <div className="messages">
          {messages.map((message, index) => (
            <article className={`message ${message.role}`} key={`${message.role}-${index}`}>
              <p>{message.text}</p>
              {message.meta && <small>{message.meta}</small>}
            </article>
          ))}
        </div>

        <form onSubmit={sendMessage}>
          <input
            value={input}
            onChange={(event) => setInput(event.target.value)}
            placeholder="How do I reset my password?"
          />
          <button disabled={loading}>{loading ? "Sending" : "Send"}</button>
        </form>
      </section>
    </main>
  );
}

createRoot(document.getElementById("root")).render(<App />);
