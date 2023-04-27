package net.eherrera.reactor.m5;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_01_IntroErrors {
    @Test
    void example_01_Exception() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .map(i -> i/(i-3))
                .map(i -> i*-1)
                .subscribe(System.out::println);
    }

    @Test
    void example_02_Error() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .filter(i -> i > 10)
                .switchIfEmpty(Flux.error(new RuntimeException("List must not be empty")))
                .subscribe(System.out::println);
    }

    @Test
    void example_03_ErrorConsumer() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .map(i -> i/(i-3))
                .map(i -> i*-1)
                .subscribe(System.out::println,
                        System.out::println
                );
    }
}
