package com.dburyak.sandbox.sandboxspringboot.config;

import com.dburyak.sandbox.sandboxspringboot.domain.AuditUser;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing(auditorAwareRef = "auditorProvider")
@Log4j2
class AuditConfig {

    @Bean
    public AuditorAware<AuditUser> auditorProvider() {
        return new SpringSecurityAuditorAware();
    }
}
