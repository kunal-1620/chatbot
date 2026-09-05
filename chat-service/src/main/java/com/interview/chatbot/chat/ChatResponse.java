package com.interview.chatbot.chat;

record ChatResponse(String answer, String source, int confidence, Long ticketId) {
}
