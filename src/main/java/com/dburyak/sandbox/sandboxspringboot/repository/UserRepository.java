package com.dburyak.sandbox.sandboxspringboot.repository;

import com.dburyak.sandbox.sandboxspringboot.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustomQueries {
    List<User> findByFirstNameRegex(String firstNameRegex);

    @Query("{ 'firstName': { $regex: ?0 } }")
    List<User> findByFirstNameCustom(String firstNameRegex);

    List<User> findByBirthDateBetween(LocalDate from, LocalDate to);

    @Query("{'birthDate':{'$gt':?0, '$lt':?1}}")
    List<User> findByBirthBetweenTwoDates(LocalDate from, LocalDate to);

    List<User> findBySalaryLessThan(int salary);

    @Query("{'salary':{'$lte':?0}}")
    List<User> findByLowSalary(int salary);

    @Query("{'lastName':{'$exists':?0}}")
    List<User> findByExistsLastNames(boolean isExists);

    List<User> findByLastNameExists(boolean exists);
}
