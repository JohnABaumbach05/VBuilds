package com.vbuilds.service;

import org.junit.jupiter.api.Test;

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

}
