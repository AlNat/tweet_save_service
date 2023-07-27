package dev.alnat.tweet.saveservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Created by @author AlNat on 27.07.2023
 * Licensed by Apache License, Version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "tweet")
public class Tweet {

    @Id
    private String id = UUID.randomUUID().toString();

    @Field(type = FieldType.Text, name = "author")
    private String author;

    private List<String> tags;

    @Field(type = FieldType.Text, name = "tweet")
    private String tweet;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sendAt;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receivedAt;

}
