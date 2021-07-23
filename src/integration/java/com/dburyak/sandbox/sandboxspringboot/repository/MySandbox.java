package com.dburyak.sandbox.sandboxspringboot.repository;

import com.dburyak.sandbox.sandboxspringboot.MongoIntegrationTest;
import com.dburyak.sandbox.sandboxspringboot.domain.User;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
public class MySandbox extends MongoIntegrationTest {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setup() {
        mongoTemplate.insert(new User("John", "Wick", LocalDate.of(2094, 7, 1), "Minsk", 100));
    }

    @Test
    void find_Regex_MongoTemplate() {
        var collections = mongoTemplate.getCollectionNames();
        mongoTemplate.insert(new User("John", "Wick", LocalDate.of(2094, 7, 1), "Minsk", 100));
        var userFound = mongoTemplate.find(new Query(
                Criteria.where("firstName").regex("Jo.*")),
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
}
