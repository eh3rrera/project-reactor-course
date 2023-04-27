package net.eherrera.reactor.m9;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;
import reactor.util.context.Context;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_04_Context {
    private final String KEY = "myKey";

    @Test
    void example_01_NoContext() {
        StepVerifier
                .create(getFlux())
                .expectNoAccessibleContext()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void example_02_Context() {
        StepVerifier
                .create(getFlux())
                .expectAccessibleContext()
                .then()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void example_03_Context() {
        StepVerifier
                .create(getFlux())
                .expectAccessibleContext()
                .assertThat(System.out::println)
                .then()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void example_04_Context() {
        StepVerifier
                .create(getFlux())
                .expectAccessibleContext()
                .hasKey(KEY)
                .then()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void example_05_Context() {
        StepVerifier
                .create(getFlux(),
                        StepVerifierOptions
                                .create()
                                .withInitialContext(Context.of(KEY, 10))
                )
                .expectAccessibleContext()
                .hasKey(KEY)
                .then()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void example_06_Context() {
        StepVerifier
                .create(getFlux(),
                        StepVerifierOptions
                                .create()
                                .withInitialContext(Context.of(KEY, 10))
                )
                .expectAccessibleContext()
                .hasKey(KEY)
                .then()
                .expectNext(10, 20, 30, 40)
                .verifyComplete();
    }

    Flux<Integer> getFlux() {
        return Flux.just(1, 2, 3, 4)
                .transformDeferredContextual(
                        (flux, ctx) ->
                                flux.map(i -> i * ctx.getOrDefault(KEY, 1))
                );
    }
}
