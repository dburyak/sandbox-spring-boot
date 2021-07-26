package com.dburyak.sandbox.sandboxspringboot.repository;

import com.dburyak.sandbox.sandboxspringboot.domain.Position;
import com.dburyak.sandbox.sandboxspringboot.domain.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public class UserRepositoryCustomQueriesImpl implements UserRepositoryCustomQueries {
    private MongoTemplate mongoTemplate;

    public UserRepositoryCustomQueriesImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Integer maxSalary() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("salary").
                        max("salary").
                        as("maxSalary"),
                Aggregation.project("maxSalary"));
        var aggregationResults = mongoTemplate.aggregate(aggregation, User.COLLECTION_NAME, Integer.class);
        var maxSalary = aggregationResults.getMappedResults().get(0);
        return maxSalary;
    }

    @Override
    public void updateFirstName(String userId, String newFirstName) {
        var upd = new Update();
        upd.set("firstName", newFirstName);
        mongoTemplate.updateFirst(new Query(where("id").is(userId)), upd, User.class);
    }

    /**
     * @param lastName
     * @return number of deleted users
     * <p>
     * old method: Integer deleteBySomething(String something);
     */
    @Override
    public Long deleteByLstName(String lastName) {
        return mongoTemplate.remove(new Query(where("lastName").is(lastName)), User.COLLECTION_NAME).getDeletedCount();
    }

    @Override
    public void incrementNumMonths(String userId) {
        var update = new Update();
        update.inc("numMonAtWork");
        mongoTemplate.updateFirst(new Query(where("id").is(userId)), update, User.class);
    }

    @Override
    public List<User> userWithContactInfo() {
        /*
        )*/
        return null;
    }

    @Override
    public List<User> workersByPosition(Position position) {
        /*Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup()*/
        return null;
    }
}
