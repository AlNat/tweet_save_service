package dev.alnat.tweet.saveservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.alnat.tweet.saveservice.dto.TweetDTO;
import dev.alnat.tweet.saveservice.service.TweetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Created by @author AlNat on 27.07.2023
 * Licensed by Apache License, Version 2.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SaveTopicListener {

    private final TweetService service;
    private final ObjectMapper mapper;

    @KafkaListener(
            topics      = "#{environment.getRequiredProperty('spring.kafka.topics.tweet-api.save.topic')}",
            groupId     = "#{environment.getRequiredProperty('spring.kafka.topics.tweet-api.save.group')}",
            autoStartup = "#{environment.getRequiredProperty('spring.kafka.topics.tweet-api.save.enabled')}"
    )
    public void onMessage(@Payload String data) {
        var res = parse(data);
        if (res != null) {
            service.save(res);
        }
    }

    private TweetDTO parse(final String rawJson) {
        if (log.isTraceEnabled()) {
            log.trace("Start processed TweetDTO {}", rawJson);
        }

        TweetDTO response;
        try {
            response = mapper.readValue(rawJson, TweetDTO.class);
        } catch (Exception e) {
            log.error("Exception while parse TweetDTO {}", rawJson, e);
            return null;
        }

        response.setReceivedAt(LocalDateTime.now());
        return response;
    }

}
