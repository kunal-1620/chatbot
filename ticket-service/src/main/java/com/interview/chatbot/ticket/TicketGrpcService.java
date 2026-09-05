package com.interview.chatbot.ticket;

import com.interview.chatbot.proto.ticket.CreateTicketRequest;
import com.interview.chatbot.proto.ticket.CreateTicketResponse;
import com.interview.chatbot.proto.ticket.TicketServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
class TicketGrpcService extends TicketServiceGrpc.TicketServiceImplBase {

    private static final String TOPIC = "ticket-created";

    private final TicketRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    TicketGrpcService(TicketRepository repository,
                      KafkaTemplate<String, String> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void createTicket(CreateTicketRequest request, StreamObserver<CreateTicketResponse> responseObserver) {
        Ticket ticket = this.repository.save(new Ticket(request.getUserId(), request.getQuestion(), request.getReason()));
        publishTicketCreated(ticket);

        CreateTicketResponse response = CreateTicketResponse.newBuilder()
                .setTicketId(ticket.getId())
                .setStatus(ticket.getStatus())
                .setMessage("Support ticket created")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private void publishTicketCreated(Ticket ticket) {
        TicketCreatedEvent event = new TicketCreatedEvent(
                ticket.getId(), ticket.getUserId(), ticket.getQuestion(), ticket.getStatus());
        this.kafkaTemplate.send(TOPIC, String.valueOf(ticket.getId()), toJson(event));
    }

    private static String toJson(TicketCreatedEvent event) {
        return """
                {"ticketId":%d,"userId":"%s","question":"%s","status":"%s"}\
                """.formatted(event.ticketId(), escape(event.userId()), escape(event.question()), escape(event.status()));
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
