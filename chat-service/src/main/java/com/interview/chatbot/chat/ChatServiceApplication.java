package com.interview.chatbot.chat;

import com.interview.chatbot.proto.ticket.TicketServiceGrpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.grpc.client.ImportGrpcClients;

@SpringBootApplication
@ImportGrpcClients(target = "ticket-service", types = TicketServiceGrpc.TicketServiceBlockingStub.class)
public class ChatServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatServiceApplication.class, args);
    }
}
