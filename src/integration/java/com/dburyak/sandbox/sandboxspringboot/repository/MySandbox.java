package com.dburyak.sandbox.sandboxspringboot.repository;

import com.dburyak.sandbox.sandboxspringboot.MongoIntegrationTest;
import com.dburyak.sandbox.sandboxspringboot.domain.Contact;
import com.dburyak.sandbox.sandboxspringboot.domain.User;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Log4j2
public class MySandbox extends MongoIntegrationTest {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setup() {
        mongoTemplate.insert(new User("John", "Wick", LocalDate.of(2094, 7, 1), "Minsk", 100, 24, new Contact[]{new Contact().builder().type("phone").value("2464765545747").build()}));
        mongoTemplate.insert(new User("Naruto", "Uzumaki",LocalDate.of(1997,2,12),"Konoha", 120, 17,null));
        mongoTemplate.insert(new User("Han", "Solo",LocalDate.of(1964,2,12),"Nabu", 30, 20,null));
        mongoTemplate.insert(new User("Luke", "Skywalker",LocalDate.of(1964,7,12),"Nabu", 30, 21,null));
        mongoTemplate.insert(new User("Chewie", null, LocalDate.of(1964,6,12),"Nabu", 135, 20,null));
    }

    @Test
    void find_Regex_MongoTemplate() {
        var collections = mongoTemplate.getCollectionNames();
       // mongoTemplate.insert(new User("John", "Wick", LocalDate.of(2094, 7, 1), "Minsk", 100, 24, ));
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

}
