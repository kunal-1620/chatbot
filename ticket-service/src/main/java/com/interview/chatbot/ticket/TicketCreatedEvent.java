package com.interview.chatbot.ticket;

record TicketCreatedEvent(Long ticketId, String userId, String question, String status) {
}
