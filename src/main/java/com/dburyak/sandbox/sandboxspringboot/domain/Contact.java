package com.dburyak.sandbox.sandboxspringboot.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(of={"type", "value"})
@AllArgsConstructor
@NoArgsConstructor
public class Contact {
    private String type;
    private String value;
}
