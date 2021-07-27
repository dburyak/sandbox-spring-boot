package com.dburyak.sandbox.sandboxspringboot.config;

import com.dburyak.sandbox.sandboxspringboot.domain.AuditUser;
import com.dburyak.sandbox.sandboxspringboot.domain.User;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class SpringSecurityAuditorAware implements AuditorAware<AuditUser> {


    @Override
    public Optional<AuditUser> getCurrentAuditor() {

        return Optional.ofNullable(/*SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map(User.class::cast*/
        new AuditUser("test","testovich",null,"Testovgrad",1,300));
    }
}
