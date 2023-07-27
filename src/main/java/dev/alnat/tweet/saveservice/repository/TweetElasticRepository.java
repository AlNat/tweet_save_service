package dev.alnat.tweet.saveservice.repository;

import dev.alnat.tweet.saveservice.model.Tweet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by @author AlNat on 27.07.2023
 * Licensed by Apache License, Version 2.0
 */
public interface TweetElasticRepository extends ElasticsearchRepository<Tweet, String> {

    Page<Tweet> findByAuthor(String author, PageRequest pageRequest);

}
