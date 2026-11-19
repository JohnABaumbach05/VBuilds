package com.vbuilds.service;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Basic per-IP failed-login limiter for a single application instance.
 * Use a shared store (for example Redis) when running multiple instances.
 */
@Component
public class LoginAttemptLimiter {
    private static final int MAX_FAILURES = 10;
    private static final Duration WINDOW = Duration.ofMinutes(15);
    private static final int MAX_TRACKED_ADDRESSES = 100_000;

    private final Map<String, Attempt> attempts = new HashMap<>();
    private final Clock clock;

    public LoginAttemptLimiter() {
        this(Clock.systemUTC());
    }

    LoginAttemptLimiter(Clock clock) {
        this.clock = clock;
    }

    public synchronized boolean isBlocked(String address) {
        Instant now = clock.instant();
        Attempt attempt = attempts.get(address);
        if (attempt == null) return false;
        if (!now.isBefore(attempt.started.plus(WINDOW))) {
            attempts.remove(address);
            return false;
        }
        return attempt.failures >= MAX_FAILURES;
    }

    public synchronized void recordFailure(String address) {
        Instant now = clock.instant();
        Attempt attempt = attempts.get(address);
        if (attempt == null || !now.isBefore(attempt.started.plus(WINDOW))) {
            if (attempts.size() >= MAX_TRACKED_ADDRESSES) {
                Iterator<Map.Entry<String, Attempt>> iterator = attempts.entrySet().iterator();
                while (iterator.hasNext()) {
                    if (!now.isBefore(iterator.next().getValue().started.plus(WINDOW))) iterator.remove();
                }
                // Bound memory even if many unique addresses arrive inside one window.
                if (attempts.size() >= MAX_TRACKED_ADDRESSES) attempts.clear();
            }
            attempts.put(address, new Attempt(now, 1));
        } else {
            attempt.failures++;
        }
    }

    public synchronized void clear(String address) {
        attempts.remove(address);
    }

    private static final class Attempt {
        final Instant started;
        int failures;

        Attempt(Instant started, int failures) {
            this.started = started;
            this.failures = failures;
        }
    }
}
