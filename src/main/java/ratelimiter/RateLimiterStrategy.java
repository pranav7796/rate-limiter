package ratelimiter;

public interface RateLimiterStrategy {
    boolean allow(String key);
}
