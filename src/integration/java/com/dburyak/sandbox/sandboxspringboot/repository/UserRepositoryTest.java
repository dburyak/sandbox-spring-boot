package com.dburyak.sandbox.sandboxspringboot.repository;

import com.dburyak.sandbox.sandboxspringboot.MongoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

public class UserRepositoryTest extends MongoIntegrationTest {

    @Autowired
    private UserRepository userRepository;

}
