package com.interview.chatbot.kb;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "knowledge_articles")
class KnowledgeArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String body;

    protected KnowledgeArticle() {
    }

    Long getId() {
        return this.id;
    }

    String getTitle() {
        return this.title;
    }

    String getBody() {
        return this.body;
    }
}
