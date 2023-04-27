package net.eherrera.reactor.m9.exercises;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.function.Predicate;

public class Test02 {
    @Test
    void test() {
        // Create a Flux that emits a sequence of integers from 1 to 3
        Flux<Integer> integerFlux = Flux.just(1, 2, 3);

        // Define a predicate that checks if a number is even
        Predicate<Integer> isEven = n -> n % 2 == 0;

        // Use StepVerifier to create a test scenario with the expectNextMatches method
        StepVerifier.create(integerFlux)
                .expectNext(1)
                .expectNextMatches(isEven)
                .expectNext(3)
                .verifyComplete();
    }
}
