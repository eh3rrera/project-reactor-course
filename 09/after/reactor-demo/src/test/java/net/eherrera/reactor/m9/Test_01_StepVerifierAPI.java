package net.eherrera.reactor.m9;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_01_StepVerifierAPI {
    @Test
    void example_01_SimpleExample() {
        StepVerifier
                .create(getFlux())              // 1
                .expectNext(2)                  // 2
                .expectNextMatches(i -> i == 4) // 3
                .expectComplete()               // 4
                .verify();                      // 5
    }

    @Test
    void example_02_StepVerifierOptions() {
        StepVerifier
                .create(getFlux(),
                        StepVerifierOptions.create().scenarioName("my-example"))
                .expectNext(2)
                //.expectNextMatches(i -> i == 4)
                .expectComplete()
                .verify();
    }
    @Test
    void example_03_VerifyComplete() {
        StepVerifier
                .create(getFlux())
                .expectNext(2)
                .expectNextMatches(i -> i == 4)
                .verifyComplete();
    }

    Flux<Integer> getFlux() {
        return Flux.just(1, 2, 3, 4)
                .filter(i -> i % 2 == 0);
    }
}
