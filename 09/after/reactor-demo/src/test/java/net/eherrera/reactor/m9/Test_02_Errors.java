package net.eherrera.reactor.m9;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_02_Errors {
    @Test
    void example_01_ExpectError() {
        StepVerifier
                .create(getFlux())
                .expectNext(2)
                .expectError()
                .verify();
    }

    @Test
    void example_02_ExpectErrorType() {
        StepVerifier
                .create(getFlux())
                .expectNext(2)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void example_03_ExpectErrorMessage() {
        StepVerifier
                .create(getFlux())
                .expectNext(2)
                //.expectErrorMessage("Invalid number: ") //Bad
                .expectErrorMessage("Invalid number: 4")  // Good
                .verify();
    }

    @Test
    void example_04_ExpectErrorMessage() {
        StepVerifier
                .create(getFlux())
                .expectNext(2)
                .expectErrorMatches( e -> e.getMessage().startsWith("Invalid number:"))
                .verify();
    }

    @Test
    void example_05_VerifyErrorMessage() {
        StepVerifier
                .create(getFlux())
                .expectNext(2)
                .verifyErrorMatches( e -> e.getMessage().startsWith("Invalid number:"));
    }

    Flux<Integer> getFlux() {
        return Flux.just(1, 2, 3, 4)
                .filter(i -> i % 2 == 0)
                .map(i -> {
                    if(i > 2)
                        throw new RuntimeException("Invalid number: " + i);
                    return i;
                });
    }
}
