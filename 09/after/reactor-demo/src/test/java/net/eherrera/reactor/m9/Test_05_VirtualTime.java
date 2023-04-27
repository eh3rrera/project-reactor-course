package net.eherrera.reactor.m9;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_05_VirtualTime {
    @Test
    void example_01_ThenAwait() {
        Duration d = StepVerifier
                .withVirtualTime(() -> getFlux())
                .thenAwait(Duration.ofHours(20))
                .expectNextCount(4)
                .verifyComplete();
        System.out.println(d.toMillis());
    }

    @Test
    void example_02_ExpectNoEvent() {
        StepVerifier
                .withVirtualTime(() -> getFlux())
                .expectSubscription()
                .expectNoEvent(Duration.ofHours(5))
                .thenAwait(Duration.ofHours(20))
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void example_03_VirtualTime() {
        StepVerifier
                .withVirtualTime(() -> getFlux())
                .thenAwait(Duration.ofHours(20))
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    void example_04_VirtualTime() {
        List<Integer> list = IntStream.rangeClosed(1, 4)
                .boxed()
                .collect(Collectors.toList());

        StepVerifier
                .withVirtualTime(() -> getFlux())
                .thenAwait(Duration.ofHours(20))
                .expectNextSequence(list)
                .verifyComplete();
    }

    @Test
    void example_05_VirtualTime() {
        List<Integer> list = IntStream.rangeClosed(1, 4)
                .boxed()
                .collect(Collectors.toList());

        StepVerifier
                .withVirtualTime(() -> getFlux())
                .expectSubscription()
                .expectNoEvent(Duration.ofHours(20))
                .expectNextSequence(list)
                .verifyComplete();
    }

    Flux<Integer> getFlux() {
        return Flux.just(1, 2, 3, 4)
                .delayElements(Duration.ofHours(5));
    }
}
