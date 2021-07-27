package com.dburyak.sandbox.sandboxspringboot.repository;

import com.dburyak.sandbox.sandboxspringboot.MongoIntegrationTest;
import com.dburyak.sandbox.sandboxspringboot.domain.User;
import com.mongodb.client.MongoClient;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import reactor.test.StepVerifier;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.Month.AUGUST;
import static java.time.Month.JULY;
import static java.time.Month.JUNE;
import static java.time.Month.OCTOBER;
import static java.time.Month.SEPTEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Log4j2
public class MongoOpsSandboxTest extends MongoIntegrationTest {

    @Autowired
    private MongoClient mongoClient;

    private List<User> initialUsers = List.of(
            new User("john", "doe", LocalDate.of(1989, JULY, 27), "Chicago", 100, 1, null),
            new User("jane", "doe", LocalDate.of(1988, JUNE, 26), "Kyiv", 120, 5, null),
            new User("jack", "smith", LocalDate.of(1987, AUGUST, 25), "Washington", 125, 11, null),
            new User("rick", "sanchez", LocalDate.of(1986, SEPTEMBER, 24), "Madrid", 137, 17, null),
            new User("morty", "smith", LocalDate.of(1985, OCTOBER, 23), "Malaga", 150, 35, null)
    );

    private List<User> insertedUsers = Collections.synchronizedList(new ArrayList<>());

    @BeforeEach
    void initUsers() {
        var firstNames = initialUsers.stream().map(User::getFirstName).collect(Collectors.toList());
        StepVerifier.create(mongo.insertAll(initialUsers).collectList())
                .assertNext(insertedUsers -> {
                    assertThat(insertedUsers)
                            .map(User::getFirstName)
                            .containsExactlyElementsOf(firstNames);
                    this.insertedUsers.clear();
                    this.insertedUsers.addAll(insertedUsers);
                })
                .verifyComplete();
    }

    private void printAllUsersInDb() {
        StepVerifier
                .create(mongo.findAll(User.class)
                        .doOnNext(u -> log.debug("user in db: {}", u))
                        .collectList()
                )
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findUsers_BirthDateLessThan() {
        printAllUsersInDb();
        var today = LocalDate.of(2012, AUGUST, 26);
        log.debug("today: {}", today);
        var twentyFiveYearsAgo = today.minusYears(25);
        log.debug("25 years ago: {}", twentyFiveYearsAgo);
        var findUsersOlderThan25 = mongo.find(query(where("birthDate").lt(twentyFiveYearsAgo)), User.class)
                .doOnNext(u -> log.debug("user older 25: {}", u));
        StepVerifier.create(findUsersOlderThan25.collectList())
                .assertNext(usersOlderThan25 -> assertThat(usersOlderThan25)
                        .map(User::getFirstName)
                        .containsExactlyInAnyOrder("jack", "rick", "morty")
                )
                .verifyComplete();
    }

    @Test
    void findUsers_SalaryGreaterThan() {
        printAllUsersInDb();
        var findUsersSalaryGreaterThan125 = mongo.find(query(where("salary").gt(125)), User.class)
                .doOnNext(u -> log.debug("user with salary >125 : {}", u));
        StepVerifier.create(findUsersSalaryGreaterThan125.collectList())
                .assertNext(usersSalaryGreaterThan125 -> assertThat(usersSalaryGreaterThan125)
                        .map(User::getFirstName)
                        .containsExactlyInAnyOrder("rick", "morty")
                )
                .verifyComplete();
    }

    @Test
    void updateUsers_SetCityWhereLastName() {
        printAllUsersInDb();
        var setCityWhereLastName = mongo.update(User.class)
                .matching(query(where("lastName").is("doe")))
                .apply(Update.update("city", "Barcelona"))
                .all();
        StepVerifier.create(setCityWhereLastName)
                .assertNext(updRes -> {
                    assertThat(updRes)
                            .extracting(UpdateResult::getMatchedCount)
                            .isEqualTo(2L);
                    assertThat(updRes)
                            .extracting(UpdateResult::getModifiedCount)
                            .isEqualTo(2L);
                })
                .verifyComplete();
        log.debug("AFTER MODIFICATION:");
        printAllUsersInDb();
        var findAllWithLastNameDoe = mongo.find(query(where("lastName").is("doe")), User.class);
        StepVerifier.create(findAllWithLastNameDoe.collectList())
                .assertNext(does -> assertThat(does)
                        .map(User::getCity)
                        .containsOnly("Barcelona")
                )
                .verifyComplete();
    }

    @Test
    void removeUsers_WhereSalaryGreaterThan() {
        printAllUsersInDb();
        var removeUsersSalaryGreaterThan119 = mongo.remove(query(where("salary").gt(119)), User.class)
                .doOnNext(u -> log.debug("removed users with salary >119: numDeleted={}", u.getDeletedCount()));
        StepVerifier.create(removeUsersSalaryGreaterThan119)
                .assertNext(delResult -> assertThat(delResult)
                        .extracting(DeleteResult::getDeletedCount)
                        .isEqualTo(4L)
                )
                .verifyComplete();
        log.debug("AFTER REMOVAL:");
        printAllUsersInDb();
        StepVerifier.create(mongo.findAll(User.class).collectList())
                .assertNext(allUsers -> assertThat(allUsers)
                        .map(User::getFirstName)
                        .containsExactly("john")
                )
                .verifyComplete();
    }

    @Test
    void listDatabases() {
        var mongo = new MongoTemplate(mongoClient, "admin");
        var databasesInfoJson  = mongo.executeCommand(Document.parse("{\"listDatabases\": 1}")).toJson();
        log.info("databases info json:\n{}", databasesInfoJson);
    }

    @Test
    void objectId_Features() {
        var id1 = new ObjectId();
        var id1Ts = id1.getTimestamp();
        var idLow = ObjectId.getSmallestWithDate(Date.from(Instant.now().minusSeconds(10)));
        var idHigh = ObjectId.getSmallestWithDate(Date.from(Instant.now().plusSeconds(10)));
        assertThat(idLow).isLessThan(idHigh);
        assertThat(idLow.getTimestamp()).isLessThan(idHigh.getTimestamp());
        var findAllUsersCreatedBetweenUnixTimestamps = mongo.find(query(
                where("_id").gt(idLow).lt(idHigh)), User.class);
        var allUsersFirstNames = initialUsers.stream()
                .map(User::getFirstName)
                .collect(Collectors.toList());
        StepVerifier.create(findAllUsersCreatedBetweenUnixTimestamps.collectList())
                .assertNext(usersCreatedInRange -> assertThat(usersCreatedInRange)
                        .map(User::getFirstName)
                        .containsExactlyInAnyOrderElementsOf(allUsersFirstNames)
                )
                .verifyComplete();

        // range in past
        idLow = ObjectId.getSmallestWithDate(Date.from(Instant.now().minusSeconds(20)));
        idHigh = ObjectId.getSmallestWithDate(Date.from(Instant.now().minusSeconds(10)));
        assertThat(idLow.getTimestamp()).isLessThan(idHigh.getTimestamp());
        findAllUsersCreatedBetweenUnixTimestamps = mongo.find(query(
                where("_id").gt(idLow).lt(idHigh)), User.class);
        StepVerifier.create(findAllUsersCreatedBetweenUnixTimestamps.collectList())
                .assertNext(usersCreatedInRange -> assertThat(usersCreatedInRange)
                        .isEmpty()
                )
                .verifyComplete();
    }
}
