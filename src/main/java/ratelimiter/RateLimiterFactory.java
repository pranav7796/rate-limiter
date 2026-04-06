package ratelimiter;

import java.time.Duration;

public final class RateLimiterFactory {
    private RateLimiterFactory() {
    }

    public static RateLimiterStrategy create(RateLimiterType type, int maxRequests, Duration windowSize) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }

        switch (type) {
            case FIXED_WINDOW:
                return new FixedWindowRateLimiter(maxRequests, windowSize);
            case SLIDING_WINDOW:
                return new SlidingWindowRateLimiter(maxRequests, windowSize);
            default:
                throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }
}
