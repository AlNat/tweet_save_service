package dev.alnat.tweet.saveservice.service;

import dev.alnat.tweet.saveservice.dto.TweetDTO;

/**
 * Created by @author AlNat on 27.07.2023
 * Licensed by Apache License, Version 2.0
 */
public interface TweetService {

    void save(TweetDTO dto);

}
