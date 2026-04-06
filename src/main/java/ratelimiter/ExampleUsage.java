package ratelimiter;

import java.time.Duration;

public class ExampleUsage {
    public static void main(String[] args) {
        RateLimiterStrategy fixedWindow = RateLimiterFactory.create(
                RateLimiterType.FIXED_WINDOW,
                100,
                Duration.ofMinutes(1)
        );

        RateLimiterService rateLimiterService = new RateLimiterService(fixedWindow);

        runBusinessLogic();
        callExternalIfAllowed(rateLimiterService, "user:42");

        RateLimiterStrategy slidingWindow = RateLimiterFactory.create(
                RateLimiterType.SLIDING_WINDOW,
                100,
                Duration.ofMinutes(1)
        );

        rateLimiterService.setStrategy(slidingWindow);

        runBusinessLogic();
        callExternalIfAllowed(rateLimiterService, "provider:stripe");
    }

    private static void runBusinessLogic() {
        System.out.println("Business logic executed");
    }

    private static void callExternalIfAllowed(RateLimiterService rateLimiterService, String key) {
        if (rateLimiterService.allow(key)) {
            System.out.println("External call allowed for " + key);
        } else {
            System.out.println("External call denied for " + key);
        }
    }
}
