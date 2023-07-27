package dev.alnat.tweet.saveservice.mapper;

import dev.alnat.tweet.saveservice.dto.TweetDTO;
import dev.alnat.tweet.saveservice.model.Tweet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Created by @author AlNat on 27.07.2023
 * Licensed by Apache License, Version 2.0
 */
@Mapper(componentModel = "spring")
public interface TweetMapper {

    TweetDTO entityToDTO(Tweet domain);

    @Mapping(target = "id", ignore = true)
    Tweet dtoToEntity(TweetDTO dto);

}
