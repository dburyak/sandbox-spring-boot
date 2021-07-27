package com.dburyak.sandbox.sandboxspringboot.repository;

import com.dburyak.sandbox.sandboxspringboot.MongoIntegrationTest;
import com.dburyak.sandbox.sandboxspringboot.domain.Contact;
import com.dburyak.sandbox.sandboxspringboot.domain.User;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.test.StepVerifier;

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
        System.err.println("mongo url: "+MONGO_CONTAINER.getReplicaSetUrl());
        mongoTemplate.insert(new User("John", "Wick", LocalDate.of(2094, 7, 1), "Minsk", 100, 24, new Contact[]{new Contact().builder().type("phone").value("2464765545747").build()}));
        mongoTemplate.insert(new User("Naruto", "Uzumaki",LocalDate.of(1997,2,12),"Konoha", 120, 17,null));
        mongoTemplate.insert(new User("Han", "Solo",LocalDate.of(1964,2,12),"Nabu", 30, 20,null));
        mongoTemplate.insert(new User("Luke", "Skywalker",LocalDate.of(1964,7,12),"Nabu", 30, 21,null));
        mongoTemplate.insert(new User("Chewie", null, LocalDate.of(1964,6,12),"Nabu", 135, 20,null));
        IntStream.iterate(1, i -> i + 1)
                .limit(numGeneratedUsers)
                .forEach(i -> mongoTemplate.insert(new User(String.format("firstName-generated-%d", i),
                        String.format("lastName-generated-%d", i),
                        LocalDate.of(1980 + (i % 10), 1 + (i % 12), 1 + (i % 27)),
                        String.format("city-%d", i), 100 + (i % 30),1+(i%5), null)));
    }

    @AfterEach
    void cleanup() {
        var delResult = mongoTemplate.remove(new Query(), User.class);
        log.info("removed users: numRemoved={}", delResult.getDeletedCount());
    }

    @Test
    void find_Regex_MongoTemplate() {
        var collections = mongoTemplate.getCollectionNames();
       // mongoTemplate.insert(new User("John", "Wick", LocalDate.of(2094, 7, 1), "Minsk", 100, 24, ));
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
    void test_Max() throws InterruptedException {
        //Thread.sleep(89999);
        var users = userRepository.findByFirstNameRegex("John");
        // city == "Los Angeles"
        users.get(0).toBuilder().firstName("Jane");

        // <<<<<< other client: city = "New York"

        log.info(users.get(0));

        userRepository.save(users.get(0));
        userRepository.findAll().forEach(System.err::println);

        userRepository.updateFirstName(users.get(0).getId(),"Johnathan");


    }

    @Test
    void find_by_Lowest_Salary(){
        var findUsersWithSalaryLessOrEquals100 = mongoTemplate.find(query(where("salary").lte(100)), User.class, User.COLLECTION_NAME);
        assertThat(userRepository.findByLowSalary(100)).
                isEqualTo(findUsersWithSalaryLessOrEquals100).
                map(User::getFirstName).
                containsExactlyInAnyOrder("John","Han","Luke");
    }

   @Test
    void findUsers_BirthDayBetween() {

        var findUsersBornInJuneJuly1964 = mongoTemplate.find(
                new Query().addCriteria(
                        new Criteria().andOperator(
                                Criteria.where("birthDate").gt(LocalDate.of(1964,5,31)),
                                Criteria.where("birthDate").lt(LocalDate.of(1964,8,1))
                        )), User.class);

        assertThat(userRepository.findByBirthBetweenTwoDates(LocalDate.of(1964,5,31), LocalDate.of(1964,8,1))).isEqualTo(findUsersBornInJuneJuly1964)
                        .map(User::getFirstName)
                        .containsExactlyInAnyOrder("Chewie","Luke");
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
