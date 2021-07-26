package com.dburyak.sandbox.sandboxspringboot.repository;

import com.dburyak.sandbox.sandboxspringboot.domain.Position;
import com.dburyak.sandbox.sandboxspringboot.domain.User;

import java.util.List;

public interface UserRepositoryCustomQueries {
    Integer maxSalary();

    void updateFirstName(String userId, String newFirstName);

    Long deleteByLstName(String lastName);

    void incrementNumMonths(String userId);

    List<User> userWithContactInfo();

    List<User> workersByPosition(Position position);
}
