package com.loc.identity_service.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.loc.identity_service.dto.request.AuthenticationRequest;
import com.loc.identity_service.dto.request.IntrospectRequest;
import com.loc.identity_service.dto.request.LogoutRequest;
import com.loc.identity_service.dto.request.RefreshRequest;
import com.loc.identity_service.dto.response.AuthenticationResponse;
import com.loc.identity_service.dto.response.IntrospectResponse;
import com.loc.identity_service.entity.InvalidatedToken;
import com.loc.identity_service.entity.User;
import com.loc.identity_service.exception.AppException;
import com.loc.identity_service.exception.ErrorCode;
import com.loc.identity_service.repository.InvalidatedTokenRepository;
import com.loc.identity_service.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    
    @NonFinal
    @Value("${jwt.signer-key}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    public IntrospectResponse introspect(IntrospectRequest request)
        throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder()
            .valid(isValid)
            .build();
    }
    
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository
            .findByUsername(request.getUsername())
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = 
            passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        
        var token = generateToken(user);

        return AuthenticationResponse.builder()
            .authenticated(authenticated)
            .token(token)
            .build();
    }

    public void logout(LogoutRequest request) 
        throws ParseException, JOSEException {
        try {
            var signedToken = verifyToken(request.getToken(), true);
            String jwtId = signedToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signedToken.getJWTClaimsSet().getExpirationTime();
    
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jwtId)
                .expiryDate(expiryTime)
                .build();
            
            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException e) {
            log.info("Token already expired.");
        }

    }

    public AuthenticationResponse refreshToken(RefreshRequest request)
        throws JOSEException, ParseException {
        var signedJwt = verifyToken(request.getToken(), true);

        var jwtId = signedJwt.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJwt.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
            .id(jwtId)
            .expiryDate(expiryTime)
            .build();
        
        invalidatedTokenRepository.save(invalidatedToken);

        var username = signedJwt.getJWTClaimsSet().getSubject();
        var user = userRepository.findByUsername(username).orElseThrow(
            () -> new AppException(ErrorCode.UNAUTHENTICATED)
        );
        
        var token = generateToken(user);

        return AuthenticationResponse.builder()
            .authenticated(true)
            .token(token)
            .build();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh)
        throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = isRefresh 
            ? new Date (signedJWT
                .getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                .toEpochMilli()) 
            : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && (expiryTime.after(new Date()))))
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        
        if (invalidatedTokenRepository
            .existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        
        return signedJWT;
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
            .subject(user.getUsername())
            .issuer("thanhlocne246")
            .issueTime(new Date())
            .expirationTime(new Date(
                Instant.now()
                    .plus(VALID_DURATION, ChronoUnit.SECONDS)
                    .toEpochMilli()
            ))
            .jwtID(UUID.randomUUID().toString())
            .claim("scope", buildScope(user))
            .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token.");
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                // Ở SecConfig hoặc các Service, dùng hasRole để check role
                stringJoiner.add("ROLE_" + role.getName());
                // Dùng hasAuthority để check permission
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            });

        return stringJoiner.toString();
    }
}
