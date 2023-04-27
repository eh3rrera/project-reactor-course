package net.eherrera.reactor.m3;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_03_FlatMap {
    public Mono<Integer> transformAsync(int i) {
        return i % 2 == 0 ? Mono.just(i) : Mono.just(i * 10);
    }

    @Test
    void example_01_MonoFlatMap() {
        Mono<Integer> monoInt = Mono.just(1);

        Mono<String> monoFlat = monoInt
                .flatMap(i -> Mono.just("i: " + i));

        // Remember, nothing happens until you subscribe
        monoFlat.subscribe(System.out::println);
    }

    @Test
    void example_02_FluxFlatMap() {
        Flux<Integer> fluxInt = Flux.just(1, 2, 3, 4);

        Flux<Integer> fluxFlat = fluxInt
                .flatMap(i -> transformAsyncMono(i));

        fluxFlat.subscribe(System.out::println);
    }

    @Test
    void example_03_FluxFlatMap() {
        Flux<Integer> fluxInt = Flux.just(1, 2, 3, 4);

        Flux<Integer> fluxFlat = fluxInt
                .flatMap(i -> transformAsyncPublisher(i));

        fluxFlat.subscribe(System.out::println);
    }

    @Test
    void example_04_FluxFlatMap() {
        Flux<Integer> fluxInt = Flux.just(1, 2, 3, 4);

        Flux<Integer> fluxFlat = fluxInt
                .flatMap(i -> transformAsyncPublisherDelay(i));

        fluxFlat.subscribe(System.out::println);
    }

    @Test
    void example_05_FluxFlatMapSequential() {
        Flux<Integer> fluxInt = Flux.just(1, 2, 3, 4);

        Flux<Integer> fluxFlat = fluxInt
                .flatMapSequential(i -> transformAsyncPublisherDelay(i));

        fluxFlat.subscribe(System.out::println);
    }

    @Test
    void example_06_FluxFlatMap() {
        Flux<Integer> fluxInt = Flux.just(1, 2, 3, 4);

        Flux<Integer> fluxFlat = fluxInt
                .flatMap(i -> transformAsyncPublisher(i), 2, 3);

        fluxFlat.log().subscribe(System.out::println);
    }

    public Mono<Integer> transformAsyncMono(int i) {
        return i % 2 == 0 ? Mono.just(i) : Mono.just(i * 10);
    }

    public Publisher<Integer> transformAsyncPublisher(int i) {
        return i % 2 == 0 ? Flux.just(i, i+1) : Mono.just(i * 10);
    }

    public Publisher<Integer> transformAsyncPublisherDelay(int i) {
        return i % 2 == 0 ? Flux.just(i, i+1).delayElements(Duration.ofMillis(1)) : Mono.just(i * 10);
    }
}
