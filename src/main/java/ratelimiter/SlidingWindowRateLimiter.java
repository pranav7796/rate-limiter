package ratelimiter;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

public class SlidingWindowRateLimiter implements RateLimiterStrategy {
    private final int maxRequests;
    private final long windowSizeMillis;
    private final ConcurrentHashMap<String, RequestHistory> historyByKey;

    public SlidingWindowRateLimiter(int maxRequests, Duration windowSize) {
        if (maxRequests <= 0) {
            throw new IllegalArgumentException("maxRequests must be > 0");
        }
        if (windowSize == null || windowSize.isZero() || windowSize.isNegative()) {
            throw new IllegalArgumentException("windowSize must be > 0");
        }
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSize.toMillis();
        this.historyByKey = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allow(String key) {
        validateKey(key);
        long now = System.currentTimeMillis();
        final boolean[] allowed = {false};

        historyByKey.compute(key, (k, currentHistory) -> {
            RequestHistory history = currentHistory == null ? new RequestHistory() : currentHistory;
            long windowStart = now - windowSizeMillis;

            while (!history.timestamps.isEmpty() && history.timestamps.peekFirst() <= windowStart) {
                history.timestamps.pollFirst();
            }

            if (history.timestamps.size() < maxRequests) {
                history.timestamps.addLast(now);
                allowed[0] = true;
            } else {
                allowed[0] = false;
            }

            return history;
        });

        return allowed[0];
    }

    private void validateKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key must not be null or blank");
        }
    }

    private static final class RequestHistory {
        private final Deque<Long> timestamps = new ArrayDeque<>();
    }
}
