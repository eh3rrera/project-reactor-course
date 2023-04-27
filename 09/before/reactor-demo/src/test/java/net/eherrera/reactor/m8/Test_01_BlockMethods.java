package net.eherrera.reactor.m8;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_01_BlockMethods {
    @Test
    void example_01_Block() {
        Mono<Integer> myMono = Mono.just(1);
        Integer valueMono = myMono.block();
        System.out.println(valueMono);
    }

    @Test
    void example_02_BlockWithDuration() {
        Integer valueFlux = Flux.just(1, 2, 3)
                .delayElements(Duration.ofSeconds(1))
                .blockLast(Duration.ofMillis(1));
        System.out.println(valueFlux);
    }
}
