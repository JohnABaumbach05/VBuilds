package com.vbuilds.service;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class LoginAttemptLimiterTest {
    @Test
    void blocksAfterTenFailuresButNotOtherAddresses() {
        LoginAttemptLimiter limiter = new LoginAttemptLimiter();
        for (int i = 0; i < 10; i++) limiter.recordFailure("192.0.2.1");
        assertTrue(limiter.isBlocked("192.0.2.1"));
        assertFalse(limiter.isBlocked("192.0.2.2"));
    }

    @Test
    void successfulLoginClearsFailureCounter() {
        LoginAttemptLimiter limiter = new LoginAttemptLimiter();
        for (int i = 0; i < 10; i++) limiter.recordFailure("192.0.2.1");
        limiter.clear("192.0.2.1");
        assertFalse(limiter.isBlocked("192.0.2.1"));
    }

    @Test
    void expiredFailuresDoNotBlock() {
        Instant now = Instant.parse("2026-11-18T00:00:00Z");
        LoginAttemptLimiter limiter = new LoginAttemptLimiter(Clock.fixed(now, ZoneOffset.UTC));
        for (int i = 0; i < 10; i++) limiter.recordFailure("192.0.2.1");
        assertTrue(limiter.isBlocked("192.0.2.1"));
        LoginAttemptLimiter later = new LoginAttemptLimiter(Clock.fixed(now.plusSeconds(901), ZoneOffset.UTC));
        assertFalse(later.isBlocked("192.0.2.1"));
    }
}
