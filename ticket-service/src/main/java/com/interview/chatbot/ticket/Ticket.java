package com.interview.chatbot.ticket;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "support_tickets")
class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false, columnDefinition = "text")
    private String question;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private String status = "OPEN";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    protected Ticket() {
    }

    Ticket(String userId, String question, String reason) {
        this.userId = userId;
        this.question = question;
        this.reason = reason;
    }

    Long getId() {
        return this.id;
    }

    String getStatus() {
        return this.status;
    }

    String getUserId() {
        return this.userId;
    }

    String getQuestion() {
        return this.question;
    }
}
