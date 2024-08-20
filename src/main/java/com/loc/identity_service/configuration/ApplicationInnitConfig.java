package com.loc.identity_service.configuration;

import java.util.HashSet;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.loc.identity_service.constant.PredefinedRoles;
import com.loc.identity_service.entity.Role;
import com.loc.identity_service.entity.User;
import com.loc.identity_service.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
@ConditionalOnProperty(
    prefix = "spring",
    value = "datasource.driverClassName",
    havingValue = "org.postgresql.Driver"
)
@Slf4j
public class ApplicationInnitConfig {

    PasswordEncoder passwordEncoder;
    
    @NonFinal
    static final String ADMIN_USER_NAME = "admin";

    @NonFinal
    static final String ADMIN_PASSWORD = "admin";

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                var roles = new HashSet<Role>();
                roles.add(PredefinedRoles.ADMIN);

                User admin = User.builder()
                    .username(ADMIN_USER_NAME)
                    .password(passwordEncoder.encode(ADMIN_PASSWORD))
                    .roles(roles)
                    .build();

                userRepository.save(admin);
                log.warn("Admin user has been created with default password: 'admin'. Please change it for security measures.");
            }
        };
    }
}
