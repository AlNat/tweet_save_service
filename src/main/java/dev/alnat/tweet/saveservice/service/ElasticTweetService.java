package dev.alnat.tweet.saveservice.service;

import dev.alnat.tweet.saveservice.dto.TweetDTO;
import dev.alnat.tweet.saveservice.mapper.TweetMapper;
import dev.alnat.tweet.saveservice.repository.TweetElasticRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by @author AlNat on 27.07.2023
 * Licensed by Apache License, Version 2.0
 */
@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class ElasticTweetService implements TweetService {

    private final TweetMapper mapper;
    private final TweetElasticRepository repository;

    @Override
    public void save(TweetDTO dto) {
        var entity = mapper.dtoToEntity(dto);
        repository.save(entity);
    }

}
