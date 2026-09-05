package com.interview.chatbot.ticket;

import org.springframework.data.jpa.repository.JpaRepository;

interface TicketRepository extends JpaRepository<Ticket, Long> {
}
