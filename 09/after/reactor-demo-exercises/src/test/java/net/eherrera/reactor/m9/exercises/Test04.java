package net.eherrera.reactor.m9.exercises;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class Test04 {
    @Test
    void test() {
        // Create a Flux that emits a sequence of integers from 1 to 3
        Flux<Integer> integerFlux = Flux.just(1, 2, 3);

        // Use StepVerifier to create a test scenario for the Flux
        StepVerifier verifier = StepVerifier.create(integerFlux)
                .expectNext(1,2,3)
                .expectComplete();

        // Get an instance of StepVerifier.Assertions using the verifyThenAssertThat method
        StepVerifier.Assertions assertions = verifier.verifyThenAssertThat();

        // Make assertions on the final state of the subscriber
        assertions.hasNotDroppedElements()
                .hasNotDroppedErrors()
                .hasNotDiscardedElements();
    }
}
