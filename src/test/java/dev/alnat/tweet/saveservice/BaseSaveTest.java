package dev.alnat.tweet.saveservice;

import dev.alnat.tweet.saveservice.repository.TweetElasticRepository;
import dev.alnat.tweet.saveservice.tools.CustomElasticSearchContainer;
import lombok.SneakyThrows;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by @author AlNat on 27.07.2023
 * Licensed by Apache License, Version 2.0
 */
@SpringBootTest
@Testcontainers
@DirtiesContext // due kafka
class BaseSaveTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private TweetElasticRepository repository;


    @Container
    @ServiceConnection(name = "elastic")
    static final ElasticsearchContainer elastic = new CustomElasticSearchContainer();

    @Container
    @ServiceConnection(name = "kafka")
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.6"));


    private static final Set<String> KAFKA_TOPICS = Set.of("tweet.save");

    @BeforeAll
    static void initKafkaTopics() {
        try (var admin = AdminClient.create(Map.of(BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers()))) {
            admin.createTopics(KAFKA_TOPICS.stream().map(topic -> new NewTopic(topic, 1, (short) 1)).collect(Collectors.toList()));
        }

        Awaitility.setDefaultPollInterval(1, TimeUnit.SECONDS);
        Awaitility.setDefaultPollDelay(Duration.ZERO);
        Awaitility.setDefaultTimeout(Duration.ofSeconds(30));
    }

    @AfterEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    @SneakyThrows
    void testSingleTweet() {
        var json = """
                {
                  "author": "Dow",
                  "tags": [
                    "tag1",
                    "tag2"
                  ],
                  "tweet": "This is the tweet text",
                  "sendAt": "2021-02-13 12:00:00"
                }
                """;

        kafkaTemplate.send("tweet.save", "Dow", json);

        await().atLeast(Duration.ofSeconds(1))
                .atMost(Duration.ofSeconds(30))
                .until(() -> StreamSupport.stream(repository.findAll().spliterator(), false).count(), is(equalTo(1L)));

        var allTweets = StreamSupport.stream(repository.findAll().spliterator(), false).toList();
        assertEquals(1, allTweets.size());
        assertTrue(allTweets.stream().allMatch(tweet -> tweet.getAuthor().equals("Dow")));
    }

    @Test
    @SneakyThrows
    void testMultipleTweet() {
        var json = """
                {
                  "author": "Dow",
                  "tags": [
                    "tag1",
                    "tag2"
                  ],
                  "tweet": "This is the tweet text",
                  "sendAt": "2021-02-13 12:01:00"
                }
                """;

        kafkaTemplate.send("tweet.save", "Dow", json);
        json = """
                {
                  "author": "Dow",
                  "tags": [
                    "tag2",
                    "tag3"
                  ],
                  "tweet": "This is the second tweet text",
                  "sendAt": "2021-02-13 12:02:00"
                }
                """;
        kafkaTemplate.send("tweet.save", "Dow", json);
        json = """
                {
                  "author": "Dow",
                  "tags": [
                    "tag3",
                    "tag1"
                  ],
                  "tweet": "This is the third tweet text",
                  "sendAt": "2021-02-13 12:03:00"
                }
                """;
        kafkaTemplate.send("tweet.save", "Dow", json);

        await().atLeast(Duration.ofSeconds(1))
                .atMost(Duration.ofSeconds(30))
                .until(() -> StreamSupport.stream(repository.findAll().spliterator(), false).count(), is(equalTo(3L)));

        var allTweets = StreamSupport.stream(repository.findAll().spliterator(), false).toList();
        assertEquals(3, allTweets.size());
        assertTrue(allTweets.stream().allMatch(tweet -> tweet.getAuthor().equals("Dow")));
        assertTrue(allTweets.stream().allMatch(tweet -> tweet.getSendAt().isAfter(LocalDateTime.of(2021, 2, 13, 12, 0, 0))));
    }

}
