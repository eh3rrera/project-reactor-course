package net.eherrera.reactor.m9.exercises;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;

public class Test06 {
    @Test
    void test() {
        // Create a Flux that emits a sequence of integers from 1 to 5
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);

        // Create a PublisherProbe instance and use it to wrap the Flux
        PublisherProbe<Integer> probe = PublisherProbe.of(integerFlux);

        // Use the StepVerifier to create a test scenario for the wrapped Flux,
        // canceling the subscription after receiving the first element
        StepVerifier verifier = StepVerifier.create(probe.flux())
                .expectNext(1)
                .thenCancel();

        // Run the test scenario
        verifier.verify();

        // Use the PublisherProbe to assert that the probe was canceled
        probe.assertWasCancelled();
    }
}
