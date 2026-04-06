package ratelimiter;

public class RateLimiterService {
    private volatile RateLimiterStrategy strategy;

    public RateLimiterService(RateLimiterStrategy strategy) {
        setStrategy(strategy);
    }

    public boolean allow(String key) {
        return strategy.allow(key);
    }

    public void setStrategy(RateLimiterStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("strategy must not be null");
        }
        this.strategy = strategy;
    }
}
