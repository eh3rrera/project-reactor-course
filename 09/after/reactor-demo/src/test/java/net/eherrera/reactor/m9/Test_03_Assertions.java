package net.eherrera.reactor.m9;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_03_Assertions {
    @Test
    void example_01_Assertions() {
        StepVerifier
                .create(getFlux())
                .expectNextCount(2)
                .expectComplete()
                .verifyThenAssertThat()
                .hasNotDroppedElements()
                .tookLessThan(Duration.ofSeconds(3));
    }

    Flux<Integer> getFlux() {
        return Flux.just(1, 2, 3, 4)
                .delayElements(Duration.ofMillis(500))
                .filter(i -> i % 2 == 0);
    }
}
