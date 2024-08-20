package com.loc.identity_service.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.loc.identity_service.validator.DateOfBirthConstraint;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level=AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min=4, message="USERNAME_INVALID")
    String username;

    @Size(min=8, message="PASSWORD_INVALID")
    String password;
    String firstName;
    String lastName;

    List<String> roles;

    @DateOfBirthConstraint(min = 6, message="INVALID_DOB")
    LocalDate dob;
}
