package com.dburyak.sandbox.sandboxspringboot.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class PersistentVersionedEntity {

    @Id
    private String id;

    @Version
    private long version;

    @CreatedBy
    private AuditUser creator;

    @LastModifiedBy
    private AuditUser changer;

    @CreatedDate
    private LocalDateTime creationTime;

    @LastModifiedDate
    private LocalDateTime lastModificationTime;
}
