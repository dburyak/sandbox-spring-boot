package com.dburyak.sandbox.sandboxspringboot.repository;

import com.dburyak.sandbox.sandboxspringboot.domain.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public class UserRepositoryCustomQueriesImpl implements UserRepositoryCustomQueries {
    private MongoTemplate mongoTemplate;

    public UserRepositoryCustomQueriesImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Integer maxSalary() {
        return 42;
    }

    @Override
    public void updateFirstName(String userId, String newFirstName) {
        var upd = new Update();
        upd.set("firstName", newFirstName);
        mongoTemplate.updateFirst(new Query(where("id").is(userId)), upd, User.class);
    }

    @Override
    public Integer deleteBySomething(String something) {
        return 42;
    }

    @Override
    public void incrementNumMonths(String userId) {
        var update = new Update();
        update.inc("numMonAtWork");
        mongoTemplate.updateFirst(new Query(where("id").is(userId)), update, User.class);
    }
}
