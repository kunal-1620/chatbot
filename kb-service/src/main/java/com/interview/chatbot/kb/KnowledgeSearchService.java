package com.interview.chatbot.kb;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
class KnowledgeSearchService {

    private static final Pattern WORD = Pattern.compile("[^a-z0-9]+", Pattern.CASE_INSENSITIVE);
    private static final Set<String> STOP_WORDS = Set.of("a", "an", "and", "are", "can", "for", "how", "i",
            "is", "it", "of", "on", "the", "to", "what", "with", "you", "your");

    private final KnowledgeArticleRepository repository;

    KnowledgeSearchService(KnowledgeArticleRepository repository) {
        this.repository = repository;
    }

    KnowledgeSearchResponse search(String query) {
        if (query == null || query.isBlank()) {
            return new KnowledgeSearchResponse(null, null, "Please ask a knowledge-base question.", 0);
        }

        List<String> terms = terms(query);
        return this.repository.findAll().stream()
                .map(article -> scored(article, terms))
                .max(Comparator.comparingInt(ScoredArticle::score))
                .filter(scored -> scored.score() > 0)
                .map(scored -> {
                    int confidence = Math.min(95, 40 + scored.score() * 15);
                    KnowledgeArticle article = scored.article();
                    return new KnowledgeSearchResponse(article.getId(), article.getTitle(), article.getBody(), confidence);
                })
                .orElse(new KnowledgeSearchResponse(null, null, "No confident knowledge-base match found.", 0));
    }

    private static ScoredArticle scored(KnowledgeArticle article, List<String> queryTerms) {
        String text = (article.getTitle() + " " + article.getBody()).toLowerCase(Locale.ROOT);
        int score = 0;
        for (String term : queryTerms) {
            if (text.contains(term)) {
                score++;
            }
        }
        return new ScoredArticle(article, score);
    }

    private static List<String> terms(String query) {
        return WORD.splitAsStream(query.toLowerCase(Locale.ROOT))
                .filter(word -> word.length() > 1)
                .filter(word -> !STOP_WORDS.contains(word))
                .distinct()
                .toList();
    }

    private record ScoredArticle(KnowledgeArticle article, int score) {
    }
}
