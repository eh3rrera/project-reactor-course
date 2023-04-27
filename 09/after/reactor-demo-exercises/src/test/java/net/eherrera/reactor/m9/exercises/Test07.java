package net.eherrera.reactor.m9.exercises;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

public class Test07 {
    @Test
    void test() {
        // Create a cold TestPublisher instance for Integer values
        TestPublisher<Integer> testPublisher = TestPublisher.createCold();

        // Use the TestPublisher to emit a sequence from 1 to 3 using the next method
        testPublisher.next(1, 2, 3);

        // Create a Flux from the TestPublisher
        Flux<Integer> integerFlux = testPublisher.flux();

        // Use the StepVerifier to create a test scenario for the Flux
        StepVerifier verifier = StepVerifier.create(integerFlux)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .then(() -> testPublisher.complete())
                .expectComplete();

        // Complete the test scenario and verify the results
        verifier.verify();
    }
}
