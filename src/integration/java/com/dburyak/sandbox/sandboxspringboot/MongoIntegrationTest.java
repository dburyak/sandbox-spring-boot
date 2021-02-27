package com.dburyak.sandbox.sandboxspringboot;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;

@DataMongoTest
@ActiveProfiles("integration-test")
@Testcontainers
public class MongoIntegrationTest {
    public static final String MONGO_VERSION = "4.4.4";

    @Autowired
    private ReactiveMongoOperations mongo;

    @Container
    public static final MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(
            DockerImageName.parse("mongo:" + MONGO_VERSION));

    @DynamicPropertySource
    public static void mongoProperties(DynamicPropertyRegistry reg) {
        reg.add("spring.data.mongodb.host", MONGO_CONTAINER::getHost);
        reg.add("spring.data.mongodb.port", MONGO_CONTAINER::getFirstMappedPort);
    }

    @BeforeEach
    public void initDb() {
        StepVerifier.create(mongo.getCollectionNames()
                .flatMap(col -> mongo.dropCollection(col)))
                .verifyComplete();
        StepVerifier.create(mongo.getCollectionNames())
                .verifyComplete();
    }
}
