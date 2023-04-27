package net.eherrera.reactor.m9;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_06_PublisherProbe {
    @Test
    void example_01_FilterReturnsFlux() {
        StepVerifier.create(
                    processFlux(
                            Flux.just(5, 12)
                    )
                )
                .expectNext(12)
                .verifyComplete();
    }

    @Test
    void example_02_FilterDoesntReturnFlux() {
        StepVerifier.create(
                        processFlux(
                                Flux.just(5)
                        )
                )
                .expectNext(0)
                .verifyComplete();
    }

    @Test
    void example_03_FallbackPublisherProbe() {
        PublisherProbe<Integer> fallbackProbe = PublisherProbe.of(Mono.just(0));
        StepVerifier.create(
                        processMono(
                                Mono.empty(),
                                fallbackProbe.mono()
                        )
                )
                .verifyComplete();
        fallbackProbe.assertWasSubscribed();
        fallbackProbe.assertWasRequested();
        fallbackProbe.assertWasNotCancelled(); // Optional
    }

    @Test
    void example_04_BothPathsPublisherProbe() {
        PublisherProbe<Integer> integerProbe = PublisherProbe.of(Mono.just(1));
        PublisherProbe<Integer> fallbackProbe = PublisherProbe.of(Mono.just(0));
        StepVerifier.create(
                        processMono(
                                integerProbe.mono(),
                                fallbackProbe.mono()
                        )
                )
                .verifyComplete();
        integerProbe.assertWasSubscribed();
        integerProbe.assertWasRequested();
        fallbackProbe.assertWasNotSubscribed();
        fallbackProbe.assertWasNotRequested();
    }

    Flux<Integer> processFlux(Flux<Integer> flux) {
        return flux
                .filter(i -> i > 10)
                .defaultIfEmpty(0);
    }

    Mono<Void> processMono(Mono<Integer> mono, Mono<Integer> fallback) {
        return mono
                .flatMap(i -> Mono.just(i*10))
                .switchIfEmpty(fallback)
                .flatMap(i -> Mono.fromRunnable(() -> System.out.println("Do something with: " + i)));
    }
}
