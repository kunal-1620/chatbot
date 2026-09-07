package com.interview.chatbot.chat;

import com.interview.chatbot.proto.ticket.CreateTicketRequest;
import com.interview.chatbot.proto.ticket.CreateTicketResponse;
import com.interview.chatbot.proto.ticket.TicketServiceGrpc;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
class ChatOrchestrator {

    private final RestClient kbClient;
    private final TicketServiceGrpc.TicketServiceBlockingStub ticketStub;
    private final StringRedisTemplate redisTemplate;

    ChatOrchestrator(RestClient.Builder restClientBuilder,
                     TicketServiceGrpc.TicketServiceBlockingStub ticketStub,
                     StringRedisTemplate redisTemplate) {
        this.kbClient = restClientBuilder.baseUrl("http://localhost:8081").build();
        this.ticketStub = ticketStub;
        this.redisTemplate = redisTemplate;
    }

    ChatResponse handle(ChatRequest request) {
        if (request.message() == null || request.message().isBlank()) {
            return new ChatResponse("Please enter a question.", "chat-service", 100, null);
        }

        if (isGreeting(request.message())) {
            return new ChatResponse(
                    "Hi. Ask me a support question, and I will search the knowledge base first.",
                    "chat-service",
                    100,
                    null);
        }

        cacheLastQuestion(request);

        KnowledgeSearchResponse kbResponse = this.kbClient.post()
                .uri("/api/knowledge/search")
                .body(new KnowledgeSearchRequest(request.message()))
                .retrieve()
                .body(KnowledgeSearchResponse.class);

        if (kbResponse != null && kbResponse.confidence() >= 60) {
            return new ChatResponse(kbResponse.answer(), "knowledge-base", kbResponse.confidence(), null);
        }

        CreateTicketResponse ticket = this.ticketStub.createTicket(CreateTicketRequest.newBuilder()
                .setUserId(request.userId() == null || request.userId().isBlank() ? "anonymous" : request.userId())
                .setQuestion(request.message())
                .setReason("Low knowledge-base confidence")
                .build());

        String answer = "I could not find a confident answer, so I created support ticket #" + ticket.getTicketId() + ".";
        return new ChatResponse(answer, "ticket-service-grpc", 0, ticket.getTicketId());
    }

    private void cacheLastQuestion(ChatRequest request) {
        String userId = request.userId() == null || request.userId().isBlank() ? "anonymous" : request.userId();
        this.redisTemplate.opsForValue().set("chat:last-question:" + userId, request.message());
    }

    private static boolean isGreeting(String message) {
        String normalized = message.trim().toLowerCase();
        return normalized.equals("hi")
                || normalized.equals("hello")
                || normalized.equals("hey")
                || normalized.equals("hi there")
                || normalized.equals("hello there");
    }
}
