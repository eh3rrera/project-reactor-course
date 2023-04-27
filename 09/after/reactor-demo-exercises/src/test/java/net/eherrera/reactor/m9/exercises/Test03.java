package net.eherrera.reactor.m9.exercises;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.Predicate;

public class Test03 {
    @Test
    void test() {
        // Create a Flux that emits a sequence of integers
        // and then throws an IllegalStateException
        Flux<Integer> integerFlux = Flux.just(1, 2, 3)
                .concatWith(Flux.error(new IllegalStateException("Test exception")));

        // Define a predicate that checks if the error is an instance of IllegalStateException
        Predicate<Throwable> isIllegalStateException = error -> error instanceof IllegalStateException;

        // Use StepVerifier to create a test scenario with the verifyErrorMatches method
        StepVerifier.create(integerFlux)
                .expectNext(1, 2, 3)
                .verifyErrorMatches(isIllegalStateException);
    }
}
