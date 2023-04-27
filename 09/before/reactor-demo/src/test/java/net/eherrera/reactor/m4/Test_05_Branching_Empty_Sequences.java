package net.eherrera.reactor.m4;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_05_Branching_Empty_Sequences {
    @Test
    void example_01_defaultIfEmpty() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .filter(i -> i > 10)
                .defaultIfEmpty(-1)
                .subscribe(System.out::println);
    }

    @Test
    void example_02_defaultIfEmpty() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .filter(i -> i > 4)
                .defaultIfEmpty(-1)
                .subscribe(System.out::println);
    }

    @Test
    void example_03_switchIfEmpty() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .filter(i -> i > 10)
                .switchIfEmpty(Flux.just(-1, 0, 99))
                .subscribe(System.out::println);
    }

    @Test
    void example_04_switchIfEmpty() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .filter(i -> i > 4)
                .switchIfEmpty(Flux.just(-1, 0, 99))
                .subscribe(System.out::println);
    }
}
