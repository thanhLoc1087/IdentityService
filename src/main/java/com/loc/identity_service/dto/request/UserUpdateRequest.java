package com.loc.identity_service.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.loc.identity_service.validator.DateOfBirthConstraint;

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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String password;
    String firstName;
    String lastName;
    List<String> roles;

    @DateOfBirthConstraint(min = 6, message = "INVALID_DOB")
    LocalDate dob;
}
