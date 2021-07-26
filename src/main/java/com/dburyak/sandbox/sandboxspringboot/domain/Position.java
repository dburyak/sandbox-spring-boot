package com.dburyak.sandbox.sandboxspringboot.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode
@AllArgsConstructor
public class Position {
    private String name;
    private ObjectId[] workers;
}
