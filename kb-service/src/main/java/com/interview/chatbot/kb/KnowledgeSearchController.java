package com.interview.chatbot.kb;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/knowledge")
class KnowledgeSearchController {

    private final KnowledgeSearchService searchService;

    KnowledgeSearchController(KnowledgeSearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping("/search")
    KnowledgeSearchResponse search(@RequestBody KnowledgeSearchRequest request) {
        return this.searchService.search(request.query());
    }
}
