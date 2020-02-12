package com.spring.repositories;

import com.spring.entity.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;


import java.util.List;

public interface ArticleRepository extends ElasticsearchCrudRepository<Article, Long> {

    List<Article> findByTitle(String title);

    List<Article> findByTitleAndContent(String title, String content);

    /**
     * 分页
     * @param title
     * @param content
     * @param pageable
     * @return
     */
    List<Article> findByTitleOrContent(String title, String content, Pageable pageable);

}
