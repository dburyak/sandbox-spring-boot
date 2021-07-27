package com.dburyak.sandbox.sandboxspringboot.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

import static com.dburyak.sandbox.sandboxspringboot.domain.User.COLLECTION_NAME;

@Data
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AuditUser{

    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String city;
    private int salary;
    private int numMonAtWork;
}
