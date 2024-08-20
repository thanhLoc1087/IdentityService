package com.loc.identity_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loc.identity_service.entity.InvalidatedToken;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {}
