package fr.tawane.demo;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.stream.IntStream.range;

public class RateLimiterPocApplication {

    public static void main(String[] args) throws InterruptedException {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofMillis(1000))
                .limitForPeriod(350)
                .build();
        RateLimiterRegistry rateLimiterRegistry = RateLimiterRegistry.of(config);
        RateLimiter customRateLimiter = rateLimiterRegistry.rateLimiter("ratelimiter");

        int numberOfTasks = 1_000;
        var start = Instant.now();
        try (ExecutorService executor = Executors.newFixedThreadPool(1000)) {
            range(0, numberOfTasks)
                    .mapToObj(i -> (Runnable) RateLimiterPocApplication::someBlockingIOs)
                    .map(runnable -> RateLimiter.decorateRunnable(customRateLimiter, runnable))
                    .forEach(executor::submit);
            executor.shutdown();
            System.out.println("All tasks have completed successfully : " + executor.awaitTermination(1, MINUTES));
        }
        var end = Instant.now();
        long durationInMillis = end.toEpochMilli() - start.toEpochMilli();
        System.out.println("durationInMillis : " + durationInMillis);
        System.out.println("rate : " + (double) numberOfTasks / durationInMillis * 1000 + " tasks/second");
    }

    private static void someBlockingIOs() {
        try {
            sleep(550); // Some blocking IOs
            System.out.println("Hello world");
        } catch (InterruptedException ignored) {
        }
    }
}
