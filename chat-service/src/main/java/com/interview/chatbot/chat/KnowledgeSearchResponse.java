package com.interview.chatbot.chat;

record KnowledgeSearchResponse(Long articleId, String title, String answer, int confidence) {
}
