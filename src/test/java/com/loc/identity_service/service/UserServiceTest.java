package com.loc.identity_service.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import com.loc.identity_service.dto.request.UserCreationRequest;
import com.loc.identity_service.entity.User;
import com.loc.identity_service.exception.AppException;
import com.loc.identity_service.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private UserCreationRequest request;
    private User user;
    private LocalDate dob;

    @BeforeEach
    private void innitData() {
        dob = LocalDate.of(2003, 12, 5);

        request = UserCreationRequest.builder()
            .username("johndoe")
            .firstName("John")
            .lastName("Doe")
            .password("12345678")
            .dob(dob)
            .build();
        
        user = User.builder()
            .id("jk31hkj3h5")
            .username("johndoe")
            .firstName("John")
            .lastName("Doe")
            .dob(dob)
            .build();
    }

    @Test
    void createUser_validRequest_success() {
        // GIVEN
        Mockito.when(userRepository.existsByUsername(anyString()))
            .thenReturn(false);
        Mockito.when(userRepository.save(any()))
            .thenReturn(user);

        // WHEN
        var response = userService.createUser(request);

        // THEN
        Assertions.assertThat(response.getId()).isEqualTo("jk31hkj3h5");
        Assertions.assertThat(response.getUsername()).isEqualTo("johndoe");
    }
    
    @Test
    void createUser_userExists_fail() {
        // GIVEN
        Mockito.when(userRepository.existsByUsername(anyString()))
            .thenReturn(true);

        // WHEN
        var exception = assertThrows(AppException.class, 
            () -> userService.createUser(request));
        
        Assertions.assertThat(exception.getErrorCode().getCode())
            .isEqualTo(1002);
    }
}
