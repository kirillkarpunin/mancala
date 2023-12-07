package com.bol.security.jwt.service;


public interface JwtService {

    String generateToken(String subject);
}

