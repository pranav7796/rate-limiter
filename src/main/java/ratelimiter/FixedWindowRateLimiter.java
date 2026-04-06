package ratelimiter;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

public class FixedWindowRateLimiter implements RateLimiterStrategy {
    private final int maxRequests;
    private final long windowSizeMillis;
    private final ConcurrentHashMap<String, WindowState> stateByKey;

    public FixedWindowRateLimiter(int maxRequests, Duration windowSize) {
        if (maxRequests <= 0) {
            throw new IllegalArgumentException("maxRequests must be > 0");
        }
        if (windowSize == null || windowSize.isZero() || windowSize.isNegative()) {
            throw new IllegalArgumentException("windowSize must be > 0");
        }
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSize.toMillis();
        this.stateByKey = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allow(String key) {
        validateKey(key);
        long now = System.currentTimeMillis();
        final boolean[] allowed = {false};

        stateByKey.compute(key, (k, currentState) -> {
            if (currentState == null || now - currentState.windowStartMillis >= windowSizeMillis) {
                allowed[0] = true;
                return new WindowState(now, 1);
            }

            if (currentState.requestCount < maxRequests) {
                currentState.requestCount++;
                allowed[0] = true;
                return currentState;
            }

            allowed[0] = false;
            return currentState;
        });

        return allowed[0];
    }

    private void validateKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key must not be null or blank");
        }
    }

    private static final class WindowState {
        private long windowStartMillis;
        private int requestCount;

        private WindowState(long windowStartMillis, int requestCount) {
            this.windowStartMillis = windowStartMillis;
            this.requestCount = requestCount;
        }
    }
}
