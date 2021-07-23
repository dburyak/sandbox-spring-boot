package com.dburyak.sandbox.sandboxspringboot;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
@ActiveProfiles("integration-test")
@Testcontainers
@Slf4j
@DirtiesContext
public class MongoIntegrationTest {
    public static final String MONGO_VERSION;
    public static final String REDIS_VERSION = "6.0.1";

    static {
        // TODO: read version here from application-integration-test.yml config
        MONGO_VERSION = "4.4.4";
    }

    @Autowired
    protected ReactiveMongoOperations mongo;

    @Container
    protected static final MongoDBContainer MONGO_CONTAINER = new MongoDBContainer("mongo:" + MONGO_VERSION);

    @DynamicPropertySource
    protected static void mongoProperties(DynamicPropertyRegistry reg) {
        reg.add("spring.data.mongodb.uri", () -> {
            log.debug("using mongo config: uri={}", MONGO_CONTAINER.getReplicaSetUrl());
            return MONGO_CONTAINER.getReplicaSetUrl();
        });
    }

    @AfterEach
    protected void cleanupAllDataInDb() {
        log.debug("removing all mongo data");
        StepVerifier
                .create(mongo.getCollectionNames()
                        .flatMap(col -> mongo.remove(new Query(), col))
                        .collectList()
                )
                .expectNextCount(1L)
                .verifyComplete();
    }
}
