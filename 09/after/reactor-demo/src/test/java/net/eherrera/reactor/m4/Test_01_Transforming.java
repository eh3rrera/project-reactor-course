package net.eherrera.reactor.m4;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_01_Transforming {
    @Test
    void example_01_Cast() {
        Flux<Number> fluxNumber = Flux.just(1L, 2L, 3L);
        Flux<Long> fluxLong = fluxNumber.cast(Long.class);
        fluxLong.subscribe(System.out::println);
    }

    @Test
    void example_02_Cast_Invalid() {
        Flux<Number> fluxString = Flux.just(1L, 2L, 3L);
        Flux<Integer> fluxInteger = fluxString.cast(Integer.class);
        fluxInteger.subscribe(System.out::println);
    }

    @Test
    void example_03_flatMap() {
        Flux<Integer> fluxInt = Flux.just(1, 2, 3, 4);
        Flux<Integer> fluxFlat = fluxInt
                .flatMap(i -> transformAsyncPublisherDelay(i));
        fluxFlat.subscribe(System.out::println);
    }

    @Test
    void example_04_flatMapSequential() {
        Flux<Integer> fluxInt = Flux.just(1, 2, 3, 4);
        Flux<Integer> fluxFlat = fluxInt
                .flatMapSequential(i -> transformAsyncPublisherDelay(i));
        fluxFlat.subscribe(System.out::println);
    }

    @Test
    void example_05_concatMap() {
        Flux<Integer> fluxInt = Flux.just(1, 2, 3, 4);
        Flux<Integer> fluxConcat = fluxInt
                .concatMap(i -> transformAsyncPublisherDelay(i));
        fluxConcat.subscribe(System.out::println);
    }

    @Test
    void example_06_switchMap() {
        Flux<Integer> fluxInt = Flux.just(1, 2, 3, 4);
        Flux<Integer> fluxSwitch = fluxInt
                .switchMap(i -> transformAsyncPublisherDelay(i));
        fluxSwitch.subscribe(System.out::println);
    }

    public Publisher<Integer> transformAsyncPublisherDelay(int i) {
        return i % 2 == 0 ? Flux.just(i, i+1).delayElements(Duration.ofMillis(1)) : Mono.just(i * 10);
    }
}
