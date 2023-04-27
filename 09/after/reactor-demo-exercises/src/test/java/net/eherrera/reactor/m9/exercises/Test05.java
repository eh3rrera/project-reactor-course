package net.eherrera.reactor.m9.exercises;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.Supplier;

public class Test05 {
    @Test
    void test() {
        // Create a Flux that emits a sequence of integers with a delay of 1 hour between each element
        Supplier<Flux<Integer>> integerFluxSupplier = () -> Flux.just(1, 2, 3)
                .delayElements(Duration.ofHours(1));

        // Use the withVirtualTime and thenAwait methods to create a test scenario for the Flux
        StepVerifier verifier = StepVerifier.withVirtualTime(integerFluxSupplier)
                .expectSubscription()
                .expectNoEvent(Duration.ofHours(1))
                .expectNext(1)
                .thenAwait(Duration.ofHours(1))
                .expectNext(2)
                .thenAwait(Duration.ofHours(1))
                .expectNext(3)
                .expectComplete();

        // Complete the test scenario and verify the results
        verifier.verify();
    }
}
