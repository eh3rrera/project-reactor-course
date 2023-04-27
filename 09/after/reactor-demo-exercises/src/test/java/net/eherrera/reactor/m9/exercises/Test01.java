package net.eherrera.reactor.m9.exercises;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.time.Duration;

public class Test01 {
    @Test
    void test() {
        // Create a Mono that emits an integer value after 3 seconds
        Mono<Integer> integerMono = Mono.just(1)
                .delayElement(Duration.ofSeconds(3));

        // Use StepVerifier to create a test scenario with the expectTimeout method
        StepVerifier verifier = StepVerifier.create(integerMono)
                .expectTimeout(Duration.ofSeconds(2));

        // Run the test scenario and verify the results
        verifier.verify();
    }
}
