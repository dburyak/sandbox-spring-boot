package com.dburyak.sandbox.sandboxspringboot.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@RequiredArgsConstructor(onConstructor_ = {@PersistenceConstructor})
@SuperBuilder(toBuilder = true)
@Document(collation = "users")
public class User extends PersistentVersionedEntity {
}
