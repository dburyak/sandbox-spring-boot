package com.dburyak.sandbox.sandboxspringboot.repository;

public interface UserRepositoryCustomQueries {
    Integer maxSalary();

    Integer deleteBySomething(String something);

    void updateFirstName(String userId, String newFirstName);

    void incrementNumMonths(String userId);
}
