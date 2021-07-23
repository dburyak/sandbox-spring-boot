package com.dburyak.sandbox.sandboxspringboot.repository;

import com.dburyak.sandbox.sandboxspringboot.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustomQueries {
    List<User> findByFirstNameRegex(String firstNameRegex);

    @Query("{ 'firstName': { $regex: ?0 } }")
    List<User> findByFirstNameCustom(String firstNameRegex);
}
