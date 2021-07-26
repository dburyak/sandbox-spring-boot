package com.dburyak.sandbox.sandboxspringboot.repository;

import com.dburyak.sandbox.sandboxspringboot.MongoIntegrationTest;
import com.dburyak.sandbox.sandboxspringboot.domain.User;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

@Log4j2
public class MySandbox extends MongoIntegrationTest {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    UserRepository userRepository;

    long numGeneratedUsers = 500;
    long numExplicitUsers = 1;

    @BeforeEach
    void setup() {
        mongoTemplate.insert(new User("John", "Wick", LocalDate.of(2094, 7, 1), "Minsk", 100));
        IntStream.iterate(1, i -> i + 1)
                .limit(numGeneratedUsers)
                .forEach(i -> mongoTemplate.insert(new User(String.format("firstName-generated-%d", i),
                        String.format("lastName-generated-%d", i),
                        LocalDate.of(1980 + (i % 10), 1 + (i % 12), 1 + (i % 27)),
                        String.format("city-%d", i), 100 + (i % 30))));
    }

    @AfterEach
    void cleanup() {
        var delResult = mongoTemplate.remove(new Query(), User.class);
        log.info("removed users: numRemoved={}", delResult.getDeletedCount());
    }

    @Test
    void find_Regex_MongoTemplate() {
        var collections = mongoTemplate.getCollectionNames();
        mongoTemplate.insert(new User("John", "Wick", LocalDate.of(2094, 7, 1), "Minsk", 100));
        var userFound = mongoTemplate.find(new Query(
                        where("firstName").regex("Jo.*")),
                User.class, User.COLLECTION_NAME);
        log.info("found killer: {}", userFound);
    }

    @Test
    void find_Regex_SpringDataRepository() {
        var usersFoundFunctional = userRepository.findByFirstNameRegex("Jo.*");
        var usersFoundCustomQuery = userRepository.findByFirstNameCustom("Jo.*");
        log.info("usersFoundFunctional = {}", usersFoundFunctional);
        log.info("usersFoundCustomQuery = {}", usersFoundCustomQuery);
        assertThat(usersFoundFunctional)
                .hasSize(1)
                .map(User::getFirstName)
                .containsExactly("John");
    }

    @Test
    void test_Max() {
        var users = userRepository.findByFirstNameRegex("John");
        // city == "Los Angeles"
        users.get(0).toBuilder().firstName("Jane");

        // <<<<<< other client: city = "New York"

        userRepository.save(users.get(0));
    }

    @Test
    void find_All_MongoIterator() {
        var numTotalUsersFound = mongoTemplate.stream(new Query(), User.class).stream()
                .mapToLong(u -> 0L)
                .reduce(0, (total, element) -> total + 1);
        log.info("numTotalUsersFound={}", numTotalUsersFound);
        assertThat(numTotalUsersFound)
                .isEqualTo(numGeneratedUsers + numExplicitUsers);
    }

    @Test
    void find_AndUpdate_One() {
        var johnBeforeUpdate = mongoTemplate.findOne(query(where("lastName").is("Wick")), User.class);
        assertThat(johnBeforeUpdate).isNotNull();
        var updResult = mongoTemplate.update(User.class)
                .matching(where("lastName").is("Wick"))
                .apply(update("lastName", "Doe").inc("salary", 1))
                .first();
        assertThat(updResult.getModifiedCount())
                .isOne();
        var johnAfterUpdate = mongoTemplate.findOne(query(where("id").is(johnBeforeUpdate.getId())), User.class);
        assertThat(johnAfterUpdate).isNotNull();
        assertThat(johnAfterUpdate.getLastName()).isEqualTo("Doe");
        assertThat(johnAfterUpdate.getSalary())
                .isEqualTo(johnBeforeUpdate.getSalary() + 1);
    }

    @Test
    void find_AndUpdate_Many() {
        var updResult = mongoTemplate.update(User.class)
                .matching(where("firstName").regex("firstName-generated-\\d+"))
                .apply(update("salary", 0))
                .all();
        assertThat(updResult.getModifiedCount()).isEqualTo(numGeneratedUsers);
        var generatedUserAfterUpdate = mongoTemplate.query(User.class)
                .matching(where("firstName").regex("firstName-generated-\\d+"))
                .firstValue();
        assertThat(generatedUserAfterUpdate).isNotNull();
        assertThat(generatedUserAfterUpdate.getSalary()).isZero();
    }
}
