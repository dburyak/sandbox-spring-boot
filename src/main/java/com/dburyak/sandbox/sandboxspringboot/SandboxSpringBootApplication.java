package com.dburyak.sandbox.sandboxspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.tools.agent.ReactorDebugAgent;

@SpringBootApplication
public class SandboxSpringBootApplication {

    public static void main(String[] args) {
        ReactorDebugAgent.init();
        ReactorDebugAgent.processExistingClasses();
        SpringApplication.run(SandboxSpringBootApplication.class, args);
    }

}
