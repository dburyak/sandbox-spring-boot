package com.dburyak.sandbox.sandboxspringboot.repository;

import com.dburyak.sandbox.sandboxspringboot.MongoIntegrationTest;
import com.dburyak.sandbox.sandboxspringboot.domain.User;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;

@Log4j2
public class MySandbox extends MongoIntegrationTest {

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    void find_Regex() {
        var collections = mongoTemplate.getCollectionNames();
        mongoTemplate.insert(new User("John", "Wick", LocalDate.of(2094, 7, 1), "Minsk", 100));
        var userFound = mongoTemplate.find(new Query(
                Criteria.where("firstName").regex("Jo.*")),
                User.class, User.COLLECTION_NAME);
        log.info("found killer: {}", userFound);
    }
}
