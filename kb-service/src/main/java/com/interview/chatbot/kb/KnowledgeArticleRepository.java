package com.interview.chatbot.kb;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface KnowledgeArticleRepository extends JpaRepository<KnowledgeArticle, Long> {

    @Query("""
            select article
            from KnowledgeArticle article
            where lower(article.title) like lower(concat('%', :query, '%'))
               or lower(article.body) like lower(concat('%', :query, '%'))
            """)
    List<KnowledgeArticle> searchKeyword(@Param("query") String query);
}
