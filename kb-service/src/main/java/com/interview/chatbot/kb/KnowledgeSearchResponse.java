package com.interview.chatbot.kb;

record KnowledgeSearchResponse(Long articleId, String title, String answer, int confidence) {
}
