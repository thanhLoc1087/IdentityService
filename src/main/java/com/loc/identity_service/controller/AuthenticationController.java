package com.loc.identity_service.controller;

import java.text.ParseException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.loc.identity_service.dto.request.AuthenticationRequest;
import com.loc.identity_service.dto.request.IntrospectRequest;
import com.loc.identity_service.dto.request.LogoutRequest;
import com.loc.identity_service.dto.request.RefreshRequest;
import com.loc.identity_service.dto.response.ApiResponse;
import com.loc.identity_service.dto.response.AuthenticationResponse;
import com.loc.identity_service.dto.response.IntrospectResponse;
import com.loc.identity_service.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
            .result(result)
            .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request)
        throws JOSEException, ParseException {
        IntrospectResponse result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
            .result(result)
            .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> refresh(@RequestBody RefreshRequest request)
        throws JOSEException, ParseException {
        AuthenticationResponse result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
            .result(result)
            .build();
    }
    
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest request)
    throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder()
            .build();
    }
    
}
