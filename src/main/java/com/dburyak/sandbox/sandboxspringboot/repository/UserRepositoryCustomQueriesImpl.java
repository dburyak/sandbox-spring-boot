package com.dburyak.sandbox.sandboxspringboot.repository;

import org.springframework.data.mongodb.core.MongoTemplate;

public class UserRepositoryCustomQueriesImpl implements UserRepositoryCustomQueries {
    private MongoTemplate mongoTemplate;

    public UserRepositoryCustomQueriesImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Integer maxSalary() {
        return 42;
    }
}
