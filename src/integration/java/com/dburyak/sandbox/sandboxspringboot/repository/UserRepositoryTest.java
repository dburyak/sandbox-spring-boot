package com.dburyak.sandbox.sandboxspringboot.repository;

import com.dburyak.sandbox.sandboxspringboot.MongoIntegrationTest;
import com.dburyak.sandbox.sandboxspringboot.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.Month.JULY;
import static org.assertj.core.api.Assertions.assertThat;

public class UserRepositoryTest extends MongoIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void insertUsers() {
        var users = List.of(
                new User("john", "doe", LocalDate.of(1989, JULY, 27)),
                new User("jane", "doe", LocalDate.of(1989, JULY, 26)),
                new User("jack", "smith", LocalDate.of(1989, JULY, 25)),
                new User("rick", "sanchez", LocalDate.of(1989, JULY, 24)),
                new User("morty", "smith", LocalDate.of(1989, JULY, 23))
        );
        var firstNames = users.stream().map(User::getFirstName).collect(Collectors.toList());
        var savedUsers = userRepository.saveAll(users).cache();
        StepVerifier.create(savedUsers.collectList())
                .assertNext(s -> assertThat(s)
                        .map(User::getFirstName)
                        .containsExactlyElementsOf(firstNames))
                .verifyComplete();
    }
}
