package com.loc.identity_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.loc.identity_service.dto.request.AuthenticationRequest;
import com.loc.identity_service.dto.response.ApiResponse;
import com.loc.identity_service.dto.response.AuthenticationResponse;
import com.loc.identity_service.service.AuthenticationService;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        boolean result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
            .result(AuthenticationResponse.builder()
                .authenticated(result)
                .build()
            ).build();
    }
    
    
}
