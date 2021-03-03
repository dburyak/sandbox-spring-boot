package com.dburyak.sandbox.sandboxspringboot.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

import static com.dburyak.sandbox.sandboxspringboot.domain.User.COLLECTION_NAME;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@RequiredArgsConstructor(onConstructor_ = {@PersistenceConstructor})
@SuperBuilder(toBuilder = true)
@Document(collection = COLLECTION_NAME)
public class User extends PersistentVersionedEntity {
    public static final String COLLECTION_NAME = "users";

    private final String firstName;
    private final String lastName;
    private final LocalDate birthDate;
    private final String city;
    private final int salary;
}
